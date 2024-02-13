package net.kore.mixins.player;

import com.google.common.collect.Maps;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin({ EntityLivingBase.class })
public abstract class MixinEntityLivingBase extends MixinEntity
{

    @Shadow
    protected abstract float getJumpUpwardsMotion();
    @Shadow
    public abstract boolean isPotionActive(final int p0);
    @Shadow
    public abstract PotionEffect getActivePotionEffect(final Potion p0);
    @Shadow
    protected abstract void jump();
    @Shadow
    private Map<Integer, PotionEffect> activePotionsMap;
    @Shadow
    public final float getHealth() {
        return this.dataWatcher.getWatchableObjectFloat(6);
    }
}