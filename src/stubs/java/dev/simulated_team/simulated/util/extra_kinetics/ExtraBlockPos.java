package dev.simulated_team.simulated.util.extra_kinetics;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

public class ExtraBlockPos extends BlockPos {
    public ExtraBlockPos(Vec3i blockPos) {
        super(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
