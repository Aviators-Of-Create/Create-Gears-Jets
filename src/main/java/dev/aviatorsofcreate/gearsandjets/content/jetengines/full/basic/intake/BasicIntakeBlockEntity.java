package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.intake;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.IntakeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BasicIntakeBlockEntity extends IntakeBlockEntity {
    public BasicIntakeBlockEntity(BlockEntityType<?> intakeType, BlockPos pos, BlockState state) {
        super(intakeType, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}
}