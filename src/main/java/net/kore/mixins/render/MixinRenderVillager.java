package net.kore.mixins.render;

import net.kore.Kore;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.passive.EntityVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ RenderVillager.class })
public class MixinRenderVillager
{
    @Inject(method = { "preRenderCallback(Lnet/minecraft/entity/passive/EntityVillager;F)V" }, at = { @At("HEAD") }, cancellable = true)
    private <T extends EntityVillager> void onPreRenderCallback(final T entitylivingbaseIn, final float partialTickTime, final CallbackInfo ci) {
        if (Kore.giants.isToggled() && Kore.giants.mobs.isEnabled()) {
            GlStateManager.scale(Kore.giants.scale.getValue(), Kore.giants.scale.getValue(), Kore.giants.scale.getValue());
        }
    }
}
