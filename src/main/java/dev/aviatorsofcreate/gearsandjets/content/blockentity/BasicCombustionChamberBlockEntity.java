package dev.aviatorsofcreate.gearsandjets.content.blockentity;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.aviatorsofcreate.gearsandjets.content.block.BasicCombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.content.block.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.block.IntakeBlock;
import dev.aviatorsofcreate.gearsandjets.content.interfaces.IEngine;
import dev.aviatorsofcreate.gearsandjets.client.EngineParticleBridge;
import dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller;
import dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import static dev.aviatorsofcreate.gearsandjets.content.block.BasicCombustionChamberBlock.MACHINE_STATE;
import static dev.aviatorsofcreate.gearsandjets.enums.MachineState.*;

public class BasicCombustionChamberBlockEntity extends SmartBlockEntity implements BlockEntitySubLevelPropellerActor, BlockEntityPropeller, IEngine {

    private SmartFluidTankBehaviour tank;
    private int signal = 0;
    private double thrust = 0;
    private boolean active = false;
    int remainingTicks = 0;

    public BasicCombustionChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SmartFluidTank getFluidHandler() {
        return this.tank.getPrimaryHandler();
    }

    public int getSignal() {
        return this.signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public boolean isPowered() {
        return signal > 0;
    }

    private int consumeFuel(SmartFluidTankBehaviour tankBehaviour, int redstone) {
        int fluidConsumed = getMaxBurnRate() * redstone / 15;
        if (fluidConsumed <= 0 || tankBehaviour.getPrimaryHandler().getFluidAmount() < fluidConsumed) {
            return 0;
        }
        tankBehaviour.getPrimaryHandler().drain(fluidConsumed, IFluidHandler.FluidAction.EXECUTE);
        return fluidConsumed;
    }

    private void updateThrust() {
        this.thrust = calculateThrust(this.signal);
        this.active = this.thrust > 0;
    }

    private double calculateThrust(int redstoneSignal) {
        if (redstoneSignal <= 0 || !validFS()) {
            return 0;
        }

        float multiplier = getThrustMultiplier();
        return switch (this.getBlockState().getValue(MACHINE_STATE)) {
            case OFF -> 0;
            case IDLING -> multiplier * redstoneSignal / (15 * 3.156925);
            case RUNNING -> multiplier * Math.pow((double) redstoneSignal / 15, 3.5);
        };
    }

    private double calculateAirflow(int redstoneSignal) {
        return calculateThrust(redstoneSignal) / 3;
    }

    private AttachedJetPartData getAttachedIntake(Level level) {
        BlockState chamberState = this.getBlockState();
        if (!chamberState.hasProperty(BasicCombustionChamberBlock.FACING)) {
            return null;
        }

        Direction chamberFacing = chamberState.getValue(BasicCombustionChamberBlock.FACING);
        BlockPos intakePos = this.getBlockPos().relative(chamberFacing);
        BlockState intakeState = level.getBlockState(intakePos);
        if (!(intakeState.getBlock() instanceof IntakeBlock)
                || !intakeState.hasProperty(IntakeBlock.FACING)
                || intakeState.getValue(IntakeBlock.FACING) != chamberFacing) {
            return null;
        }

        return new AttachedJetPartData(intakePos, intakeState.getValue(IntakeBlock.FACING));
    }

    private ExhaustEmissionData getAttachedExhaust(ServerLevel level) {
        BlockState chamberState = this.getBlockState();
        if (!chamberState.hasProperty(BasicCombustionChamberBlock.FACING)) {
            return null;
        }

        Direction chamberFacing = chamberState.getValue(BasicCombustionChamberBlock.FACING);
        BlockPos exhaustPos = this.getBlockPos().relative(chamberFacing.getOpposite());
        BlockState exhaustState = level.getBlockState(exhaustPos);
        if (!(exhaustState.getBlock() instanceof ExhaustBlock)
                || !exhaustState.hasProperty(ExhaustBlock.FACING)
                || exhaustState.getValue(ExhaustBlock.FACING) != chamberFacing.getOpposite()) {
            return null;
        }

        return new ExhaustEmissionData(exhaustPos, exhaustState.getValue(ExhaustBlock.FACING));
    }

    private void emitIntakeParticles(Level level) {
        AttachedJetPartData intakeData = getAttachedIntake(level);
        if (intakeData == null || this.getBlockState().getValue(MACHINE_STATE) == OFF) {
            return;
        }

        double airflow = Math.abs(calculateAirflow(this.signal));
        if (airflow <= 0) {
            return;
        }

        EngineParticleBridge.spawnIntakeParticles(level, intakeData.pos(), intakeData.facing(), airflow);
    }

    private void emitExhaustParticles(ServerLevel level, int fuelConsumed) {
        if (fuelConsumed <= 0) {
            return;
        }

        ExhaustEmissionData exhaustData = getAttachedExhaust(level);
        if (exhaustData == null) {
            return;
        }

        Direction exhaustFacing = exhaustData.facing();
        BlockPos exhaustPos = exhaustData.pos();
        double dirX = exhaustFacing.getStepX();
        double dirZ = exhaustFacing.getStepZ();
        double baseX = exhaustPos.getX() + 0.5D + dirX * 0.45D;
        double baseY = exhaustPos.getY() + 0.55D;
        double baseZ = exhaustPos.getZ() + 0.5D + dirZ * 0.45D;
        int segments = Math.max(1, Math.min(4, fuelConsumed));
        int largeSmokeCount = Math.max(1, Math.min(4, fuelConsumed / 2));
        int smokeCount = Math.max(1, Math.min(3, fuelConsumed / 3));
        int signalSmokeCount = Math.max(1, Math.min(2, fuelConsumed / 5));
        double spreadScale = 0.05D + Math.min(fuelConsumed, 10) * 0.01D;
        double speedScale = 0.006D + Math.min(fuelConsumed, 10) * 0.0008D;

        for (int i = 0; i < segments; i++) {
            double distance = i * 0.26D;
            double x = baseX + dirX * distance;
            double y = baseY + i * 0.015D;
            double z = baseZ + dirZ * distance;

            level.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, largeSmokeCount, spreadScale, 0.04D, spreadScale, speedScale);
            level.sendParticles(ParticleTypes.SMOKE, x, y, z, smokeCount, spreadScale * 0.65D, 0.03D, spreadScale * 0.65D, speedScale * 0.85D);
        }

        for (int i = 0; i < Math.min(2, segments); i++) {
            double distance = i * 0.2D;
            double x = baseX + dirX * distance;
            double y = baseY + 0.02D + i * 0.01D;
            double z = baseZ + dirZ * distance;
            level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, signalSmokeCount, spreadScale * 0.3D, 0.02D, spreadScale * 0.3D, speedScale);
        }

        level.sendParticles(ParticleTypes.LARGE_SMOKE, baseX, baseY, baseZ, largeSmokeCount + 1, spreadScale + 0.02D, 0.06D, spreadScale + 0.02D, speedScale + 0.001D);
        level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, baseX, baseY, baseZ, signalSmokeCount, spreadScale * 0.4D, 0.025D, spreadScale * 0.4D, speedScale);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 500);
        behaviours.add(tank);
    }

    public BlockEntityPropeller getPropeller() {
        return this;
    }

    @Override
    public Direction getBlockDirection() {
        return this.getBlockState().getValue(BasicCombustionChamberBlock.FACING).getOpposite();
    }

    @Override
    public double getAirflow() {
        return this.thrust / 3;
    }

    @Override
    public double getThrust() {
        return this.thrust;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void tick() {
        Level level = this.getLevel();
        if (level != null) {
            this.signal = level.getBestNeighborSignal(this.getBlockPos());
        }

        if (level != null && level.isClientSide) {
            emitIntakeParticles(level);
            updateThrust();
        } else if (level != null) {
            int fuelConsumed = consumeFuel(this.tank, this.signal);
            boolean burnedFuel = fuelConsumed > 0;
            if (burnedFuel && level instanceof ServerLevel serverLevel) {
                emitExhaustParticles(serverLevel, fuelConsumed);
            }


            BlockState state = this.getBlockState();
            boolean powered = this.signal > 0;
            boolean running = this.signal > 3;


            if (!powered && !burnedFuel) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, OFF));
            } else if (!running) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, IDLING));
            } else {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, RUNNING));
            }

            if (state.hasProperty(BasicCombustionChamberBlock.POWERED) && state.getValue(BasicCombustionChamberBlock.POWERED) != powered) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(BasicCombustionChamberBlock.POWERED, powered));
            }
            updateThrust();
        }

        super.tick();
    }

    @Override
    public int getRemainingTicks() {
        return remainingTicks;
    }

    @Override
    public SmartBlockEntity self() {
        return this;
    }

    @Override
    public FluidTank getTank() {
        return tank.getPrimaryHandler();
    }

    private record ExhaustEmissionData(BlockPos pos, Direction facing) {
    }

    private record AttachedJetPartData(BlockPos pos, Direction facing) {
    }
}
