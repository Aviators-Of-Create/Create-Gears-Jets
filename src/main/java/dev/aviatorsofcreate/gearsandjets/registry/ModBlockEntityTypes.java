package dev.aviatorsofcreate.gearsandjets.registry;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.block.CombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.block.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.block.IntakeBlock;
import dev.aviatorsofcreate.gearsandjets.block.ModBlocks;
import dev.aviatorsofcreate.gearsandjets.blockentity.CombustionChamberBlockEntity;
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
        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (level, pos, state, blockEntity, side) -> {
                    CombustionChamberBlockEntity chamber = getAttachedCombustionChamber(level, pos, state);
                    return chamber != null ? chamber.getFluidHandler() : null;
                },
                ModBlocks.SIMPLE_INTAKE.get(),
                ModBlocks.SIMPLE_EXHAUST.get()
        );
    }

    private static BlockEntityType<CombustionChamberBlockEntity> getCombustionChamberType() {
        return COMBUSTION_CHAMBER.get();
    }

    private static CombustionChamberBlockEntity getAttachedCombustionChamber(Level level, BlockPos pos, BlockState state) {
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

    private static CombustionChamberBlockEntity getCombustionChamber(Level level, BlockPos chamberPos, Direction expectedFacing) {
        BlockState chamberState = level.getBlockState(chamberPos);
        if (!(chamberState.getBlock() instanceof CombustionChamberBlock)
                || !chamberState.hasProperty(CombustionChamberBlock.FACING)
                || chamberState.getValue(CombustionChamberBlock.FACING) != expectedFacing) {
            return null;
        }

        return level.getBlockEntity(chamberPos) instanceof CombustionChamberBlockEntity chamber ? chamber : null;
    }
}
