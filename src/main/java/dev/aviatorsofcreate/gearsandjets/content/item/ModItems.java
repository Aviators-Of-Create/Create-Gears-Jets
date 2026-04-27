package dev.aviatorsofcreate.gearsandjets.content.item;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateGearsandJets.MODID);
    public static final DeferredItem<BlockItem> BASIC_INTAKE =
            registerBlockItem("basic_intake", ModBlocks.BASIC_INTAKE);
    public static final DeferredItem<BlockItem> BASIC_COMBUSTION_CHAMBER =
            registerBlockItem("basic_combustion_chamber", ModBlocks.BASIC_COMBUSTION_CHAMBER);
    public static final DeferredItem<BlockItem> BASIC_EXHAUST =
            registerBlockItem("basic_exhaust", ModBlocks.BASIC_EXHAUST);
    public static final DeferredItem<BlockItem> AIRPLANE_SEAT =
            registerBlockItem("airplane_seat", ModBlocks.AIRPLANE_SEAT);
    public static final DeferredItem<BlockItem> SMART_TORSION_SPRING =
            registerBlockItem("smart_torsion_spring", ModBlocks.SMART_TORSION_SPRING);
    public static final DeferredItem<BlockItem> SMART_TORSION_BEARING =
            registerBlockItem("smart_torsion_bearing", ModBlocks.SMART_TORSION_BEARING);
    public static final DeferredItem<BlockItem> AFTERBURNING_EXHAUST =
            registerBlockItem("afterburning_exhaust", ModBlocks.AFTERBURNING_EXHAUST);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static DeferredItem<BlockItem> registerBlockItem(String name, net.neoforged.neoforge.registries.DeferredBlock<?> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
