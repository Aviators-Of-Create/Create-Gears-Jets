package dev.aviatorsofcreate.gearsandjets.client;

import dev.aviatorsofcreate.gearsandjets.CreateGearsandJets;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;

public final class ModPartialModels {
    public static final PartialModel SMART_TORSION_SPRING =
            PartialModel.of(CreateGearsandJets.location("block/smart_torsion_spring/spring"));
    public static final PartialModel SMART_TORSION_BEARING_TOP =
            PartialModel.of(CreateGearsandJets.location("block/bearing/top"));

    private ModPartialModels() {
    }

    public static void init() {
        // Initialize static partial models before Flywheel registers additional models.
    }
}
