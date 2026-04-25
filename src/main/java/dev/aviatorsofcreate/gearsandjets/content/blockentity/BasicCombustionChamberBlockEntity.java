package dev.aviatorsofcreate.gearsandjets.content.blockentity;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.aviatorsofcreate.gearsandjets.content.block.BasicCombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.content.block.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.interfaces.IEngine;
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

    private boolean updateFuel(SmartFluidTankBehaviour tankBehaviour, int redstone) {
        int fluidRemoved = redstone / 15;
        if (tankBehaviour.getPrimaryHandler().getFluidAmount() < fluidRemoved) {
            return false;
        }
        tankBehaviour.getPrimaryHandler().drain(fluidRemoved, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    private void updateThrust() {
        double thrust = 0;
        switch (this.getBlockState().getValue(MACHINE_STATE)) {
            case OFF:
                this.active = false;
                break;
            case IDLING:
                this.active = true;
                thrust = 200 * this.signal / ( 15 * 3.156925 );
                break;
            case RUNNING:
                this.active = true;
                thrust = 200 * Math.pow((double) this.signal / 15, 3.5);
                break;
            default:
                throw new IllegalStateException("Jet engine has invalid blockstate!");
        }
        this.thrust = thrust;
    }

    private void emitExhaustParticles(Level level, boolean running) {
        BlockState chamberState = this.getBlockState();
        if (!chamberState.hasProperty(BasicCombustionChamberBlock.FACING)) {
            return;
        }

        Direction chamberFacing = chamberState.getValue(BasicCombustionChamberBlock.FACING);
        BlockPos exhaustPos = this.getBlockPos().relative(chamberFacing.getOpposite());
        BlockState exhaustState = level.getBlockState(exhaustPos);
        if (!(exhaustState.getBlock() instanceof ExhaustBlock)
                || !exhaustState.hasProperty(ExhaustBlock.FACING)
                || exhaustState.getValue(ExhaustBlock.FACING) != chamberFacing.getOpposite()) {
            return;
        }

        Direction exhaustFacing = exhaustState.getValue(ExhaustBlock.FACING);
        double dirX = exhaustFacing.getStepX();
        double dirZ = exhaustFacing.getStepZ();
        double baseX = exhaustPos.getX() + 0.5D + dirX * 0.45D;
        double baseY = exhaustPos.getY() + 0.55D;
        double baseZ = exhaustPos.getZ() + 0.5D + dirZ * 0.45D;

        for (int i = 0; i < 4; i++) {
            double distance = i * 0.26D;
            double x = baseX + dirX * distance;
            double y = baseY + i * 0.015D;
            double z = baseZ + dirZ * distance;

            level.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0, 0, 0);
            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
        }

        for (int i = 0; i < 2; i++) {
            double distance = i * 0.2D;
            double x = baseX + dirX * distance;
            double y = baseY + 0.02D + i * 0.01D;
            double z = baseZ + dirZ * distance;
            level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 0, 0, 0);
        }

        level.addParticle(ParticleTypes.LARGE_SMOKE, baseX, baseY, baseZ, 0, 0, 0);
        level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, baseX, baseY, baseZ, 0, 0, 0);
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
        if (level != null && !level.isClientSide) {
            this.signal = level.getBestNeighborSignal(this.getBlockPos());
            boolean burnedFuel = updateFuel(this.tank, this.signal);


            BlockState state = this.getBlockState();
            boolean powered = this.signal > 0;
            boolean running = this.signal > 3;


            if (!powered && !burnedFuel) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, OFF));
            } else if (!running) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, IDLING));
                emitExhaustParticles(level, false);
            } else {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(MACHINE_STATE, RUNNING));
                emitExhaustParticles(level, true);
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
}
