package dev.aviatorsofcreate.gearsandjets.blockentity;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import dev.aviatorsofcreate.gearsandjets.block.CombustionChamberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

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

    private void updateFuel(SmartFluidTankBehaviour tankBehaviour, int redstone) {
        int fluidRemoved = 100 * redstone / 15;
        if (tankBehaviour.getPrimaryHandler().getFluidAmount() < fluidRemoved) return;
        tankBehaviour.getPrimaryHandler().drain(fluidRemoved, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 500);
        behaviours.add(tank);
    }

    @Override
    public void tick() {
        if (!this.getLevel().isClientSide && this.signal > 0 ) {
            updateFuel(this.tank, this.signal);
            BlockState state = this.getBlockState();
            boolean powered = this.signal > 0;
            if (state.hasProperty(CombustionChamberBlock.POWERED) && state.getValue(CombustionChamberBlock.POWERED) != powered) {
                this.getLevel().setBlockAndUpdate(this.getBlockPos(), state.setValue(CombustionChamberBlock.POWERED, powered));
            }
        }

        super.tick();
    }
}
