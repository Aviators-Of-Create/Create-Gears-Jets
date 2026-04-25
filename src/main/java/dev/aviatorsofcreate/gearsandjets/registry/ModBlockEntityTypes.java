package dev.aviatorsofcreate.gearsandjets.registry;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.content.ModBlocks;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.exhaust.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.intake.IntakeBlock;
import dev.aviatorsofcreate.gearsandjets.content.smart_torsion_spring.SmartTorsionSpringBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CreateGearsandJets.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasicCombustionChamberBlockEntity>> COMBUSTION_CHAMBER =
            BLOCK_ENTITY_TYPES.register(
                    "combustion_chamber",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new BasicCombustionChamberBlockEntity(getCombustionChamberType(), pos, state),
                            ModBlocks.SIMPLE_COMBUSTION_CHAMBER.get()
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
        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    BasicCombustionChamberBlockEntity chamber = getAttachedCombustionChamber(level, pos, state);
                    return chamber != null ? chamber.getFluidHandler() : null;
                },
                ModBlocks.SIMPLE_INTAKE.get(),
                ModBlocks.SIMPLE_EXHAUST.get()
        );
    }

    private static BlockEntityType<BasicCombustionChamberBlockEntity> getCombustionChamberType() {
        return COMBUSTION_CHAMBER.get();
    }

    private static BlockEntityType<SmartTorsionSpringBlockEntity> getSmartTorsionSpringType() {
        return SMART_TORSION_SPRING.get();
    }

    private static BasicCombustionChamberBlockEntity getAttachedCombustionChamber(Level level, BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof IntakeBlock && state.hasProperty(IntakeBlock.FACING)) {
            Direction facing = state.getValue(IntakeBlock.FACING);
            return getCombustionChamber(level, pos.relative(facing.getOpposite()), facing);
        }

        if (state.getBlock() instanceof ExhaustBlock && state.hasProperty(ExhaustBlock.FACING)) {
            Direction facing = state.getValue(ExhaustBlock.FACING);
            return getCombustionChamber(level, pos.relative(facing.getOpposite()), facing.getOpposite());
        }

        return null;
    }

    private static BasicCombustionChamberBlockEntity getCombustionChamber(Level level, BlockPos chamberPos, Direction expectedFacing) {
        BlockState chamberState = level.getBlockState(chamberPos);
        if (!(chamberState.getBlock() instanceof BasicCombustionChamberBlock)
                || !chamberState.hasProperty(BasicCombustionChamberBlock.FACING)
                || chamberState.getValue(BasicCombustionChamberBlock.FACING) != expectedFacing) {
            return null;
        }

        return level.getBlockEntity(chamberPos) instanceof BasicCombustionChamberBlockEntity chamber ? chamber : null;
    }
}
