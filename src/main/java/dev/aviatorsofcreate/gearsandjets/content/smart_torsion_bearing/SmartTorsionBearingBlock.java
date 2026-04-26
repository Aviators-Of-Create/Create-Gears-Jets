package dev.aviatorsofcreate.gearsandjets.content.smart_torsion_bearing;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.aviatorsofcreate.gearsandjets.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SmartTorsionBearingBlock extends BearingBlock implements IBE<SmartTorsionBearingBlockEntity> {
    public SmartTorsionBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.FAIL;
        }
        if (stack.isEmpty()) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            withBlockEntityDo(level, pos, SmartTorsionBearingBlockEntity::toggleAssembly);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        SmartTorsionBearingBlockEntity be = level.getBlockEntity(pos) instanceof SmartTorsionBearingBlockEntity smart ? smart : null;
        if (be == null) {
            return 0;
        }

        float maxAngle = be.getMaxAngle();
        if (maxAngle <= 0.0F) {
            return 0;
        }

        float fraction = Mth.clamp(be.getDisplayedAngle() / maxAngle, -1.0F, 1.0F);
        if (Math.abs(be.getDisplayedAngle()) < 0.99F) {
            return 0;
        }

        return Mth.clamp(Math.round(Math.abs(fraction) * 15.0F), 0, 15);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, neighborPos, movedByPiston);
        this.withBlockEntityDo(level, pos, be -> be.onSignalChanged());
    }

    public static Direction getPositiveSignalDirection(BlockState state) {
        Direction facing = state.getValue(FACING);
        if (facing.getAxis().isVertical()) {
            return Direction.EAST;
        }
        return facing.getClockWise();
    }

    public static Direction getNegativeSignalDirection(BlockState state) {
        Direction facing = state.getValue(FACING);
        if (facing.getAxis().isVertical()) {
            return Direction.WEST;
        }
        return facing.getCounterClockWise();
    }

    @Override
    public Class<SmartTorsionBearingBlockEntity> getBlockEntityClass() {
        return SmartTorsionBearingBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SmartTorsionBearingBlockEntity> getBlockEntityType() {
        return ModBlockEntityTypes.SMART_TORSION_BEARING.get();
    }
}
