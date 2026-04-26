package dev.aviatorsofcreate.gearsandjets.content.jetengines.generic;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.JetComponentBlock;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlockEntity;
import dev.aviatorsofcreate.gearsandjets.enums.MachineState;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import static dev.aviatorsofcreate.gearsandjets.content.jetengines.full.basic.combustion.BasicCombustionChamberBlock.POWERED;

public abstract class CombustionChamberBlock extends JetComponentBlock implements EntityBlock{
    SableBlockWeight sableBlockWeight;
    public static final EnumProperty<MachineState> MACHINE_STATE =
            EnumProperty.create("machine_state", MachineState.class);

    protected CombustionChamberBlock(Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
        this.sableBlockWeight = sableBlockWeight;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicCombustionChamberBlockEntity(ModBlockEntityTypes.COMBUSTION_CHAMBER.get(), pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != ModBlockEntityTypes.COMBUSTION_CHAMBER.get()) {
            return null;
        }

        return (tickerLevel, tickerPos, tickerState, blockEntity) -> {
            if (blockEntity instanceof BasicCombustionChamberBlockEntity chamber) {
                chamber.tick();
            }
        };
    }
}
