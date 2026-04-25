package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import dev.aviatorsofcreate.gearsandjets.content.jetengines.JetComponentBlock;
import dev.aviatorsofcreate.gearsandjets.enums.MachineState;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import net.minecraft.core.Direction;

import static dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock.POWERED;

public abstract class CombustionChamberBlock extends JetComponentBlock {
    SableBlockWeight sableBlockWeight;

    protected CombustionChamberBlock(Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
    }
}
