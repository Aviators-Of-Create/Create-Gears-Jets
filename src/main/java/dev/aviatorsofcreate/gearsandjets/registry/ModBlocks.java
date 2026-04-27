package dev.aviatorsofcreate.gearsandjets.registry;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.content.AirplaneSeatBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.exhaust.BasicExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.intake.BasicIntakeBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.special.exhaust.afterburning.AfterburningExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.content.smart_torsion_bearing.SmartTorsionBearingBlock;
import dev.aviatorsofcreate.gearsandjets.content.smart_torsion_spring.SmartTorsionSpringBlock;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CreateGearsandJets.MODID);
    public static final DeferredBlock<BasicIntakeBlock> BASIC_INTAKE = BLOCKS.register(
            "basic_intake",
            () -> new BasicIntakeBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion(),
                    SableBlockWeight.HEAVY
            )
    );
    public static final DeferredBlock<BasicCombustionChamberBlock> BASIC_COMBUSTION_CHAMBER = BLOCKS.register(
            "basic_combustion_chamber",
            () -> new BasicCombustionChamberBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).noOcclusion(),
                    SableBlockWeight.HEAVY
            )
    );
    public static final DeferredBlock<ExhaustBlock> BASIC_EXHAUST = BLOCKS.register(
            "basic_exhaust",
            () -> new BasicExhaustBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).noOcclusion(),
                    SableBlockWeight.HEAVY
            )
    );
    public static final DeferredBlock<AfterburningExhaustBlock> AFTERBURNING_EXHAUST = BLOCKS.register(
            "afterburning_exhaust",
            () -> new AfterburningExhaustBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).noOcclusion(),
                    SableBlockWeight.HEAVY
            )
    );
    public static final DeferredBlock<AirplaneSeatBlock> AIRPLANE_SEAT = BLOCKS.register(
            "airplane_seat",
            () -> new AirplaneSeatBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .strength(0.8F)
                    .sound(SoundType.WOOL)
                    .noOcclusion()
            )
    );
    public static final DeferredBlock<SmartTorsionSpringBlock> SMART_TORSION_SPRING = BLOCKS.register(
            "smart_torsion_spring",
            () -> new SmartTorsionSpringBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            )
    );
    public static final DeferredBlock<SmartTorsionBearingBlock> SMART_TORSION_BEARING = BLOCKS.register(
            "smart_torsion_bearing",
            () -> new SmartTorsionBearingBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0F)
                    .sound(SoundType.METAL)
                    .noOcclusion()
            )
    );

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
