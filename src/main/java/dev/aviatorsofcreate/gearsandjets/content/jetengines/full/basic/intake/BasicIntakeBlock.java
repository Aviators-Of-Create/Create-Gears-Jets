package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.intake;

import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.IntakeBlock;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import net.minecraft.core.Direction;

public class BasicIntakeBlock extends IntakeBlock {
    SableBlockWeight sableBlockWeight;

    protected BasicIntakeBlock(Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
        );
    }
}
