package dev.aviatorsofcreate.gearsandjets.block;

import dev.aviatorsofcreate.gearsandjets.blockentity.CombustionChamberBlockEntity;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class CombustionChamberBlock extends Block implements EntityBlock {
    protected CombustionChamberBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public abstract int getTankCapacity();

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CombustionChamberBlockEntity(ModBlockEntityTypes.COMBUSTION_CHAMBER.get(), pos, state);
    }
}
