package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import dev.aviatorsofcreate.gearsandjets.enums.MachineState;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

public abstract class CombustionChamberBlock extends JetComponentBlock implements EntityBlock {

    SableBlockWeight sableBlockWeight;
    public static final Property<Boolean> POWERED = BlockStateProperties.POWERED;

    public static final EnumProperty<MachineState> MACHINE_STATE =
            EnumProperty.create("machine_state", MachineState.class);


    protected CombustionChamberBlock(Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(MACHINE_STATE, MachineState.OFF)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
        builder.add(MACHINE_STATE);
    }
}
