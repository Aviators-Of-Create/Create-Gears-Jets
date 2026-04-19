package dev.aviatorsofcreate.gearsandjets.block;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateGearsandJets.MODID);
    public static final DeferredBlock<SimpleIntakeBlock> SIMPLE_INTAKE = BLOCKS.register(
            "simple_intake",
            () -> new SimpleIntakeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE))
    );
    public static final DeferredBlock<SimpleCombustionChamberBlock> SIMPLE_COMBUSTION_CHAMBER = BLOCKS.register(
            "simple_combustion_chamber",
            () -> new SimpleCombustionChamberBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE))
    );
    public static final DeferredBlock<SimpleExhaustBlock> SIMPLE_EXHAUST = BLOCKS.register(
            "simple_exhaust",
            () -> new SimpleExhaustBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY))
    );

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
