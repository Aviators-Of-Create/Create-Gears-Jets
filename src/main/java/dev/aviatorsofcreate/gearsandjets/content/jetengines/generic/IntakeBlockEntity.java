package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class IntakeBlockEntity extends JetComponentBlockEntity {

    public IntakeBlockEntity(BlockEntityType<?> intakeType, BlockPos pos, BlockState state) {
        super(intakeType, pos, state);
    }
}
