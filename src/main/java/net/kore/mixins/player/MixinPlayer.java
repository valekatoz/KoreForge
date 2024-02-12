package net.kore.mixins.player;

import net.kore.Kore;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.stats.StatBase;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.util.FoodStats;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityPlayer.class })
public abstract class MixinPlayer extends MixinEntityLivingBase
{
    @Shadow
    public PlayerCapabilities capabilities;
    @Shadow
    private int itemInUseCount;
    @Shadow
    public InventoryPlayer inventory;
    @Shadow
    protected float speedInAir;
    @Shadow
    public float experience;
    @Shadow
    public int experienceLevel;
    @Shadow
    public int experienceTotal;
    @Shadow
    public float eyeHeight;
    @Shadow
    protected float speedOnGround;
    @Shadow
    protected FoodStats foodStats;
    @Shadow
    public float renderOffsetX;
    @Shadow
    public float renderOffsetY;
    @Shadow
    public EntityFishHook fishEntity;
    private boolean wasSprinting;

    @Shadow
    public abstract void triggerAchievement(final StatBase p0);

    @Shadow
    public abstract void addExhaustion(final float p0);

    @Shadow
    public abstract FoodStats getFoodStats();

    @Shadow
    public abstract void attackTargetEntityWithCurrentItem(final Entity p0);

    @Shadow
    @Override
    public abstract ItemStack getHeldItem();

    @Shadow
    public abstract ItemStack getCurrentEquippedItem();

    @Shadow
    public abstract void destroyCurrentEquippedItem();

    @Shadow
    protected void updateEntityActionState() {
    }

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract ItemStack getItemInUse();

    @Shadow
    protected abstract String getSwimSound();

    @Shadow
    protected abstract boolean canTriggerWalking();

    @Shadow
    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return false;
    }

    @Shadow
    public abstract boolean attackEntityFrom(final DamageSource p0, final float p1);

    @Shadow
    @Override
    protected abstract void entityInit();

    @Shadow
    public void moveEntityWithHeading(final float strafe, final float forward) {
    }

    @Shadow
    public abstract void addMovementStat(final double p0, final double p1, final double p2);

    @Shadow
    public abstract void onUpdate();

    @Shadow
    public abstract float getAIMoveSpeed();

    @Shadow
    public abstract EntityPlayer.EnumStatus trySleep(final BlockPos p0);
}
