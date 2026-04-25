package dev.aviatorsofcreate.gearsandjets;

import dev.aviatorsofcreate.gearsandjets.client.EngineIntakeParticleSpawner;
import dev.aviatorsofcreate.gearsandjets.client.EngineParticleBridge;
import dev.aviatorsofcreate.gearsandjets.client.ModPartialModels;
import dev.aviatorsofcreate.gearsandjets.content.smart_torsion_spring.SmartTorsionSpringRenderer;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CreateGearsandJets.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CreateGearsandJets.MODID, value = Dist.CLIENT)
public class CreateGearsandJetsClient {
    public CreateGearsandJetsClient(ModContainer container) {
        ModPartialModels.init();
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        EngineParticleBridge.setIntakeParticleSpawner(EngineIntakeParticleSpawner::spawn);
        event.enqueueWork(() -> BlockEntityRenderers.register(
                ModBlockEntityTypes.SMART_TORSION_SPRING.get(),
                SmartTorsionSpringRenderer::new
        ));
        // Some client setup code
        CreateGearsandJets.LOGGER.info("HELLO FROM CLIENT SETUP");
        CreateGearsandJets.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
