package net.kore.ui.windows;

import net.kore.ui.ModernClickGui;
import net.kore.utils.AnimationUtils;
import net.kore.utils.MouseUtils;
import net.kore.utils.api.ServerUtils;
import net.kore.utils.font.Fonts;

import java.awt.*;

public class HomeWindow extends Window {
    public static AnimationUtils scroll = new AnimationUtils(0.0);
    public int scrollY;
    public HomeWindow() {
        super("Home");
    }

    @Override
    public void initGui() {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int yOffset = (int) (35 + scroll.getValue());
        for (String str : ServerUtils.changelog) {
            Fonts.getPrimary().drawString(str, ModernClickGui.getX() + 100.0, ModernClickGui.getY() + (double)yOffset, Color.WHITE.getRGB());
            yOffset += 12;
        }

        MouseUtils.Scroll scrol = MouseUtils.scroll();

        if (scrol != null)
        {
            switch (scrol)
            {
                case DOWN:
                    if ((scrollY > (ModernClickGui.getHeight() - 25) - getHeight()))
                    {
                        scrollY -= 10;
                    }
                    break;
                case UP:
                    scrollY += 10;
                    if (scrollY >= 0)
                    {
                        scrollY = 0;
                    }

                    if (getHeight() < (ModernClickGui.getHeight() - 25))
                        scrollY = 0;
            }
        }

        scroll.setAnimation(scrollY, 12);
    }

    public int getHeight()
    {
        return 12 * ServerUtils.changelog.size() + 8;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }
}
