package net.kore.modules.render;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.settings.StringSetting;
import net.kore.ui.ModernClickGui;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Gui extends Module {
    public ModernClickGui modernClickGui;
    public ModeSetting colorMode;
    public NumberSetting redCustom;
    public NumberSetting greenCustom;
    public NumberSetting blueCustom;
    public NumberSetting redShift1;
    public NumberSetting greenShift1;
    public NumberSetting blueShift1;
    public NumberSetting redShift2;
    public NumberSetting greenShift2;
    public NumberSetting blueShift2;
    public NumberSetting shiftSpeed;
    public NumberSetting rgbSpeed;
    public ModeSetting blur;
    public BooleanSetting scaleGui;
    public BooleanSetting arrayList;
    public BooleanSetting disableNotifs;
    public BooleanSetting arrayBlur;
    public BooleanSetting arrayOutline;
    public BooleanSetting waterMark;
    public BooleanSetting hsb;
    public static final StringSetting commandPrefix = new StringSetting("Prefix", ".", 1);

    public Gui() {
        super("Gui", Keyboard.KEY_RSHIFT, Module.Category.RENDER);
        this.colorMode = new ModeSetting("Theme", "Astolfo", new String[] { "Rainbow", "Gradient", "Astolfo", "Vape", "Mint", "Devil"})
        {
            @Override
            public void cycle(int key)
            {
                super.cycle(key);

                Kore.themeManager.setTheme(colorMode.getSelected());
            }
        };
        this.redCustom = new NumberSetting("Red", 0.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Custom") && !this.colorMode.is("Pulse"));
        this.greenCustom = new NumberSetting("Green", 80.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Custom") && !this.colorMode.is("Pulse"));
        this.blueCustom = new NumberSetting("Blue", 255.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Custom") && !this.colorMode.is("Pulse"));
        this.redShift1 = new NumberSetting("Red 1 ", 0.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Gradient"));
        this.greenShift1 = new NumberSetting("Green 1 ", 255.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Gradient"));
        this.blueShift1 = new NumberSetting("Blue 1 ", 110.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Gradient"));
        this.redShift2 = new NumberSetting("Red 2 ", 255.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Gradient"));
        this.greenShift2 = new NumberSetting("Green 2 ", 175.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Gradient"));
        this.blueShift2 = new NumberSetting("Blue 2 ", 255.0, 0.0, 255.0, 1.0, aBoolean -> !this.colorMode.is("Gradient"));
        this.shiftSpeed = new NumberSetting("Shift Speed ", 1.0, 0.1, 5.0, 0.1, aBoolean -> !this.colorMode.is("Gradient") && !this.colorMode.is("Pulse") && !this.colorMode.is("Astolfo"));
        this.rgbSpeed = new NumberSetting("Rainbow Speed", 2.5, 0.1, 5.0, 0.1, aBoolean -> !this.colorMode.is("Rainbow"));
        this.blur = new ModeSetting("Blur strength", "Low", new String[] { "None", "Low", "High" });
        this.scaleGui = new BooleanSetting("Scale gui", false);
        this.arrayList = new BooleanSetting("ArrayList", true);
        this.disableNotifs = new BooleanSetting("Disable notifications", false);
        this.arrayBlur = new BooleanSetting("Array background", true);
        this.arrayOutline = new BooleanSetting("Array line", true);
        this.waterMark = new BooleanSetting("Watermark", true);
        this.hsb = new BooleanSetting("HSB ", true, aBoolean -> !this.colorMode.is("Gradient"));

        setToggled(false);
        this.addSettings(this.colorMode, this.hsb, this.rgbSpeed, this.shiftSpeed, this.redCustom, this.greenCustom, this.blueCustom, this.redShift1, this.greenShift1, this.blueShift1, this.redShift2, this.greenShift2, this.blueShift2, Gui.commandPrefix, this.blur, this.waterMark, this.arrayList, this.arrayOutline, this.arrayBlur, this.disableNotifs, this.scaleGui);
    }

    @Override
    public void assign()
    {
        Kore.clickGui = this;
    }

    public float getHeight() {
        if (!this.arrayList.isEnabled()) {
            return 0.0f;
        }
        final List<Module> list = Kore.moduleManager.getModules().stream().filter(module ->  (module.isToggled() || module.toggledTime.getTimePassed() <= 250L)).sorted(Comparator.comparingDouble(module -> Fonts.getPrimary().getStringWidth(module.getName()))).collect(Collectors.toList());
        float y = 3.0f;
        for (final Module module2 : list) {
            y += (Fonts.getPrimary().getHeight() + 5.0f) * Math.max(Math.min(module2.isToggled() ? (module2.toggledTime.getTimePassed() / 250.0f) : ((250.0f - module2.toggledTime.getTimePassed()) / 250.0f), 1.0f), 0.0f);
        }
        return y;
    }

    @SubscribeEvent
    public void onRender(final RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            if (this.waterMark.isEnabled()) {
                if(Boolean.parseBoolean(Kore.licensed)) {
                    Fonts.getSecondary().drawSmoothString("ore", Fonts.getSecondary().drawSmoothString("K", 5.0, 5.0f, Color.white.darker().getRGB()) + 1.0f, 5.0f,Kore.themeManager.getSecondaryColor(0).getRGB());
                } else {
                    Fonts.getSecondary().drawSmoothString("(Unlicensed)", Fonts.getSecondary().drawSmoothString("ore", Fonts.getSecondary().drawSmoothString("K", 5.0, 5.0f, Color.white.darker().getRGB()) + 1.0f, 5.0f,Kore.themeManager.getSecondaryColor(0).getRGB()) + 3.0f, 5.0f,Color.white.darker().getRGB());
                }
            }
            if (this.arrayList.isEnabled()) {
                GL11.glPushMatrix();
                final ScaledResolution resolution = new ScaledResolution(Kore.mc);
                final List<Module> list = Kore.moduleManager.getModules().stream().filter(module -> (module.isToggled() || module.toggledTime.getTimePassed() <= 250L)).sorted(Comparator.comparingDouble(module -> Fonts.getPrimary().getStringWidth(module.getName() + " | " + module.suffix()))).collect(Collectors.toList());
                Collections.reverse(list);
                float y = 2.0f;
                int x = list.size();
                for (final Module module2 : list) {
                    --x;
                    GL11.glPushMatrix();
                    String moduleName = module2.getName() + " | " + module2.suffix();
                    if (module2.suffix() == "")
                    {
                        moduleName = module2.getName();
                    }
                    final float width = (float)(Fonts.getPrimary().getStringWidth(moduleName) + 5.0);
                    final float translatedWidth = width * Math.max(Math.min(module2.isToggled() ? ((250.0f - module2.toggledTime.getTimePassed()) / 250.0f) : (module2.toggledTime.getTimePassed() / 250.0f), 1.0f), 0.0f);
                    GL11.glTranslated((double)translatedWidth, 0.0, 0.0);
                    final float height = (float)(Fonts.getPrimary().getHeight() + 5);
                    if (this.arrayBlur.isEnabled()) {
                        for (float i = 0.0f; i < 3.0f; i += 0.5f) {
                            RenderUtils.drawRect(resolution.getScaledWidth() - 1 - width - i, y + i, (float)resolution.getScaledWidth(), y + (Fonts.getPrimary().getHeight() + 5.0f) * Math.max(Math.min(module2.isToggled() ? (module2.toggledTime.getTimePassed() / 250.0f) : ((250.0f - module2.toggledTime.getTimePassed()) / 250.0f), 1.0f), 0.0f) + i, new Color(20, 20, 20, 40).getRGB());
                        }
                        RenderUtils.drawRect(resolution.getScaledWidth() - 1 - width, y, (float)(resolution.getScaledWidth() - 1), y + height, new Color(19, 19, 19, 70).getRGB());
                    }
                    Fonts.getPrimary().drawSmoothCenteredString(moduleName, resolution.getScaledWidth() - 1 - width / 2.0f + 0.4f, y + height / 2.0f - Fonts.getPrimary().getHeight() / 2.0f + 0.5f, new Color(20, 20, 20).getRGB());
                    Fonts.getPrimary().drawSmoothCenteredString(moduleName, resolution.getScaledWidth() - 1 - width / 2.0f, y + height / 2.0f - Fonts.getPrimary().getHeight() / 2.0f, Kore.themeManager.getSecondaryColor(x).getRGB(), Kore.themeManager.getSecondaryColor(x - 1).getRGB());
                    y += (Fonts.getPrimary().getHeight() + 5) * Math.max(Math.min(module2.isToggled() ? (module2.toggledTime.getTimePassed() / 250.0f) : ((250.0f - module2.toggledTime.getTimePassed()) / 250.0f), 1.0f), 0.0f);
                    GL11.glPopMatrix();
                }
                x = list.size();
                y = 2.0f;
                if (this.arrayOutline.isEnabled()) {
                    final Tessellator tessellator = Tessellator.getInstance();
                    final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
                    GlStateManager.enableBlend();
                    GlStateManager.disableTexture2D();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    GlStateManager.shadeModel(7425);
                    worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    for (final Module module3 : list) {
                        --x;
                        final float height = (Fonts.getPrimary().getHeight() + 5.0f) * Math.max(Math.min(module3.isToggled() ? (module3.toggledTime.getTimePassed() / 250.0f) : ((250.0f - module3.toggledTime.getTimePassed()) / 250.0f), 1.0f), 0.0f);
                        addVertex((float)(resolution.getScaledWidth() - 1), y, (float)resolution.getScaledWidth(), y + height, Kore.themeManager.getSecondaryColor(x - 1).getRGB(), Kore.themeManager.getSecondaryColor(x).getRGB());
                        y += height;
                    }
                    tessellator.draw();
                    GlStateManager.shadeModel(7424);
                }
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
        }
    }

    public static void addVertex(float left, float top, float right, float bottom, final int color, final int color2) {
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
        worldrenderer.pos((double)right, (double)bottom, 0.0).color(ff4, ff5, ff6, ff3).endVertex();
        worldrenderer.pos((double)right, (double)top, 0.0).color(f4, f5, f6, f3).endVertex();
        worldrenderer.pos((double)left, (double)top, 0.0).color(f4, f5, f6, f3).endVertex();
    }

    @Override
    public void onEnable()
    {
        if (modernClickGui == null)
            modernClickGui = new ModernClickGui();
        Kore.mc.displayGuiScreen(modernClickGui);
    }

    @Override
    public void onDisable()
    {
        Kore.mc.displayGuiScreen(null);
    }

    public Color getColor() {
        return Kore.themeManager.getSecondaryColor(0);
    }

    public Color getColor(int index) {
        return Kore.themeManager.getSecondaryColor(index);
    }
}
