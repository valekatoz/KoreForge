package net.kore.ui.hud.impl;

import net.kore.Kore;
import net.kore.modules.render.InventoryDisplay;
import net.kore.ui.hud.DraggableComponent;
import net.kore.ui.hud.HudVec;
import net.kore.utils.StencilUtils;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.BlurUtils;
import net.kore.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class InventoryHud extends DraggableComponent
{
    public static final InventoryHud inventoryHUD;

    public InventoryHud() {
        this.setSize(182.0, (double)(80 - (Fonts.getPrimary().getHeight() - 4)));
        this.setPosition(Kore.inventoryDisplay.x.getValue(), Kore.inventoryDisplay.y.getValue());
    }

    public HudVec drawScreen() {
        GL11.glPushMatrix();
        super.drawScreen();
        final ScaledResolution scaledResolution = new ScaledResolution(Kore.mc);
        int blur = 0;

        double x;
        double y;

        switch (InventoryDisplay.defaultPosition.getSelected()) {
            default:
                x = 2; // Top left, x = 2
                y = 0; // Top left, y = 0

                this.setPosition(x, y);
                break;
            case "Top Right":
                x = scaledResolution.getScaledWidth() - 184; // Adjust x for top right
                y = 0; // Top left, y = 0

                this.setPosition(x, y);
                break;
            case "Bottom Left":
                x = 2; // Bottom left, x = 2
                y = scaledResolution.getScaledHeight() - 82; // Adjust y for bottom left

                this.setPosition(x, y);
                break;
            case "Bottom Right":
                x = scaledResolution.getScaledWidth() - 184; // Adjust x for bottom right
                y = scaledResolution.getScaledHeight() - 82; // Adjust y for bottom right

                this.setPosition(x, y);
                break;
            case "Custom":
                x = this.x;
                y = this.y;
                break;
        }

        final String selected = Kore.inventoryDisplay.blurStrength.getSelected();

        switch (selected) {
            case "Low": {
                blur = 7;
                break;
            }
            case "High": {
                blur = 15;
                break;
            }
        }
        final ScaledResolution resolution = new ScaledResolution(Kore.mc);
        StencilUtils.initStencil();
        StencilUtils.bindWriteStencilBuffer();
        RenderUtils.drawRoundedRect2(x, y + Fonts.getPrimary().getHeight() - 4.0, 182.0, 80 - (Fonts.getPrimary().getHeight() - 4), 5.0, Color.white.getRGB());
        StencilUtils.bindReadStencilBuffer(1);
        BlurUtils.renderBlurredBackground((float)blur, (float)resolution.getScaledWidth(), (float)resolution.getScaledHeight(), 0.0f, 0.0f, (float)scaledResolution.getScaledWidth(), (float)scaledResolution.getScaledHeight());
        StencilUtils.uninitStencil();
        this.drawBorderedRoundedRect((float)x, (float)y + Fonts.getPrimary().getHeight() - 4.0f, 182.0f, (float)(80 - (Fonts.getPrimary().getHeight() - 4)), 5.0f, 2.5f);
        Fonts.getSecondary().drawSmoothCenteredString("Inventory", (float)x + 90.0f, (float)y + Fonts.getPrimary().getHeight(), Color.white.getRGB());
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        for (int i = 9; i < 36; ++i) {
            if (i % 9 == 0) {
                y += 20.0;
            }
            final ItemStack stack = Kore.mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null) {
                Kore.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, (int)x + 2 + 20 * (i % 9), (int)y);
                this.renderItemOverlayIntoGUI(stack, x + 2.0 + 20 * (i % 9), y);
            }
        }
        RenderHelper.disableStandardItemLighting();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
        final InventoryDisplay display = Kore.inventoryDisplay;
        display.x.set(this.x);
        display.y.set(this.y);
        return new HudVec(x + this.width, y + this.height);
    }

    private void drawBorderedRoundedRect(final float x, final float y, final float width, final float height, final float radius, final float linewidth) {
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, radius, new Color(21, 21, 21, 50).getRGB());
        if (this.isHovered() && Kore.mc.currentScreen instanceof GuiChat) {
            RenderUtils.drawOutlinedRoundedRect(x, y, width, height, radius, linewidth, Color.white.getRGB());
        }
        else {
            RenderUtils.drawGradientOutlinedRoundedRect(x, y, width, height, radius, linewidth, Kore.themeManager.getSecondaryColor().getRGB(), Kore.themeManager.getSecondaryColor(3).getRGB(), Kore.themeManager.getSecondaryColor(6).getRGB(), Kore.themeManager.getSecondaryColor(9).getRGB());
        }
    }

    public void renderItemOverlayIntoGUI(final ItemStack itemStack, final double x, final double y) {
        if (itemStack != null) {
            if (itemStack.stackSize != 1) {
                String s = String.valueOf(itemStack.stackSize);
                if (itemStack.stackSize < 1) {
                    s = EnumChatFormatting.RED + String.valueOf(itemStack.stackSize);
                }
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableBlend();
                Fonts.getPrimary().drawSmoothStringWithShadow(s, x + 19.0 - 2.0 - Fonts.getPrimary().getStringWidth(s), y + 6.0 + 3.0, Color.white.getRGB());
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
            if (itemStack.getItem().showDurabilityBar(itemStack)) {
                final double health = itemStack.getItem().getDurabilityForDisplay(itemStack);
                final int j = (int)Math.round(13.0 - health * 13.0);
                final int i = (int)Math.round(255.0 - health * 255.0);
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                final Tessellator tessellator = Tessellator.getInstance();
                final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                this.draw(worldrenderer, x + 2.0, y + 13.0, 13, 2, 0, 0, 0, 255);
                this.draw(worldrenderer, x + 2.0, y + 13.0, 12, 1, (255 - i) / 4, 64, 0, 255);
                this.draw(worldrenderer, x + 2.0, y + 13.0, j, 1, 255 - i, i, 0, 255);
                GlStateManager.enableAlpha();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }
    }

    private void draw(final WorldRenderer p_draw_1_, final double p_draw_2_, final double p_draw_3_, final int p_draw_4_, final int p_draw_5_, final int p_draw_6_, final int p_draw_7_, final int p_draw_8_, final int p_draw_9_) {
        p_draw_1_.begin(7, DefaultVertexFormats.POSITION_COLOR);
        p_draw_1_.pos(p_draw_2_ + 0.0, p_draw_3_ + 0.0, 0.0).color(p_draw_6_, p_draw_7_, p_draw_8_, p_draw_9_).endVertex();
        p_draw_1_.pos(p_draw_2_ + 0.0, p_draw_3_ + p_draw_5_, 0.0).color(p_draw_6_, p_draw_7_, p_draw_8_, p_draw_9_).endVertex();
        p_draw_1_.pos(p_draw_2_ + p_draw_4_, p_draw_3_ + p_draw_5_, 0.0).color(p_draw_6_, p_draw_7_, p_draw_8_, p_draw_9_).endVertex();
        p_draw_1_.pos(p_draw_2_ + p_draw_4_, p_draw_3_ + 0.0, 0.0).color(p_draw_6_, p_draw_7_, p_draw_8_, p_draw_9_).endVertex();
        Tessellator.getInstance().draw();
    }

    static {
        inventoryHUD = new InventoryHud();
    }
}