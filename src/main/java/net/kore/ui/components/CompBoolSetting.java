package net.kore.ui.components;

import net.kore.Kore;
import net.kore.settings.BooleanSetting;
import net.kore.ui.ModernClickGui;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.RenderUtils;

import java.awt.*;

public class CompBoolSetting extends Comp {
    private final BooleanSetting booleanSetting;

    public CompBoolSetting(double x, double y, BooleanSetting tickSetting) {
        this.x = x;
        this.y = y;
        this.booleanSetting = tickSetting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, double scrollY) {
        int insideC = this.booleanSetting.isEnabled() ? Kore.themeManager.getSecondaryColor().getRGB() : Kore.themeManager.getPrimaryColor().getRGB();
        RenderUtils.drawBorderedRoundedRect((float) (ModernClickGui.getX() + this.x), (float) (ModernClickGui.getY() + this.y), 10.0f, 10.0f, 3, 1, insideC, Kore.themeManager.getSecondaryColor().getRGB());

        if (booleanSetting.isEnabled())
            Fonts.icon.drawString("D", ModernClickGui.getX() + x, (ModernClickGui.getY() + y + 3), Color.WHITE.getRGB());

        Fonts.getPrimary().drawString(this.booleanSetting.name, ModernClickGui.getX() + this.x + 15.0, ModernClickGui.getY() + this.y + 2.0, Color.WHITE.getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.isHovered(mouseX, mouseY, ModernClickGui.getX() + this.x, ModernClickGui.getY() + this.y, 10.0, 10.0) && mouseButton == 0) {
            this.booleanSetting.toggle();
        }
    }
}
