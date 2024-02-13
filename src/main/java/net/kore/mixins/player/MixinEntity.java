package net.kore.mixins.player;

import net.kore.Kore;
import net.minecraft.entity.DataWatcher;
import net.minecraft.util.AxisAlignedBB;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Entity.class })
public abstract class MixinEntity
{
    @Shadow public Entity ridingEntity;

    @Shadow
    public abstract void moveFlying(final float p0, final float p1, final float p2);
    @Shadow
    public double motionX;
    @Shadow
    public double motionY;
    @Shadow
    public double motionZ;
    @Shadow
    public float rotationYaw;
    @Shadow
    public boolean isAirBorne;
    @Shadow
    public double posX;
    @Shadow
    public double posY;
    @Shadow
    public double posZ;
    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();
    @Shadow
    public float rotationPitch;
    @Shadow
    public boolean noClip;
    @Shadow
    public boolean onGround;
    @Shadow
    public abstract boolean isSprinting();
    @Shadow
    protected abstract boolean getFlag(int flag);
    @Shadow
    protected DataWatcher dataWatcher;
}
