package net.kore.mixins.player;

import net.minecraft.stats.StatBase;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityPlayer.class })
public abstract class MixinPlayer extends MixinEntityLivingBase
{
    @Shadow
    public abstract void triggerAchievement(final StatBase p0);
    @Shadow
    public abstract void addExhaustion(final float p0);
    @Shadow
    protected boolean sleeping;
    @Shadow
    private int sleepTimer;
}