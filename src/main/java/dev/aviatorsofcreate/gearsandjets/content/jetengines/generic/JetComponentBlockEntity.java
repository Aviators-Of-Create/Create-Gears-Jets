package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;


import java.util.ArrayList;

import static dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.JetComponentBlock.FACING;

public abstract class JetComponentBlockEntity extends SmartBlockEntity {

    private ArrayList<BlockPos> jetComponents;

    public JetComponentBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private void updateJetComponents() {
        this.jetComponents = JetComponentBlock.getJetEngineComponents(this.getLevel(), this.getBlockPos(), this.getLevel().getBlockState(this.getBlockPos()).getValue(FACING));
    }

    protected ArrayList<BlockPos> getJetComponents() {
        return this.jetComponents;
    }

    @Override
    public void tick() {

        // do this class's stuff
        updateJetComponents();

        // tick the base class
        super.tick();
    }
}