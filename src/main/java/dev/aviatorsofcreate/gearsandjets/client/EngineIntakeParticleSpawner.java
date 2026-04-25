package dev.aviatorsofcreate.gearsandjets.client;

import dev.eriksonn.aeronautics.content.particle.PropellerAirParticleData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class EngineIntakeParticleSpawner {
    private static final double MAX_ENGINE_AIRFLOW = 100.0D;

    private EngineIntakeParticleSpawner() {
    }

    public static void spawn(Level level, BlockPos pos, Direction facing, double airflow) {
        if (!(level instanceof ClientLevel clientLevel)) {
            return;
        }

        double clampedAirflow = Math.abs(airflow);
        if (clampedAirflow <= 0) {
            return;
        }

        RandomSource random = clientLevel.getRandom();
        double airflowTickSpeed = clampedAirflow / 20.0D;
        double normalizedAirflow = Mth.clamp(clampedAirflow / MAX_ENGINE_AIRFLOW, 0.0D, 1.0D);
        int particleCount = Mth.clamp(Mth.ceil(0.6D + airflowTickSpeed * 1.1D + random.nextDouble()), 1, 6);
        double intakeRadius = 0.16D + normalizedAirflow * 0.22D;
        double spawnDepth = 0.35D + normalizedAirflow * 0.28D;
        double speed = 0.02D + airflowTickSpeed * 0.03D;

        Vec3 inward = Vec3.atLowerCornerOf(facing.getOpposite().getNormal());
        Vec3 outward = inward.scale(-1);
        Vec3 lateral = facing.getAxis() == Direction.Axis.Z ? new Vec3(1, 0, 0) : new Vec3(0, 0, 1);
        Vec3 vertical = new Vec3(0, 1, 0);
        Vec3 intakeFaceCenter = Vec3.atCenterOf(pos).add(outward.scale(0.45D));

        for (int i = 0; i < particleCount; i++) {
            double radius = intakeRadius * Math.sqrt(random.nextDouble());
            double angle = Math.PI * 2.0D * random.nextDouble();
            Vec3 faceOffset = lateral.scale(Math.cos(angle) * radius).add(vertical.scale(Math.sin(angle) * radius));
            Vec3 particlePos = intakeFaceCenter.add(faceOffset).add(outward.scale(random.nextDouble() * spawnDepth));
            double speedScale = Mth.lerp(random.nextFloat(), 0.75D, 1.2D);
            Vec3 particleVelocity = inward.scale(speed * speedScale)
                    .add(lateral.scale((random.nextDouble() - 0.5D) * speed * 0.18D))
                    .add(vertical.scale((random.nextDouble() - 0.5D) * speed * 0.18D));

            clientLevel.addParticle(new PropellerAirParticleData(false, false),
                    particlePos.x, particlePos.y, particlePos.z,
                    particleVelocity.x, particleVelocity.y, particleVelocity.z);
        }
    }
}
