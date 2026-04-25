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

    public CombustionChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public SmartFluidTank getFluidHandler() {
        return this.tank.getPrimaryHandler();
    }

    private void updateFuel(SmartFluidTankBehaviour tankBehaviour, int redstone) {
        int fluidRemoved = 100 * redstone / 15;
        if (tankBehaviour.getPrimaryHandler().getFluidAmount() < fluidRemoved) return;
        tankBehaviour.getPrimaryHandler().drain(fluidRemoved, IFluidHandler.FluidAction.EXECUTE);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        tank = SmartFluidTankBehaviour.single(this, 500);
    }

    @Override
    public void tick() {
        int signal = this.getLevel().getBestNeighborSignal(this.getBlockPos());

        if (!this.getLevel().isClientSide) {
            updateFuel(this.tank, signal);
            this.getLevel().setBlockAndUpdate(this.getBlockPos(), this.getBlockState().setValue(CombustionChamberBlock.POWERED, signal > 0));
        }

        super.tick();
    }
}
