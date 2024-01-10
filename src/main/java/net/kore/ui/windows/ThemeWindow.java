package net.kore.ui.windows;

import net.kore.Kore;
import net.kore.ui.components.CompModeSetting;

import java.util.ArrayList;
import java.util.List;

public class ThemeWindow extends Window {
    public ThemeWindow() {
        super("Themes");
    }

    @Override
    public void initGui() {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        CompModeSetting modeSetting = new CompModeSetting(95, 30, Kore.clickGui.colorMode);
        modeSetting.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        CompModeSetting modeSetting = new CompModeSetting(95, 30, Kore.clickGui.colorMode);
        modeSetting.mouseClicked(mouseX, mouseY, mouseButton);

        Kore.themeManager.setTheme(Kore.clickGui.colorMode.getSelected());
        Kore.configManager.saveConfig();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }
}
