package dev.aviatorsofcreate.gearsandjets.registry;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.block.ModBlocks;
import dev.aviatorsofcreate.gearsandjets.blockentity.CombustionChamberBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateGearsandJets.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CombustionChamberBlockEntity>> COMBUSTION_CHAMBER =
            BLOCK_ENTITY_TYPES.register(
                    "combustion_chamber",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new CombustionChamberBlockEntity(getCombustionChamberType(), pos, state),
                            ModBlocks.SIMPLE_COMBUSTION_CHAMBER.get()
                    ).build(null)
            );

    private ModBlockEntityTypes() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
        eventBus.addListener(ModBlockEntityTypes::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                COMBUSTION_CHAMBER.get(),
                (blockEntity, side) -> blockEntity.getFluidHandler()
        );
    }

    private static BlockEntityType<CombustionChamberBlockEntity> getCombustionChamberType() {
        return COMBUSTION_CHAMBER.get();
    }
}
