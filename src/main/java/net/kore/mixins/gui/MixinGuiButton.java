package net.kore.mixins.gui;

import net.kore.Kore;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiButton.class)
public abstract class MixinGuiButton extends MixinGui
{
    @Shadow
    public boolean visible;
    @Shadow
    @Final
    protected static ResourceLocation buttonTextures;
    @Shadow
    public int height;
    @Shadow
    protected boolean hovered;
    @Shadow
    public int xPosition;
    @Shadow
    public int yPosition;
    @Shadow
    public int width;
    @Shadow (remap = false)
    public int packedFGColour;
    @Shadow
    public boolean enabled;
    @Shadow
    public String displayString;

    @Shadow
    public abstract int getButtonWidth();

    @Shadow
    protected abstract int getHoverState(final boolean p0);

    @Shadow
    protected abstract void mouseDragged(final Minecraft p0, final int p1, final int p2);

    @Inject(method = { "drawButton" }, at = { @At("HEAD") }, cancellable = true)
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final CallbackInfo callbackInfo) {
        if (this.visible && Kore.interfaces != null && Kore.interfaces.customButtons.isEnabled()) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            for (float i = 0.0f; i < 2.0f; i += 0.5) {
                RenderUtils.drawRoundedRect(this.xPosition - i, this.yPosition + i, this.xPosition + this.width - i, this.yPosition + this.height + i, 2.0, new Color(21, 21, 21, 30).getRGB());
            }
            RenderUtils.drawRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 2.0, new Color(21, 21, 21, 180).getRGB());
            this.drawGradientRect(0.0f, 255);
            this.mouseDragged(mc, mouseX, mouseY);
            Fonts.getPrimary().drawSmoothCenteredString(this.displayString, this.xPosition + this.width / 2.0f, this.yPosition + (this.height - Fonts.getPrimary().getHeight()) / 2.0f, this.hovered ? Color.white.getRGB() : new Color(200, 200, 200).getRGB());
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            callbackInfo.cancel();
        }
    }

    public void drawGradientRect(final float expand, final int opacity) {
        if (Kore.interfaces != null && Kore.interfaces.buttonLine.is("Wave")) {
            this.start2ColorDraw();
            float prevPos = (float)this.xPosition;
            for (int i = 1; i < 11; ++i) {
                final float pos = this.xPosition + i * 0.1f * this.width;
                if (Kore.interfaces.lineLocation.is("Top")) {
                    this.addVertexes(prevPos - expand, this.yPosition - expand, pos + expand, this.yPosition + 1.5f + expand, RenderUtils.applyOpacity(Kore.themeManager.getSecondaryColor(i), opacity).getRGB(), RenderUtils.applyOpacity(Kore.themeManager.getSecondaryColor(i + 1), opacity).getRGB());
                }
                else {
                    this.addVertexes(prevPos - expand, this.yPosition - expand + this.height - 1.5f, pos + expand, this.yPosition + this.height + expand, RenderUtils.applyOpacity(Kore.themeManager.getSecondaryColor(i), opacity).getRGB(), RenderUtils.applyOpacity(Kore.themeManager.getSecondaryColor(i + 1), opacity).getRGB());
                }
                prevPos = pos;
            }
            this.end2ColorDraw();
        }
        else if (Kore.interfaces != null && Kore.interfaces.buttonLine.is("Single")) {
            if (Kore.interfaces.lineLocation.is("Top")) {
                RenderUtils.drawRect(this.xPosition - expand, this.yPosition - expand, this.xPosition + this.width + expand, this.yPosition + 1.5f + expand, RenderUtils.applyOpacity(Kore.themeManager.getSecondaryColor(), opacity).getRGB());
            }
            else {
                RenderUtils.drawRect(this.xPosition - expand, this.yPosition - expand + this.height - 1.5f, this.xPosition + this.width + expand, this.yPosition + this.height + expand, RenderUtils.applyOpacity(Kore.themeManager.getSecondaryColor(), opacity).getRGB());
            }
        }
    }

    public void start2ColorDraw() {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        final WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
    }

    public void end2ColorDraw() {
        Tessellator.getInstance().draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public void addVertexes(float left, float top, float right, float bottom, final int color, final int color2) {
        if (left < right) {
            final float i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            final float j = top;
            top = bottom;
            bottom = j;
        }
        final float f3 = (color >> 24 & 0xFF) / 255.0f;
        final float f4 = (color >> 16 & 0xFF) / 255.0f;
        final float f5 = (color >> 8 & 0xFF) / 255.0f;
        final float f6 = (color & 0xFF) / 255.0f;
        final float ff3 = (color2 >> 24 & 0xFF) / 255.0f;
        final float ff4 = (color2 >> 16 & 0xFF) / 255.0f;
        final float ff5 = (color2 >> 8 & 0xFF) / 255.0f;
        final float ff6 = (color2 & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.pos((double)left, (double)bottom, 0.0).color(ff4, ff5, ff6, ff3).endVertex();
        worldrenderer.pos((double)right, (double)bottom, 0.0).color(f4, f5, f6, f3).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0).color(f4, f5, f6, f3).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0).color(ff4, ff5, ff6, ff3).endVertex();
    }
}

