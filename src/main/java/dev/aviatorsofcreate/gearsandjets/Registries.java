package dev.aviatorsofcreate.gearsandjets;

import dev.aviatorsofcreate.gearsandjets.fuel_type.FuelType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class Registries {
    public static final ResourceKey<Registry<FuelType>> FUEL_TYPE = key("fuel_type");

    private static <T> ResourceKey<Registry<T>> key(String name) {
        return ResourceKey.createRegistryKey(CreateGearsandJets.location(name));
    }
}
