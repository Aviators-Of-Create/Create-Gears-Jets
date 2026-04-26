package dev.aviatorsofcreate.gearsandjets.content.smart_torsion_bearing;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.BearingRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class SmartTorsionBearingRenderer extends BearingRenderer<SmartTorsionBearingBlockEntity> {
    public SmartTorsionBearingRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(SmartTorsionBearingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        BlockState state = getRenderedBlockState(be);
        RenderType type = getRenderType(be, state);
        renderRotatingBuffer(be, getRotatedModel(be, state), ms, buffer.getBuffer(type), light);

        Direction facing = be.getBlockState().getValue(BearingBlock.FACING);
        SuperByteBuffer top = CachedBuffers.partial(AllPartialModels.BEARING_TOP, be.getBlockState());
        float interpolatedAngle = be.getInterpolatedAngle(partialTicks - 1);

        kineticRotationTransform(top, be, facing.getAxis(), (float) (interpolatedAngle / 180 * Math.PI), light);

        if (facing.getAxis().isHorizontal()) {
            top.rotateCentered(AngleHelper.rad(AngleHelper.horizontalAngle(facing.getOpposite())), Direction.UP);
        }

        top.rotateCentered(AngleHelper.rad(-90 - AngleHelper.verticalAngle(facing)), Direction.EAST);
        top.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }
}
