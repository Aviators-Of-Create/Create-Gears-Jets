package dev.aviatorsofcreate.gearsandjets.content.jetengines.full.special.exhaust.afterburning;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.aviatorsofcreate.gearsandjets.content.interfaces.IEngine;
import dev.aviatorsofcreate.gearsandjets.content.jetengines.generic.ExhaustBlock;
import dev.aviatorsofcreate.gearsandjets.enums.SableBlockWeight;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class AfterburningExhaustBlock extends ExhaustBlock {
    public AfterburningExhaustBlock(Properties properties, SableBlockWeight sableBlockWeight) {
        super(properties, sableBlockWeight);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new AfterburningExhaustBlockEntity(ModBlockEntityTypes.AFTERBURNING_EXHAUST.get(), blockPos, blockState);
    }
}
