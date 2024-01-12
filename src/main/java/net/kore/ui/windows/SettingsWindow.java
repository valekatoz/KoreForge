package net.kore.ui.windows;

import net.kore.Kore;
import net.kore.settings.Setting;
import net.kore.ui.components.CompModeSetting;
import net.kore.ui.components.Comp;

import java.util.ArrayList;
import java.util.List;

public class SettingsWindow extends Window {
    public List<Setting> settingList = new ArrayList<>();
    public SettingsWindow() {
        super("Settings");
        this.settingList.add(Kore.clickGui.colorMode);
        this.settingList.addAll(Kore.clientSettings.settings);
    }

    @Override
    public void initGui() {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (Comp comp : ModuleWindow.updateComps(this.settingList))
        {
            comp.drawScreen(mouseX, mouseY, (double)partialTicks);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (Comp comp : ModuleWindow.updateComps(this.settingList))
        {
            comp.mouseClicked(mouseX, mouseY, mouseButton);
        }

        Kore.themeManager.setTheme(Kore.clickGui.colorMode.getSelected());
        Kore.configManager.saveConfig();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Comp comp : ModuleWindow.updateComps(this.settingList))
        {
            comp.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (ModuleWindow.selectedString != null)
        {
            ModuleWindow.selectedString = null;
        }

        for (Comp comp : ModuleWindow.updateComps(this.settingList))
        {
            comp.keyTyped(typedChar, keyCode);
        }
    }
}
