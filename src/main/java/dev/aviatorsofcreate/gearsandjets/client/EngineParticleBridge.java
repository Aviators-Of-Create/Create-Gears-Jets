package dev.aviatorsofcreate.gearsandjets.client;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public final class EngineParticleBridge {
    private static IntakeParticleSpawner intakeParticleSpawner = (level, pos, facing, airflow) -> {
    };

    private EngineParticleBridge() {
    }

    public static void setIntakeParticleSpawner(IntakeParticleSpawner intakeParticleSpawner) {
        EngineParticleBridge.intakeParticleSpawner = intakeParticleSpawner;
    }

    public static void spawnIntakeParticles(Level level, BlockPos pos, Direction facing, double airflow) {
        intakeParticleSpawner.spawn(level, pos, facing, airflow);
    }

    @FunctionalInterface
    public interface IntakeParticleSpawner {
        void spawn(Level level, BlockPos pos, Direction facing, double airflow);
    }
}
