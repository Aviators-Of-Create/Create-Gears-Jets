package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion;

import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.CombustionChamberBlock;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.enums.MachineState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public class BasicCombustionChamberBlock extends CombustionChamberBlock{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Property<Boolean> POWERED = BlockStateProperties.POWERED;
    private final SableBlockWeight sableBlockWeight;


    protected BasicCombustionChamberBlock(BlockBehaviour.Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(MACHINE_STATE, MachineState.OFF)
        );
    }
}
