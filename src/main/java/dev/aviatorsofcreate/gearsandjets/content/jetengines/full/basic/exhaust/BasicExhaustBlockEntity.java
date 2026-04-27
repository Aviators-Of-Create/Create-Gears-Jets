package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.exhaust;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.ExhaustBlockEntity;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BasicExhaustBlockEntity extends ExhaustBlockEntity {

    // This class uses the defaults

    public BasicExhaustBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
}
