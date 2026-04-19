package dev.aviatorsofcreate.gearsandjets.blockentity;

import java.util.List;

import dev.aviatorsofcreate.gearsandjets.block.CombustionChamberBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class CombustionChamberBlockEntity extends BlockEntity {
    private static final List<TagKey<Fluid>> ACCEPTED_FUEL_TAGS = List.of(
            fuelTag("forge:fuel"),
            fuelTag("c:fuel"),
            fuelTag("forge:diesel"),
            fuelTag("forge:biodiesel"),
            fuelTag("forge:gasoline"),
            fuelTag("forge:ethanol"),
            fuelTag("forge:plantoil"),
            fuelTag("c:diesel"),
            fuelTag("c:biodiesel"),
            fuelTag("c:gasoline"),
            fuelTag("c:ethanol"),
            fuelTag("c:plantoil")
    );
    private final FluidTank tank = new ConfigurableFluidTank();

    public CombustionChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public FluidTank getFluidHandler() {
        return tank;
    }

    public int getTankCapacity() {
        if (getBlockState().getBlock() instanceof CombustionChamberBlock combustionChamberBlock) {
            return combustionChamberBlock.getTankCapacity();
        }

        return 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tank.readFromNBT(registries, tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tank.writeToNBT(registries, tag);
    }

    private class ConfigurableFluidTank extends FluidTank {
        private ConfigurableFluidTank() {
            super(0);
        }

        @Override
        public int getCapacity() {
            syncCapacity();
            return super.getCapacity();
        }

        @Override
        public int getTankCapacity(int tank) {
            syncCapacity();
            return super.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            for (TagKey<Fluid> fuelTag : ACCEPTED_FUEL_TAGS) {
                if (stack.is(fuelTag)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            syncCapacity();
            return super.fill(resource, action);
        }

        @Override
        protected void onContentsChanged() {
            setChanged();

            if (level != null) {
                BlockState state = getBlockState();
                level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
            }
        }

        private void syncCapacity() {
            setCapacity(CombustionChamberBlockEntity.this.getTankCapacity());
        }
    }

    private static TagKey<Fluid> fuelTag(String id) {
        return TagKey.create(Registries.FLUID, ResourceLocation.parse(id));
    }
}
