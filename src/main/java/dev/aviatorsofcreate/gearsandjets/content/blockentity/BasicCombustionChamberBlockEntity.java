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
        return this.signal > 0;
    }

    private int updateFuel(SmartFluidTankBehaviour tankBehaviour, int redstone) {
        FluidTank tank = tankBehaviour.getPrimaryHandler();
        int fluidConsumed = Math.min(getMaxBurnRate() * redstone / 15, tank.getFluidAmount());
        if (fluidConsumed == 0) {
            return 0;
        }
        tank.drain(fluidConsumed, IFluidHandler.FluidAction.EXECUTE);
        return fluidConsumed;
    }

    private void updateThrust() {
        double thrust = 0;
        float multiplier = getThrustMultiplier();
        switch (this.getBlockState().getValue(MACHINE_STATE)) {
            case OFF:
                this.active = false;
                break;
            case IDLING:
                this.active = true;
                thrust = multiplier * this.signal / ( 15 * 3.156925 );
                break;
            case RUNNING:
                this.active = true;
                thrust = multiplier * Math.pow((double) this.signal / 15, 3.5);
                break;
            default:
                throw new IllegalStateException("Jet engine has invalid blockstate!");
        }
        this.thrust = thrust;
    }

    private double calculateAirflow() {
        return this.thrust / 3;
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

        double airflow = Math.abs(calculateAirflow());
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
        int segments = Math.clamp(fuelConsumed, 1, 4);
        int largeSmokeCount = Math.clamp(fuelConsumed / 2, 1, 4);
        int signalSmokeCount = Math.clamp(fuelConsumed / 5, 1, 2);
        double spreadScale = 0.05D + Math.min(fuelConsumed, 10) * 0.01D;
        double speedScale = 0.006D + Math.min(fuelConsumed, 10) * 0.0008D;

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

        updateThrust();

        if (level != null && level.isClientSide) {
            emitIntakeParticles(level);
        } else if (level != null) {
            int fuelConsumed = updateFuel(this.tank, this.signal);
            boolean burnedFuel = fuelConsumed > 0;
            if (burnedFuel && level instanceof ServerLevel serverLevel) {
                emitExhaustParticles(serverLevel, fuelConsumed);
            }

            BlockState state = this.getBlockState();
            boolean powered = this.signal > 0;
            boolean running = this.signal > 3;

            if (!powered) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, OFF));
            } else if (!running) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, IDLING));
            } else {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, RUNNING));
            }

            if (state.hasProperty(BasicCombustionChamberBlock.POWERED) && state.getValue(BasicCombustionChamberBlock.POWERED) != powered) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(BasicCombustionChamberBlock.POWERED, powered));
            }
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
    public SmartFluidTank getTank() {
        return tank.getPrimaryHandler();
    }

    private record ExhaustEmissionData(BlockPos pos, Direction facing) {
    }

    private record AttachedJetPartData(BlockPos pos, Direction facing) {
    }
}
