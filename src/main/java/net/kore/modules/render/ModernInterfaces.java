package net.kore.modules.render;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.kore.Kore;
import net.kore.events.GuiChatEvent;
import net.kore.events.ScoreboardRenderEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.ui.hud.DraggableComponent;
import net.kore.ui.hud.impl.GuesserHud;
import net.kore.ui.hud.impl.ScoreboardHud;
import net.kore.utils.StencilUtils;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.shader.BlurUtils;
import net.kore.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModernInterfaces extends Module
{
    public static ModeSetting defaultPosition;
    public NumberSetting scoreboardX;
    public NumberSetting scoreboardY;
    public BooleanSetting customScoreboard;
    public BooleanSetting customFont;
    public BooleanSetting outline;
    public BooleanSetting hideLobby;
    public BooleanSetting customButtons;
    public BooleanSetting customChat;
    public BooleanSetting customChatFont;
    public ModeSetting blurStrength;
    public ModeSetting buttonLine;
    public ModeSetting lineLocation;
    public BooleanSetting roundedButton = new BooleanSetting("Round Button", true, aBoolean -> !customButtons.isEnabled());

    public ModernInterfaces() {
        super("Modern Interfaces", Category.RENDER);
        this.setToggled(true);
        this.customScoreboard = new BooleanSetting("Custom Scoreboard", true);
        this.scoreboardX = new NumberSetting("scoreboardX", (double) new ScaledResolution(Kore.mc).getScaledWidth() /2, -100000.0, 100000.0, 1.0E-5, a -> true);
        this.scoreboardY = new NumberSetting("scoreboardY", (double) new ScaledResolution(Kore.mc).getScaledHeight() /2, -100000.0, 100000.0, 1.0E-5, a -> true);
        defaultPosition = new ModeSetting("Position", "Default", new String[] { "Default", "Custom"});
        this.customFont = new BooleanSetting("Scoreboard Font", true) {
            @Override
            public boolean isHidden() {
                return !ModernInterfaces.this.customScoreboard.isEnabled();
            }
        };
        this.outline = new BooleanSetting("Scoreboard Outline", false) {
            @Override
            public boolean isHidden() {
                return !ModernInterfaces.this.customScoreboard.isEnabled();
            }
        };
        this.hideLobby = new BooleanSetting("Hide Lobby", false) {
            @Override
            public boolean isHidden() {
                return !ModernInterfaces.this.customScoreboard.isEnabled();
            }
        };
        this.customButtons = new BooleanSetting("Custom Buttons", true);
        this.customChat = new BooleanSetting("Custom Chat", true);
        this.customChatFont = new BooleanSetting("Chat Font", true, aBoolean -> !this.customChat.isEnabled());
        this.blurStrength = new ModeSetting("Blur Strength", "Low", new String[] { "None", "Low", "High" }) {
            @Override
            public boolean isHidden() {
                return !ModernInterfaces.this.customScoreboard.isEnabled();
            }
        };
        this.buttonLine = new ModeSetting("Button line", "None", new String[] { "Wave", "Single", "None" }) {
            @Override
            public boolean isHidden() {
                return !ModernInterfaces.this.customButtons.isEnabled();
            }
        };
        this.lineLocation = new ModeSetting("Line location", "Top", new String[] { "Top", "Bottom" }) {
            @Override
            public boolean isHidden() {
                return !ModernInterfaces.this.customButtons.isEnabled() || ModernInterfaces.this.buttonLine.is("None");
            }
        };

        this.addSettings("Client", this.customButtons, roundedButton, this.buttonLine, this.lineLocation);
        this.addSettings("Chat", this.customChat, this.customChatFont);
        this.addSettings("Scoreboard", this.customScoreboard, this.customFont, this.outline, this.hideLobby, this.blurStrength, defaultPosition, this.scoreboardX, this.scoreboardY);
    }

    @Override
    public void assign()
    {
        Kore.modernInterfaces = this;
    }

    @SubscribeEvent
    public void onChatEvent(final GuiChatEvent event) {
        if (!this.isToggled()) {
            return;
        }

        final DraggableComponent component = ScoreboardHud.scoreboardHud;

        if (event instanceof GuiChatEvent.MouseClicked) {
            if (component.isHovered(event.mouseX, event.mouseY)) {
                defaultPosition.setSelected("Custom");
                component.startDragging();
            }
        }
        else if (event instanceof GuiChatEvent.MouseReleased) {
            component.stopDragging();
        }
        else if (event instanceof GuiChatEvent.Closed) {
            component.stopDragging();
        }
        else if (event instanceof GuiChatEvent.DrawChatEvent) {

        }
    }

    @SubscribeEvent
    public void onDraw(final ScoreboardRenderEvent event) {
        if (!this.isToggled() || !this.customScoreboard.isEnabled()) {
            return;
        }
        event.setCanceled(true);
        ScoreboardHud.scoreboardHud.drawScreen(event.objective, event.resolution, this.customFont.isEnabled());
    }
}
