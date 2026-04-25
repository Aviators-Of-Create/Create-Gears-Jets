package dev.aviatorsofcreate.gearsandjets.mixin.client;

import dev.eriksonn.aeronautics.content.particle.PropellerAirParticle;
import dev.ryanhcode.sable.mixinterface.particle.ParticleExtension;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PropellerAirParticle.class)
public abstract class PropellerAirParticleMixin extends SimpleAnimatedParticle {
    @Shadow
    Vec3 motion;

    protected PropellerAirParticleMixin(ClientLevel level, double x, double y, double z, SpriteSet sprites, float gravity) {
        super(level, x, y, z, sprites, gravity);
    }

    /**
     * @author Aviators of Create
     * @reason Keep intake particles moving in the tracked sublevel frame instead of raw world axes
     */
    @Overwrite
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.setSprite(this.sprites.get((int) Mth.clamp((this.age / (float) this.lifetime) * 8, 0, 7), 8));

        Vec3 worldMotion = this.motion;
        SubLevel trackingSubLevel = ((ParticleExtension) this).sable$getTrackingSubLevel();
        if (trackingSubLevel != null) {
            worldMotion = trackingSubLevel.logicalPose().transformNormal(this.motion);
        }

        this.xd = worldMotion.x;
        this.yd = worldMotion.y;
        this.zd = worldMotion.z;

        double friction = PropellerAirParticle.frictionScale * this.motion.length();
        friction = Math.min(friction, 0.5F);
        this.motion = this.motion.scale(1.0D - friction);

        this.move(this.xd, this.yd, this.zd);
    }
}
