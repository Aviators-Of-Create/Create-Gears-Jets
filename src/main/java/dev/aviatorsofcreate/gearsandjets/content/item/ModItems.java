package dev.aviatorsofcreate.gearsandjets.content.item;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.content.block.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CreateGearsandJets.MODID);
    public static final DeferredItem<BlockItem> SIMPLE_INTAKE =
            registerBlockItem("simple_intake", ModBlocks.SIMPLE_INTAKE);
    public static final DeferredItem<BlockItem> SIMPLE_COMBUSTION_CHAMBER =
            registerBlockItem("simple_combustion_chamber", ModBlocks.SIMPLE_COMBUSTION_CHAMBER);
    public static final DeferredItem<BlockItem> SIMPLE_EXHAUST =
            registerBlockItem("simple_exhaust", ModBlocks.SIMPLE_EXHAUST);
    public static final DeferredItem<BlockItem> AIRPLANE_SEAT =
            registerBlockItem("airplane_seat", ModBlocks.AIRPLANE_SEAT);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    private static DeferredItem<BlockItem> registerBlockItem(String name, net.neoforged.neoforge.registries.DeferredBlock<?> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}
