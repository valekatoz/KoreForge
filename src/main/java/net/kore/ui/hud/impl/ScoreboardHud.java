/*
    TODO: fix bug that has the Component X and Y under the rendered scoreboard.
*/

package net.kore.ui.hud.impl;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.kore.Kore;
import net.kore.modules.misc.BuildGuesser;
import net.kore.modules.render.InventoryDisplay;
import net.kore.modules.render.ModernInterfaces;
import net.kore.ui.hud.DraggableComponent;
import net.kore.ui.hud.HudVec;
import net.kore.utils.StencilUtils;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.RenderUtils;
import net.kore.utils.render.shader.BlurUtils;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.*;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScoreboardHud extends DraggableComponent {
    public static final ScoreboardHud scoreboardHud;
    private double savedX, savedY;

    public ScoreboardHud() {
        savedX = Kore.modernInterfaces.scoreboardX.getValue();
        savedY = Kore.modernInterfaces.scoreboardY.getValue();
        this.setPosition(Kore.modernInterfaces.scoreboardX.getValue(), Kore.modernInterfaces.scoreboardY.getValue());
    }

    public HudVec drawScreen(final ScoreObjective p_180475_1_, final ScaledResolution p_180475_2_, final boolean customFont) {
        super.drawScreen();
        final Scoreboard scoreboard = p_180475_1_.getScoreboard();
        Collection<Score> collection = (Collection<Score>)scoreboard.getSortedScores(p_180475_1_);
        final List<Score> list = collection.stream().filter(p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")).collect(Collectors.toList());
        if (list.size() > 15) {
            collection = (Collection<Score>) Lists.newArrayList(Iterables.skip((Iterable)list, collection.size() - 15));
        }
        else {
            collection = list;
        }
        float width = this.getStringWidth(p_180475_1_.getDisplayName(), customFont);
        final int fontHeight = customFont ? (Fonts.getPrimary().getHeight() + 2) : Kore.mc.fontRendererObj.FONT_HEIGHT;
        for (final Score score : collection) {
            final ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
            final String s = ScorePlayerTeam.formatPlayerName((Team)scoreplayerteam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            width = Math.max(width, this.getStringWidth(s, customFont));
        }
        final float i1 = (float)(collection.size() * fontHeight);
        final float arrayHeight = Kore.clickGui.getHeight();
        float j1;
        final float k1 = 3.0f;
        float l1;
        float m;

        int blur = 0;
        final String selected = Kore.modernInterfaces.blurStrength.getSelected();
        switch (selected) {
            case "Low": {
                blur = 7;
                break;
            }
            case "High": {
                blur = 25;
                break;
            }
        }

        if(ModernInterfaces.defaultPosition.getSelected().equals("Custom")) {
            l1 = (float) this.getX();
            j1 = (float) this.getY();
            m = l1 + width;

            if (blur > 0 && !Kore.modernInterfaces.outline.isEnabled()) {
                for (float i2 = 0.5f; i2 < 3.0f; i2 += 0.5f) {
                    RenderUtils.drawRoundedRect2(l1 - 2.0f - i2, j1 + i2, m - (l1 - 2.0f) +2.0f, fontHeight * (collection.size() + 1) + 4, 5.0, new Color(20, 20, 20, 40).getRGB());
                }
            }
            StencilUtils.initStencil();
            StencilUtils.bindWriteStencilBuffer();
            RenderUtils.drawRoundedRect2(l1 - 2.0f, j1, m - (l1 - 2.0f) +2.0f, fontHeight * (collection.size() + 1) + 4, 5.0, new Color(21, 21, 21, 50).getRGB());
            StencilUtils.bindReadStencilBuffer(1);
            BlurUtils.renderBlurredBackground((float)blur, (float)p_180475_2_.getScaledWidth(), (float)p_180475_2_.getScaledHeight(), l1 - 2.0f, j1, m - (l1 - 2.0f), (float)(fontHeight * (collection.size() + 1) + 4));
            StencilUtils.uninitStencil();
            if (Kore.modernInterfaces.outline.isEnabled()) {
                this.drawBorderedRoundedRect(l1 - 2.0f, j1, m - (l1 - 2.0f) +2.0f, (float)(fontHeight * (collection.size() + 1) + 4), 5.0f, 2.5f);
            }
            else {
                RenderUtils.drawRoundedRect2(l1 - 2.0f, j1, m - (l1 - 2.0f) +2.0f, fontHeight * (collection.size() + 1) + 4, 5.0, new Color(21, 21, 21, 50).getRGB());
            }
            int i3 = 0;
            for (final Score score2 : collection) {
                ++i3;
                final ScorePlayerTeam scoreplayerteam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
                String s2 = ScorePlayerTeam.formatPlayerName((Team)scoreplayerteam2, score2.getPlayerName());
                if (s2.contains(Kore.fancy + "ewww.hypixel.ne\ud83c\udf82" + Kore.fancy + "et") && Kore.clickGui.waterMark.isEnabled()) {
                    s2 = s2.replaceAll(Kore.fancy + "ewww.hypixel.ne\ud83c\udf82" + Kore.fancy + "et", "Kore Client");
                }
                final float k2 = (j1 + (float)(fontHeight * (collection.size() + 1) + 4)) - i3 * fontHeight;
                final Matcher matcher = Pattern.compile("[0-9][0-9]/[0-9][0-9]/[0-9][0-9]").matcher(s2);
                if (Kore.modernInterfaces.hideLobby.isEnabled() && matcher.find()) {
                    s2 = ChatFormatting.GRAY + matcher.group();
                }
                final boolean flag = s2.equals("Kore Client");
                if (flag) {
                    if (customFont) {
                        Fonts.getPrimary().drawSmoothCenteredStringWithShadow(s2, l1 + width / 2.0f, k2, Kore.themeManager.getSecondaryColor().getRGB());
                    }
                    else {
                        Kore.mc.fontRendererObj.drawString(s2, (int)(l1 + width / 2.0f - Kore.mc.fontRendererObj.getStringWidth(s2) / 2), (int)k2, Kore.themeManager.getSecondaryColor().getRGB());
                    }
                }
                else {
                    this.drawString(s2, l1, k2, 553648127, customFont);
                }
                if (i3 == collection.size()) {
                    final String s3 = p_180475_1_.getDisplayName();
                    this.drawString(s3, l1 + width / 2.0f - this.getStringWidth(s3, customFont) / 2.0f, k2 - fontHeight, Color.white.getRGB(), customFont);
                }
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f);
        } else {
            l1 = p_180475_2_.getScaledWidth() - width - k1;
            j1 = p_180475_2_.getScaledHeight() / 2.0f + i1 / 3.0f;
            if (Kore.clickGui.arrayList.isEnabled()) {
                j1 = Math.max(j1, arrayHeight + 40.0f + (collection.size() * fontHeight - fontHeight - 3));
            }
            m = p_180475_2_.getScaledWidth() - k1 + 2.0f;
            this.setPosition(l1 -2.0f,j1 - fontHeight * (collection.size() + 1) + 4);

            if (blur > 0 && !Kore.modernInterfaces.outline.isEnabled()) {
                for (float i2 = 0.5f; i2 < 3.0f; i2 += 0.5f) {
                    RenderUtils.drawRoundedRect2(l1 - 2.0f - i2, j1 - collection.size() * fontHeight - fontHeight - 3.0f + i2, m - (l1 - 2.0f), fontHeight * (collection.size() + 1) + 4, 5.0, new Color(20, 20, 20, 40).getRGB());
                }
            }
            StencilUtils.initStencil();
            StencilUtils.bindWriteStencilBuffer();
            RenderUtils.drawRoundedRect2(l1 - 2.0f, j1 - collection.size() * fontHeight - fontHeight - 3.0f, m - (l1 - 2.0f), fontHeight * (collection.size() + 1) + 4, 5.0, new Color(21, 21, 21, 50).getRGB());
            StencilUtils.bindReadStencilBuffer(1);
            BlurUtils.renderBlurredBackground((float)blur, (float)p_180475_2_.getScaledWidth(), (float)p_180475_2_.getScaledHeight(), l1 - 2.0f, j1 - collection.size() * fontHeight - fontHeight - 3.0f, m - (l1 - 2.0f), (float)(fontHeight * (collection.size() + 1) + 4));
            StencilUtils.uninitStencil();
            if (Kore.modernInterfaces.outline.isEnabled()) {
                this.drawBorderedRoundedRect(l1 - 2.0f, j1 - collection.size() * fontHeight - fontHeight - 3.0f, m - (l1 - 2.0f), (float)(fontHeight * (collection.size() + 1) + 4), 5.0f, 2.5f);
            }
            else {
                RenderUtils.drawRoundedRect2(l1 - 2.0f, j1 - collection.size() * fontHeight - fontHeight - 3.0f, m - (l1 - 2.0f), fontHeight * (collection.size() + 1) + 4, 5.0, new Color(21, 21, 21, 50).getRGB());
            }
            int i3 = 0;
            for (final Score score2 : collection) {
                ++i3;
                final ScorePlayerTeam scoreplayerteam2 = scoreboard.getPlayersTeam(score2.getPlayerName());
                String s2 = ScorePlayerTeam.formatPlayerName((Team)scoreplayerteam2, score2.getPlayerName());
                if (s2.contains(Kore.fancy + "ewww.hypixel.ne\ud83c\udf82" + Kore.fancy + "et") && Kore.clickGui.waterMark.isEnabled()) {
                    s2 = s2.replaceAll(Kore.fancy + "ewww.hypixel.ne\ud83c\udf82" + Kore.fancy + "et", "Kore Client");
                }
                final float k2 = j1 - i3 * fontHeight;
                final Matcher matcher = Pattern.compile("[0-9][0-9]/[0-9][0-9]/[0-9][0-9]").matcher(s2);
                if (Kore.modernInterfaces.hideLobby.isEnabled() && matcher.find()) {
                    s2 = ChatFormatting.GRAY + matcher.group();
                }
                final boolean flag = s2.equals("Kore Client");
                if (flag) {
                    if (customFont) {
                        Fonts.getPrimary().drawSmoothCenteredStringWithShadow(s2, l1 + width / 2.0f, k2, Kore.themeManager.getSecondaryColor().getRGB());
                    }
                    else {
                        Kore.mc.fontRendererObj.drawString(s2, (int)(l1 + width / 2.0f - Kore.mc.fontRendererObj.getStringWidth(s2) / 2), (int)k2, Kore.themeManager.getSecondaryColor().getRGB());
                    }
                }
                else {
                    this.drawString(s2, l1, k2, 553648127, customFont);
                }
                if (i3 == collection.size()) {
                    final String s3 = p_180475_1_.getDisplayName();
                    this.drawString(s3, l1 + width / 2.0f - this.getStringWidth(s3, customFont) / 2.0f, k2 - fontHeight, Color.white.getRGB(), customFont);
                }
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f);
        }

        this.setSize(m - (l1 - 2.0f), fontHeight * (collection.size() + 1) + 4);

        Kore.modernInterfaces.scoreboardX.set(this.x);
        Kore.modernInterfaces.scoreboardY.set(this.y);

        return new HudVec(l1 + (m - (l1 - 2.0f)), j1 + (fontHeight * (collection.size() + 1) + 4));
    }

    private void drawBorderedRoundedRect(final float x, final float y, final float width, final float height, final float radius, final float linewidth) {
        RenderUtils.drawRoundedRect(x, y, x + width, y + height, radius, new Color(21, 21, 21, 50).getRGB());
        if(super.isHovered() && Kore.mc.currentScreen instanceof GuiChat) {
            RenderUtils.drawOutlinedRoundedRect(x, y, width, height, radius, linewidth, Color.white.getRGB());
        } else {
            RenderUtils.drawGradientOutlinedRoundedRect(x, y, width, height, radius, linewidth, Kore.themeManager.getSecondaryColor(0).getRGB(), Kore.themeManager.getSecondaryColor(3).getRGB(), Kore.themeManager.getSecondaryColor(6).getRGB(), Kore.themeManager.getSecondaryColor(9).getRGB());
        }
    }

    private void drawString(String s, final float x, final float y, final int color, final boolean customFont) {
        if (Kore.nickHider.isToggled() && s.contains(Kore.mc.getSession().getUsername())) {
            s = s.replaceAll(Kore.mc.getSession().getUsername(), Kore.nickHider.nick.getValue());
        }
        if (customFont) {
            Fonts.getPrimary().drawSmoothStringWithShadow(s, x, y, Color.white.getRGB());
        }
        else {
            Kore.mc.fontRendererObj.drawString(s, (int)x, (int)y, color);
        }
    }

    private float getStringWidth(final String s, final boolean customFont) {
        return customFont ? ((float)Fonts.getPrimary().getStringWidth(s)) : ((float)Kore.mc.fontRendererObj.getStringWidth(s));
    }

    static {
        scoreboardHud = new ScoreboardHud();
    }

}
