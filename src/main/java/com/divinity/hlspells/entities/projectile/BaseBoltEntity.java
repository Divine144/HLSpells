package com.divinity.hlspells.entities.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public abstract class BaseBoltEntity extends Arrow {

    private final boolean overridesTick;
    private final SimpleParticleType[] particleTypes;

    public BaseBoltEntity(EntityType<? extends BaseBoltEntity> entityType, Level level, boolean overridesTick, SimpleParticleType... particleTypes) {
        super(entityType, level);
        this.overridesTick = overridesTick;
        this.particleTypes = particleTypes;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.overridesTick) {
            // Remove if it's more than 40 block away from the owner
            if (this.getOwner() != null && this.distanceTo(this.getOwner()) > 40) this.remove(RemovalReason.KILLED);
            Vec3 vector3d1 = this.getDeltaMovement();
            double baseYOffset = 0.15D;
            if (this.level.isClientSide) {
                for (int i = 0; i < this.particleTypes.length; i++) {
                    this.level.addParticle(this.particleTypes[i], this.getX() - vector3d1.x, this.getY() - (vector3d1.y + (baseYOffset + ((double) i / 100))), this.getZ() - vector3d1.z, 0, 0, 0);
                }
            }
        }
    }

    @Override
    public void checkDespawn() {
        super.checkDespawn();
        if (this.level.getDifficulty() == Difficulty.PEACEFUL) this.discard();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.level instanceof ServerLevel level && source.isProjectile() && this.isAlive()) {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
            level.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.remove(RemovalReason.KILLED);
            return true;
        }
        return false;
    }

    @Override @NotNull public Packet<?> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }

    @Override public boolean isNoGravity() { return true; }

    @Override public boolean isPickable() { return false; }

    @Override public boolean fireImmune() { return (this instanceof AquaBoltEntity || this instanceof InvisibleTargetingEntity); }

    @Override @NotNull protected SoundEvent getDefaultHitGroundSoundEvent() { return SoundEvents.SHULKER_BULLET_HIT; }

    @Override protected float getWaterInertia() { return 1F; }

    @Override protected boolean canHitEntity(@NotNull Entity entity) { return super.canHitEntity(entity) && !entity.noPhysics; }
}
