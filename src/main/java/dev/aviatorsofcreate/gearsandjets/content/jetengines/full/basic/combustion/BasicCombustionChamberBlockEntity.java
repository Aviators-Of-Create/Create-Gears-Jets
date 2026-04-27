package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion;

import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BasicCombustionChamberBlockEntity extends CombustionChamberBlockEntity {
    public BasicCombustionChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
