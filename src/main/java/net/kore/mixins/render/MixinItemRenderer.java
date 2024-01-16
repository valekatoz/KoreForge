package net.kore.mixins.render;

import net.kore.Kore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = { ItemRenderer.class }, priority = 1)
public abstract class MixinItemRenderer {
    @Shadow
    private float prevEquippedProgress;
    @Shadow
    private float equippedProgress;
    @Shadow
    @Final
    private Minecraft mc;
    @Shadow
    private ItemStack itemToRender;

    @Shadow
    protected abstract void rotateArroundXAndY(final float p0, final float p1);

    @Shadow
    protected abstract void setLightMapFromPlayer(final AbstractClientPlayer p0);

    @Shadow
    protected abstract void rotateWithPlayerRotations(final EntityPlayerSP p0, final float p1);

    @Shadow
    protected abstract void renderItemMap(final AbstractClientPlayer p0, final float p1, final float p2, final float p3);

    @Shadow
    protected abstract void performDrinking(final AbstractClientPlayer p0, final float p1);

    @Shadow
    protected abstract void doItemUsedTransformations(final float p0);

    @Shadow
    public abstract void renderItem(final EntityLivingBase p0, final ItemStack p1, final ItemCameraTransforms.TransformType p2);

    @Shadow
    protected abstract void renderPlayerArm(final AbstractClientPlayer p0, final float p1, final float p2);

    @Shadow
    protected abstract void doBowTransformations(final float p0, final AbstractClientPlayer p1);

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void renderItemInFirstPerson(final float partialTicks) {
        final float f = 1.0f - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        final AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer)this.mc.thePlayer;
        final float f2 = abstractclientplayer.getSwingProgress(partialTicks);
        final float f3 = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * partialTicks;
        final float f4 = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * partialTicks;
        this.rotateArroundXAndY(f3, f4);
        this.setLightMapFromPlayer(abstractclientplayer);
        this.rotateWithPlayerRotations((EntityPlayerSP)abstractclientplayer, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        if (this.itemToRender != null) {
            if (this.itemToRender.getItem() instanceof ItemMap) {
                this.renderItemMap(abstractclientplayer, f3, f, f2);
            }
            else if (abstractclientplayer.getItemInUseCount() > 0) {
                EnumAction enumaction = this.itemToRender.getItemUseAction();
                switch (enumaction) {
                    case NONE: {
                        this.transformFirstPersonItem(f, 0.0f);
                        break;
                    }
                    case EAT:
                    case DRINK: {
                        this.performDrinking(abstractclientplayer, partialTicks);
                        this.transformFirstPersonItem(f, 0.0f);
                        break;
                    }
                    case BLOCK: {
                        if (Kore.animations.isToggled()) {
                            final String selected = Kore.animations.mode.getSelected();
                            switch (selected) {
                                case "1.7": {
                                    this.transformFirstPersonItem(f, f2);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "spin":
                                case "vertical spin": {
                                    this.transformFirstPersonItem(f, Kore.animations.showSwing.isEnabled() ? f2 : 0.0f);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "long hit": {
                                    this.transformFirstPersonItem(f, 0.0f);
                                    this.doBlockTransformations();
                                    final float var19 = MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f);
                                    GlStateManager.translate(-0.05f, 0.6f, 0.3f);
                                    GlStateManager.rotate(-var19 * 70.0f / 2.0f, -8.0f, -0.0f, 9.0f);
                                    GlStateManager.rotate(-var19 * 70.0f, 1.5f, -0.4f, -0.0f);
                                    break;
                                }
                                case "chill": {
                                    final float f5 = MathHelper.sin(MathHelper.sqrt_float(f2) * 3.1415927f);
                                    this.transformFirstPersonItem(f / 2.0f - 0.18f, 0.0f);
                                    GL11.glRotatef(f5 * 60.0f / 2.0f, -f5 / 2.0f, -0.0f, -16.0f);
                                    GL11.glRotatef(-f5 * 30.0f, 1.0f, f5 / 2.0f, -1.0f);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "push": {
                                    this.transformFirstPersonItem(f, -f2);
                                    this.doBlockTransformations();
                                    break;
                                }
                                case "helicopter": {
                                    GlStateManager.rotate((float)(System.currentTimeMillis() / 3L % 360L), 0.0f, 0.0f, -0.1f);
                                    this.transformFirstPersonItem(f / 1.6f, 0.0f);
                                    this.doBlockTransformations();
                                    break;
                                }
                            }
                            break;
                        }
                        this.transformFirstPersonItem(f, 0.0f);
                        this.doBlockTransformations();
                        break;
                    }
                    case BOW: {
                        this.transformFirstPersonItem(f, 0.0f);
                        this.doBowTransformations(partialTicks, abstractclientplayer);
                        break;
                    }
                }
            }
            else {
                this.doItemUsedTransformations(f2);
                this.transformFirstPersonItem(f, f2);
            }
            this.renderItem((EntityLivingBase) abstractclientplayer, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        }
        else if (!abstractclientplayer.isInvisible()) {
            this.renderPlayerArm(abstractclientplayer, f, f2);
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void transformFirstPersonItem(final float equipProgress, final float swingProgress) {
        final float size = (float) Kore.animations.size.getValue();
        final float x = (float)Kore.animations.x.getValue();
        final float y = (float)Kore.animations.y.getValue();
        final float z = (float)Kore.animations.z.getValue();
        GlStateManager.translate(0.56f * x, -0.52f * y, -0.71999997f * z);
        GlStateManager.translate(0.0f, equipProgress * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        final float f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f);
        final float f2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f);
        GlStateManager.rotate(f * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(f2 * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(f2 * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.4f * size, 0.4f * size, 0.4f * size);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    private void doBlockTransformations() {
        float angle1 = 30.0f;
        float angle2 = -80.0f;
        float angle3 = 60.0f;
        float translateX = -0.5f;
        float translateY = 0.2f;
        float translateZ = 0.0f;
        float rotation1x = 0.0f;
        float rotation1y = 1.0f;
        float rotation1z = 0.0f;
        float rotation2x = 1.0f;
        float rotation2y = 0.0f;
        float rotation2z = 0.0f;
        final String selected = Kore.animations.mode.getSelected();
        if(Kore.animations != null && Kore.animations.isToggled()) {
            switch (selected) {
                case "vertical spin": {
                    angle1 = (float)(System.currentTimeMillis() % 720L);
                    angle1 /= 2.0f;
                    rotation2x = 0.0f;
                    angle2 = 0.0f;
                    break;
                }
                case "spin": {
                    translateY = 0.8f;
                    angle1 = 60.0f;
                    angle2 = (float)(-System.currentTimeMillis() % 720L);
                    angle2 /= 2.0f;
                    rotation2z = 0.8f;
                    angle3 = 30.0f;
                    break;
                }
            }
        }
        GlStateManager.translate(translateX, translateY, translateZ);
        GlStateManager.rotate(angle1, rotation1x, rotation1y, rotation1z);
        GlStateManager.rotate(angle2, rotation2x, rotation2y, rotation2z);
        GlStateManager.rotate(angle3, 0.0f, 1.0f, 0.0f);
    }
}
