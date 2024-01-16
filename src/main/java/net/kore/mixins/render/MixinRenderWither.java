package net.kore.mixins.render;

import net.kore.Kore;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderWither;
import net.minecraft.entity.boss.EntityWither;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ RenderWither.class })
public class MixinRenderWither
{
    @Inject(method = { "preRenderCallback(Lnet/minecraft/entity/boss/EntityWither;F)V" }, at = { @At("HEAD") }, cancellable = true)
    private <T extends EntityWither> void onPreRenderCallback(final T entitylivingbaseIn, final float partialTickTime, final CallbackInfo ci) {
        if (Kore.giants != null && Kore.giants.isToggled() && Kore.giants.mobs.isEnabled()) {
            GlStateManager.scale(Kore.giants.scale.getValue(), Kore.giants.scale.getValue(), Kore.giants.scale.getValue());
        }
    }
}
