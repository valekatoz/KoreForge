package net.kore.ui.components;

import net.kore.ui.ModernClickGui;
import net.kore.utils.font.Fonts;

import java.awt.*;

public class CompSettingSpacer extends Comp {

    public String category;

    public CompSettingSpacer(double x, double y, String category)
    {
        this.x = x;
        this.y = y;
        this.category = category;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, double scrollY)
    {
        Fonts.newIcons.drawString("||||||", 5 + Fonts.getPrimary().drawString(category, 5 + Fonts.newIcons.drawString("|", (float) (ModernClickGui.getX() + x), (float) (ModernClickGui.getY() + y + 2), Color.WHITE.getRGB()), (float) (ModernClickGui.getY() + y + 2), Color.WHITE.getRGB()), (float) (ModernClickGui.getY() + y + 2), Color.WHITE.getRGB());
    }
}
