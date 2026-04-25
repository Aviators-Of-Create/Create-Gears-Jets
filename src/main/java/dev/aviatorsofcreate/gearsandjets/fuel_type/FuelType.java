package dev.aviatorsofcreate.gearsandjets.fuel_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;

public record FuelType(HolderSet<Fluid> fluid, PerEngineProperties basic, float soundPitchMultiplier) {

    public static final Codec<FuelType> CODEC = RecordCodecBuilder.create(i -> i.group(
            RegistryCodecs.homogeneousList(Registries.FLUID).fieldOf("fluid").forGetter(FuelType::fluid),
            PerEngineProperties.CODEC.fieldOf("basic").forGetter(FuelType::basic),
            Codec.FLOAT.optionalFieldOf("pitch_multiplier", 1f).forGetter(FuelType::soundPitchMultiplier)
    ).apply(i, FuelType::new));

    public static final Codec<FuelType> NCODEC = RecordCodecBuilder.create(i -> i.group(
            RegistryCodecs.homogeneousList(Registries.FLUID).fieldOf("fluid").forGetter(FuelType::fluid),
            PerEngineProperties.CODEC.fieldOf("basic").forGetter(FuelType::basic),
            Codec.FLOAT.optionalFieldOf("pitch_multiplier", 1f).forGetter(FuelType::soundPitchMultiplier)
    ).apply(i, FuelType::new));

    public record PerEngineProperties(float thrust, float burn, float startupCost) {
        public static final Codec<PerEngineProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.FLOAT.fieldOf("thrust").forGetter(PerEngineProperties::thrust),
                Codec.FLOAT.fieldOf("burn_rate").forGetter(PerEngineProperties::burn),
                Codec.FLOAT.fieldOf("startup_cost").forGetter(PerEngineProperties::startupCost)
        ).apply(i, PerEngineProperties::new));
    }
}