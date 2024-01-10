package net.kore.mixins.render;

import net.kore.Kore;
import net.kore.events.RenderEntityEvent;
import net.kore.events.RenderLayersEvent;
import net.kore.mixins.render.MixinRender;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Logger;

@Mixin({ RendererLivingEntity.class })
public abstract class MixinRenderLivingEntity extends MixinRender
{
    @Shadow
    protected ModelBase mainModel;

    @Shadow
    protected boolean renderOutlines;

    @Shadow
    protected abstract <T extends EntityLivingBase> float getSwingProgress(final T p0, final float p1);

    @Shadow
    protected abstract float interpolateRotation(final float p0, final float p1, final float p2);

    @Shadow
    protected abstract <T extends EntityLivingBase> void renderLivingAt(final T p0, final double p1, final double p2, final double p3);

    @Shadow
    protected abstract <T extends EntityLivingBase> float handleRotationFloat(final T p0, final float p1);

    @Shadow
    protected abstract <T extends EntityLivingBase> void rotateCorpse(final T p0, final float p1, final float p2, final float p3);

    @Shadow
    protected abstract <T extends EntityLivingBase> void preRenderCallback(final T p0, final float p1);

    @Shadow
    protected abstract <T extends EntityLivingBase> boolean setScoreTeamColor(final T p0);

    @Shadow
    protected abstract void unsetScoreTeamColor();

    @Shadow
    protected abstract <T extends EntityLivingBase> boolean setDoRenderBrightness(final T p0, final float p1);

    @Shadow
    protected abstract void unsetBrightness();

    @Shadow
    protected abstract <T extends EntityLivingBase> void renderModel(final T p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6);

    @Shadow
    protected abstract <T extends EntityLivingBase> void renderLayers(final T p0, final float p1, final float p2, final float p3, final float p4, final float p5, final float p6, final float p7);

    @Redirect(method = { "doRender(Lnet/minecraft/entity/EntityLivingBase;DDDFF)V" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/RendererLivingEntity;renderModel(Lnet/minecraft/entity/EntityLivingBase;FFFFFF)V", ordinal = 1))
    private <T extends EntityLivingBase> void onDoRender(final RendererLivingEntity instance, final T entitylivingbaseIn, final float p_77036_2_, final float p_77036_3_, final float p_77036_4_, final float p_77036_5_, final float p_77036_6_, final float p_77036_7_) {
        if (!MinecraftForge.EVENT_BUS.post(new RenderEntityEvent((EntityLivingBase)entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_))) {
            this.renderModel(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);
        }
    }

    @Inject(method = { "renderLayers" }, at = { @At("RETURN") }, cancellable = true)
    protected void renderLayersPost(final EntityLivingBase entitylivingbaseIn, final float p_177093_2_, final float p_177093_3_, final float partialTicks, final float p_177093_5_, final float p_177093_6_, final float p_177093_7_, final float p_177093_8_, final CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new RenderLayersEvent(entitylivingbaseIn, p_177093_2_, p_177093_3_, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_, this.mainModel))) {
            ci.cancel();
        }
    }

    @Inject(method = { "preRenderCallback" }, at = { @At("HEAD") }, cancellable = true)
    private <T extends EntityLivingBase> void onPreRenderCallback(final T entitylivingbaseIn, final float partialTickTime, final CallbackInfo ci) {
        if (Kore.giants.isToggled() && Kore.giants.mobs.isEnabled() && (!(entitylivingbaseIn instanceof EntityArmorStand) || Kore.giants.armorStands.isEnabled())) {
            GlStateManager.scale(Kore.giants.scale.getValue(), Kore.giants.scale.getValue(), Kore.giants.scale.getValue());
        }
    }
}
