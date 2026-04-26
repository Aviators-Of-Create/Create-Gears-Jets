package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.exhaust;

import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import net.minecraft.core.Direction;

public class BasicExhaustBlock extends ExhaustBlock {
    SableBlockWeight sableBlockWeight;

    protected BasicExhaustBlock(Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
    }
}
