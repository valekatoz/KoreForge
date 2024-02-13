package net.kore.ui.hud.impl;

import net.kore.Kore;
import net.kore.modules.combat.KillAura;
import net.kore.modules.render.TargetDisplay;
import net.kore.ui.hud.DraggableComponent;
import net.kore.ui.hud.HudVec;
import net.kore.utils.MathUtils;
import net.kore.utils.MilliTimer;
import net.kore.utils.StencilUtils;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.RenderUtils;
import net.kore.utils.render.shader.BlurUtils;
import net.minecraft.entity.Entity;
import net.minecraft.client.gui.inventory.GuiInventory;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.entity.EntityLivingBase;

public class TargetHud extends DraggableComponent
{
    public static final TargetHud INSTANCE;
    private float lastHp;
    private static EntityLivingBase lastEntity;
    private static final MilliTimer resetTimer;
    private static final MilliTimer startTimer;

    public TargetHud() {
        this.lastHp = 0.8f;
        this.setSize(150.0, 50.0);
        this.setPosition(Kore.targetDisplay.x.getValue(), Kore.targetDisplay.y.getValue());
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @Override
    public HudVec drawScreen() {
        return this.drawScreen((EntityLivingBase)((KillAura.target == null && Kore.mc.currentScreen instanceof GuiChat) ? Kore.mc.thePlayer : KillAura.target));
    }

    public HudVec drawScreen(final EntityLivingBase entity) {
        if (entity != null) {
            if (TargetHud.lastEntity == null) {
                TargetHud.startTimer.reset();
            }
            TargetHud.resetTimer.reset();
        }
        if (TargetHud.resetTimer.hasTimePassed(750L) || entity != null) {
            TargetHud.lastEntity = entity;
        }
        if (TargetHud.lastEntity != null) {
            super.drawScreen();
            final double x = this.getX();
            final double y = this.getY();
            GL11.glPushMatrix();
            int blur = 0;
            final String selected = TargetDisplay.getInstance().blurStrength.getSelected();
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
            GL11.glPushMatrix();
            scale(x + 75.0, y + 25.0, 0.0);
            StencilUtils.initStencil();
            StencilUtils.bindWriteStencilBuffer();
            RenderUtils.drawRoundedRect2(x, y, 150.0, 50.0, 5.0, Color.black.getRGB());
            StencilUtils.bindReadStencilBuffer(1);
            GL11.glPopMatrix();
            BlurUtils.renderBlurredBackground((float)blur, (float)resolution.getScaledWidth(), (float)resolution.getScaledHeight(), 0.0f, 0.0f, (float)resolution.getScaledWidth(), (float)resolution.getScaledHeight());
            StencilUtils.uninitStencil();
            scale(x + 75.0, y + 25.0, 0.0);
            final float hp = this.lastHp + (getHp() - this.lastHp) / (7.0f * (Minecraft.getDebugFPS() / 20.0f));
            if (Math.abs(hp - this.lastHp) < 0.001f) {
                this.lastHp = hp;
            }
            if (Kore.mc.currentScreen instanceof GuiChat && this.isHovered()) {
                RenderUtils.drawBorderedRoundedRect((float)x, (float)y, 150.0f, 50.0f, 5.0f, 2.0f, new Color(21, 21, 21, 52).getRGB(), Color.white.getRGB());
            }
            else {
                RenderUtils.drawRoundedRect2(x, y, 150.0, 50.0, 5.0, new Color(21, 21, 21, 52).getRGB());
            }
            Fonts.getPrimary().drawSmoothStringWithShadow(ChatFormatting.stripFormatting(TargetHud.lastEntity.getName()), x + 5.0, y + 6.0, Kore.clickGui.getColor(0).brighter().getRGB());
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            final int i = 0;
            try {
                final EntityLivingBase lastEntity = TargetHud.lastEntity;
                lastEntity.posY += 1000.0;
                GuiInventory.drawEntityOnScreen((int)(x + 130.0), (int)(y + 40.0), (int)(35.0 / Math.max(TargetHud.lastEntity.height, 1.5)), 20.0f, 10.0f, TargetHud.lastEntity);
                final EntityLivingBase lastEntity2 = TargetHud.lastEntity;
                lastEntity2.posY -= 1000.0;
            }
            catch (Exception ex) {}
            Fonts.getSecondary().drawSmoothStringWithShadow((int)(TargetHud.lastEntity.getDistanceToEntity((Entity)Kore.mc.thePlayer) * 10.0f) / 10.0 + "m", x + 5.0, y + 11.0 + Fonts.getSecondary().getHeight(), new Color(231, 231, 231).getRGB());
            final String text = String.format("%.1f", getHp() * 100.0f) + "%";
            RenderUtils.drawRoundedRect(x + 10.0, y + 42.0, x + 140.0, y + 46.0, 2.0, Color.HSBtoRGB(0.0f, 0.0f, 0.1f));
            if (hp > 0.05) {
                RenderUtils.drawRoundedRect(x + 10.0, y + 42.0, x + 140.0f * hp, y + 46.0, 2.0, Kore.clickGui.getColor(0).getRGB());
            }
            Fonts.getSecondary().drawSmoothStringWithShadow(text, x + 75.0 - Fonts.getSecondary().getStringWidth(text) / 2.0, y + 33.0, new Color(231, 231, 231).getRGB());
            this.lastHp = hp;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }

        Kore.targetDisplay.x.set(this.x);
        Kore.targetDisplay.y.set(this.y);

        return new HudVec(this.x + this.getWidth(), this.y + this.getHeight());
    }

    private static float getHp() {
        if (TargetHud.lastEntity == null) {
            return 0.0f;
        }
        return MathUtils.clamp(TargetHud.lastEntity.getHealth() / TargetHud.lastEntity.getMaxHealth(), 1.0f, 0.0f);
    }

    private static void scale(final double x, final double y, final double startingSize) {
        final ScaledResolution resolution = new ScaledResolution(Kore.mc);
        if (TargetHud.resetTimer.hasTimePassed(550L)) {
            RenderUtils.doScale(x, y, (750L - TargetHud.resetTimer.getTimePassed()) / 200.0);
        }
        else if (!TargetHud.startTimer.hasTimePassed(200L)) {
            RenderUtils.doScale(x, y, TargetHud.startTimer.getTimePassed() / 200.0 * (1.0 - startingSize + startingSize));
        }
    }

    static {
        INSTANCE = new TargetHud();
        resetTimer = new MilliTimer();
        startTimer = new MilliTimer();
    }
}