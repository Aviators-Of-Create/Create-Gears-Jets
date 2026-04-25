package dev.aviatorsofcreate.gearsandjets.content.interfaces;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.aviatorsofcreate.gearsandjets.Registries;
import dev.aviatorsofcreate.gearsandjets.enums.MachineState;
import dev.aviatorsofcreate.gearsandjets.fuel_type.FuelType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import static dev.aviatorsofcreate.gearsandjets.content.block.BasicCombustionChamberBlock.MACHINE_STATE;

public interface IEngine {

    default boolean enabled() {
        if (validFS()) {
            return (self().getBlockState().getValue(MACHINE_STATE) != MachineState.OFF);
        }
        return false;
    }

    default boolean validFS() {
        if (fs().isEmpty()) return false;
        return FuelType.getTypeFor(self().getLevel().registryAccess().lookupOrThrow(Registries.FUEL_TYPE), fs().getFluid()) != FuelType.EMPTY;
    }

    default FluidStack fs() {
        return getTank().getFluid();
    }

    default float getThrustMultiplier() {
        return FuelType.getTypeFor(self().getLevel().registryAccess().lookupOrThrow(Registries.FUEL_TYPE), fs().getFluid()).getGeneratedData(self()).thrust();
    }

    default int getStartupCost() {
        return FuelType.getTypeFor(self().getLevel().registryAccess().lookupOrThrow(Registries.FUEL_TYPE), fs().getFluid()).getGeneratedData(self()).startupCost();
    }

    default int getMaxBurnRate() {
        return FuelType.getTypeFor(self().getLevel().registryAccess().lookupOrThrow(Registries.FUEL_TYPE), fs().getFluid()).getGeneratedData(self()).burn();
    }

    default float getPitchMultiplier() {
        return FuelType.getTypeFor(self().getLevel().registryAccess().lookupOrThrow(Registries.FUEL_TYPE), fs().getFluid()).soundPitchMultiplier();
    }

    int getRemainingTicks();

    SmartBlockEntity self();

    FluidTank getTank();
}
