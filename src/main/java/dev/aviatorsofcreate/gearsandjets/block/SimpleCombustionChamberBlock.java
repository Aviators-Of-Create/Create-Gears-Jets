package dev.aviatorsofcreate.gearsandjets.block;

import dev.aviatorsofcreate.gearsandjets.Config;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SimpleCombustionChamberBlock extends CombustionChamberBlock {
    public SimpleCombustionChamberBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
    }

    @Override
    public int getTankCapacity() {
        return Config.SIMPLE_COMBUSTION_CHAMBER_CAPACITY.getAsInt();
    }
}
