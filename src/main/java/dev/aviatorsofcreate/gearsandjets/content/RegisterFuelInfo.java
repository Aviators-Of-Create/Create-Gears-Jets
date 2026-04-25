package dev.aviatorsofcreate.gearsandjets.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.tterrag.registrate.providers.ProviderType;
import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.aviatorsofcreate.gearsandjets.Registries;
import dev.aviatorsofcreate.gearsandjets.fuel_type.FuelType;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.Map;

@EventBusSubscriber(modid = CreateGearsandJets.MODID)
public class RegisterFuelInfo {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void gatherData(GatherDataEvent event) {

        CreateGearsandJets.REGISTRATE.addDataGenerator(ProviderType.LANG, provider -> {

            JsonElement jsonElement = FilesHelper.loadJsonResource("assets/createdieselgenerators/lang/default/default.json");
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet())
                provider.add(entry.getKey(), entry.getValue().getAsString());
;
        });
    }

    @SubscribeEvent
    public static void registerDataPackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                Registries.FUEL_TYPE,
                FuelType.CODEC,
                FuelType.NCODEC
        );
    }
}
