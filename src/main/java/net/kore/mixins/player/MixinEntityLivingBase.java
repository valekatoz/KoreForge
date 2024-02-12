package net.kore.mixins.player;

import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityLivingBase.class })
public abstract class MixinEntityLivingBase extends MixinEntity
{
    @Shadow
    public float rotationYawHead;
    @Shadow
    private int jumpTicks;
    @Shadow
    protected boolean isJumping;
    @Shadow
    public float jumpMovementFactor;
    @Shadow
    protected int newPosRotationIncrements;
    @Shadow
    public float moveForward;
    @Shadow
    public float moveStrafing;
    @Shadow
    protected float movedDistance;
    @Shadow
    protected int entityAge;
    @Shadow
    protected double newPosX;
    @Shadow
    public float renderYawOffset;
    @Shadow
    protected float onGroundSpeedFactor;
    @Shadow
    public float prevLimbSwingAmount;
    @Shadow
    public float limbSwingAmount;
    @Shadow
    public float limbSwing;
    @Shadow
    protected float prevOnGroundSpeedFactor;

    @Shadow
    protected abstract float getJumpUpwardsMotion();

    @Shadow
    public abstract boolean isPotionActive(final int p0);

    @Shadow
    public abstract PotionEffect getActivePotionEffect(final Potion p0);

    @Shadow
    protected abstract void jump();

    @Shadow
    public abstract IAttributeInstance getEntityAttribute(final IAttribute p0);

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract boolean isOnLadder();

    @Shadow
    public abstract boolean isPotionActive(final Potion p0);

    @Shadow
    public abstract void setLastAttacker(final Entity p0);

    @Shadow
    public abstract float getSwingProgress(final float p0);

    @Shadow
    protected abstract void updateFallState(final double p0, final boolean p1, final Block p2, final BlockPos p3);

    @Shadow
    protected abstract void resetPotionEffectMetadata();

    @Shadow
    public abstract ItemStack getHeldItem();

    @Shadow
    protected abstract void entityInit();

    @Shadow
    public abstract void setSprinting(final boolean p0);

    public void setJumpTicks(final int jumpTicks) {
        this.jumpTicks = jumpTicks;
    }

    public int getJumpTicks() {
        return this.jumpTicks;
    }
}
