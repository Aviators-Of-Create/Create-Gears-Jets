package dev.aviatorsofcreate.gearsandjets.registry;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.exhaust.BasicExhaustBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.intake.BasicIntakeBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.special.exhaust.afterburning.AfterburningExhaustBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.smart_torsion_bearing.SmartTorsionBearingBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.smart_torsion_spring.SmartTorsionSpringBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlockEntity;
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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasicCombustionChamberBlockEntity>> BASIC_COMBUSTION_CHAMBER =
            BLOCK_ENTITY_TYPES.register(
                    "basic_combustion_chamber",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new BasicCombustionChamberBlockEntity(getCombustionChamberType(), pos, state),
                            ModBlocks.BASIC_COMBUSTION_CHAMBER.get()
                    ).build(null)
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmartTorsionSpringBlockEntity>> SMART_TORSION_SPRING =
            BLOCK_ENTITY_TYPES.register(
                    "smart_torsion_spring",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new SmartTorsionSpringBlockEntity(getSmartTorsionSpringType(), pos, state),
                            ModBlocks.SMART_TORSION_SPRING.get()
                    ).build(null)
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmartTorsionBearingBlockEntity>> SMART_TORSION_BEARING =
            BLOCK_ENTITY_TYPES.register(
                    "smart_torsion_bearing",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new SmartTorsionBearingBlockEntity(getSmartTorsionBearingType(), pos, state),
                            ModBlocks.SMART_TORSION_BEARING.get()
                    ).build(null)
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasicIntakeBlockEntity>> BASIC_INTAKE =
            BLOCK_ENTITY_TYPES.register(
                    "basic_intake",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new BasicIntakeBlockEntity(getIntakeType(), pos, state),
                            ModBlocks.BASIC_INTAKE.get()
                    ).build(null)
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasicExhaustBlockEntity>> BASIC_EXHAUST =
            BLOCK_ENTITY_TYPES.register(
                    "basic_exhaust",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new BasicExhaustBlockEntity(getBasicExhaustType(), pos, state),
                            ModBlocks.BASIC_EXHAUST.get()
                    ).build(null)
            );
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AfterburningExhaustBlockEntity>> AFTERBURNING_EXHAUST =
            BLOCK_ENTITY_TYPES.register(
                    "afterburning_exhaust",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) ->  new AfterburningExhaustBlockEntity(getAfterburningExhaustType(), pos, state),
                            ModBlocks.AFTERBURNING_EXHAUST.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
        eventBus.addListener(ModBlockEntityTypes::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BASIC_COMBUSTION_CHAMBER.get(),
                (blockEntity, side) -> blockEntity.getTank()
        );
    }

    private static BlockEntityType<BasicCombustionChamberBlockEntity> getCombustionChamberType() {
        return BASIC_COMBUSTION_CHAMBER.get();
    }

    private static BlockEntityType<SmartTorsionSpringBlockEntity> getSmartTorsionSpringType() {
        return SMART_TORSION_SPRING.get();
    }

    private static BlockEntityType<SmartTorsionBearingBlockEntity> getSmartTorsionBearingType() {
        return SMART_TORSION_BEARING.get();
    }

    private static BlockEntityType<BasicIntakeBlockEntity> getIntakeType() {
        return BASIC_INTAKE.get();
    }

    private static BlockEntityType<BasicExhaustBlockEntity> getBasicExhaustType() {
        return BASIC_EXHAUST.get();
    }

    private static BlockEntityType<AfterburningExhaustBlockEntity> getAfterburningExhaustType() {
        return AFTERBURNING_EXHAUST.get();
    }
}
