package dev.aviatorsofcreate.gearsandjets.blockentity;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.aviatorsofcreate.gearsandjets.block.CombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.block.ExhaustBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class CombustionChamberBlockEntity extends SmartBlockEntity {

    float fuelTicks = 0;
    private SmartFluidTankBehaviour tank;
    int signal = 0;

    public CombustionChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        int fluidRemoved = 100 * redstone / 15;
        if (tankBehaviour.getPrimaryHandler().getFluidAmount() < fluidRemoved) {
            return false;
        }
        tankBehaviour.getPrimaryHandler().drain(fluidRemoved, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    private void emitExhaustParticles(ServerLevel level) {
        BlockState chamberState = this.getBlockState();
        if (!chamberState.hasProperty(CombustionChamberBlock.FACING)) {
            return;
        }

        Direction chamberFacing = chamberState.getValue(CombustionChamberBlock.FACING);
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
        double x = exhaustPos.getX() + 0.5D + dirX * 0.45D;
        double y = exhaustPos.getY() + 0.55D;
        double z = exhaustPos.getZ() + 0.5D + dirZ * 0.45D;

        level.sendParticles(ParticleTypes.CLOUD, x, y, z, 2, 0.04D, 0.03D, 0.04D, 0.01D);
        level.sendParticles(ParticleTypes.SMOKE, x, y, z, 1, 0.02D, 0.02D, 0.02D, 0.01D);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 500);
        behaviours.add(tank);
    }

    @Override
    public void tick() {
        Level level = this.getLevel();
        if (level != null && !level.isClientSide) {
            this.signal = level.getBestNeighborSignal(this.getBlockPos());

            if (this.signal > 0) {
                boolean burnedFuel = updateFuel(this.tank, this.signal);
                if (burnedFuel && level instanceof ServerLevel serverLevel && level.getGameTime() % 2L == 0L) {
                    emitExhaustParticles(serverLevel);
                }
            }

            BlockState state = this.getBlockState();
            boolean powered = this.signal > 0;
            if (state.hasProperty(CombustionChamberBlock.POWERED) && state.getValue(CombustionChamberBlock.POWERED) != powered) {
                level.setBlockAndUpdate(this.getBlockPos(), state.setValue(CombustionChamberBlock.POWERED, powered));
            }
        }

        super.tick();
    }
}
