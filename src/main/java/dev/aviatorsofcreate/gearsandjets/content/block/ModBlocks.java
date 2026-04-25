package dev.aviatorsofcreate.gearsandjets.content.block;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.content.block.smart_torsion_spring.SmartTorsionSpringBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateGearsandJets.MODID);
    public static final DeferredBlock<SimpleIntakeBlock> SIMPLE_INTAKE = BLOCKS.register(
            "simple_intake",
            () -> new SimpleIntakeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion(), SableBlockWeight.HEAVY)
    );
    public static final DeferredBlock<SimpleCombustionChamberBlock> SIMPLE_COMBUSTION_CHAMBER = BLOCKS.register(
            "simple_combustion_chamber",
            () -> new SimpleCombustionChamberBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).noOcclusion(),
                    SableBlockWeight.HEAVY
            )
    );
    public static final DeferredBlock<SimpleExhaustBlock> SIMPLE_EXHAUST = BLOCKS.register(
            "simple_exhaust",
            () -> new SimpleExhaustBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).noOcclusion(), SableBlockWeight.HEAVY)
    );
    public static final DeferredBlock<AirplaneSeatBlock> AIRPLANE_SEAT = BLOCKS.register(
            "airplane_seat",
            () -> new AirplaneSeatBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.8F)
                    .sound(SoundType.WOOL)
                    .noOcclusion())
    );
    public static final DeferredBlock<SmartTorsionSpringBlock> SMART_TORSION_SPRING = BLOCKS.register(
            "smart_torsion_spring",
            () -> new SmartTorsionSpringBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion())
    );

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
