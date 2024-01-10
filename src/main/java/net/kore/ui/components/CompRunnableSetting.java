package net.kore.ui.components;

import net.kore.Kore;
import net.kore.settings.RunnableSetting;
import net.kore.ui.ModernClickGui;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.RenderUtils;

import java.awt.*;

public class CompRunnableSetting extends Comp {
    public RunnableSetting runnableSetting;
    public CompRunnableSetting(int x, int y, RunnableSetting runnableSetting)
    {
        this.x = x;
        this.y = y;
        this.runnableSetting = runnableSetting;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, double scrollY)
    {
        RenderUtils.drawBorderedRoundedRect((float) (ModernClickGui.getX() + x), (float) (ModernClickGui.getY() + y), (float) (ModernClickGui.getWidth() - x - 5), 15, 5, 1, Kore.themeManager.getPrimaryColor().getRGB(), Kore.themeManager.getSecondaryColor().getRGB());

        Fonts.getPrimary().drawCenteredString(runnableSetting.name, (float) (ModernClickGui.getX() + x + (ModernClickGui.getWidth() - x)/2), (float) (ModernClickGui.getY() + y + 3), Color.WHITE.getRGB());
    }
}
