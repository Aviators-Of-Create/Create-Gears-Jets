package dev.aviatorsofcreate.gearsandjets.fuel_type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.aviatorsofcreate.gearsandjets.content.blockentity.BasicCombustionChamberBlockEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    public static final FuelType EMPTY = new FuelType(null, new PerEngineProperties(0, 0, 0), 0);

    @SuppressWarnings("deprecation")
    public static FuelType getTypeFor(HolderLookup.RegistryLookup<FuelType> registry, Fluid fluid) {
        if (registry == null)
            return EMPTY;
        var type = registry.listElements()
                .filter(r -> r.value().fluid().contains(fluid.builtInRegistryHolder()))
                .findFirst();
        return type.isEmpty() ? EMPTY : type.get().value();
    }

    public PerEngineProperties getGeneratedData(BlockEntity blockEntity) {
        //if (blockEntity instanceof BasicCombustionChamberBlockEntity) return basic; // Going to be implemented once we add more engine types
        return basic;
    }

    public record PerEngineProperties(float thrust, int burn, int startupCost) {
        public static final Codec<PerEngineProperties> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.FLOAT.fieldOf("thrust").forGetter(PerEngineProperties::thrust),
                Codec.INT.fieldOf("burn_rate").forGetter(PerEngineProperties::burn),
                Codec.INT.fieldOf("startup_cost").forGetter(PerEngineProperties::startupCost)
        ).apply(i, PerEngineProperties::new));
    }
}