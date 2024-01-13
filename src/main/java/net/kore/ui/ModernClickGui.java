package net.kore.ui;

import net.kore.Kore;
import net.kore.managers.WindowManager;
import net.kore.ui.windows.ModuleWindow;
import net.kore.ui.windows.Window;
import net.kore.utils.StencilUtils;
import net.kore.utils.render.GLUtil;
import net.kore.utils.render.RenderUtils;
import net.kore.utils.font.Fonts;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class ModernClickGui extends GuiScreen {
    public WindowManager windowManager = new WindowManager();
    public static Window selectedWindow;
    public static boolean settingsOpened;
    private static double x;
    private static double y;
    private int settingsOffset = 210;

    public ModernClickGui() {
        selectedWindow = this.windowManager.getDefaultWindow();
    }

    @Override
    public void initGui() {
        super.initGui();
        for (Window window : this.windowManager.windows) {
            window.initGui();
        }

        ScaledResolution sr = new ScaledResolution(Kore.mc);
        x = (double)sr.getScaledWidth() / 2.0 - (double)(getWidth() / 2.0f);
        y = (double)sr.getScaledHeight() / 2.0 - (double)(getHeight() / 2.0f);

        ModuleWindow.selectedModule = null;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int categoryOffset = 25;

        GLUtil.startScale((float)(getX() + (getX() + (double)getWidth())) / 2.0f, (float)(getY() + (getY() + (double)getHeight())) / 2.0f, 1.0f);
        RenderUtils.drawBorderedRoundedRect((float) getX() - 5, (float) getY() - 5, getWidth() + 10, getHeight() + 10, 3.0f, 2 , Kore.themeManager.getPrimaryColor().getRGB(), Kore.themeManager.getSecondaryColor().getRGB());
        RenderUtils.drawBorderedRoundedRect((float)getX(), (float)getY(), 85.0f, getHeight(), 3.0f, 2.0f, Kore.themeManager.getPrimaryColor().getRGB(), Kore.themeManager.getSecondaryColor().getRGB());
        RenderUtils.drawBorderedRoundedRect((float)(getX() + 90.0), (float)getY(), getWidth() - 90.0f, 20.0f, 3.0f, 2.0f, Kore.themeManager.getPrimaryColor().getRGB(), Kore.themeManager.getSecondaryColor().getRGB());
        RenderUtils.drawBorderedRoundedRect((float)(getX() + 90.0), (float)(getY() + 25.0), getWidth() - 90.0f, getHeight() - 25.0f, 3.0f, 2.0f, Kore.themeManager.getPrimaryColor().getRGB(), Kore.themeManager.getSecondaryColor().getRGB());
        Fonts.getSecondary().drawCenteredString("Kore Client", (float)(getX() + 42.5), (float)(getY() + 6.0), Color.WHITE.getRGB());
        drawTopBar(mouseX, mouseY);
        for (Window window : this.windowManager.windows) {
            if(window.getName().equals("Settings")) {
                if (window == selectedWindow) {
                    RenderUtils.drawBorderedRoundedRect((float)(getX() + 5.0), (float)(getY() + (double)settingsOffset + 3.0), 75.0f, 12.0f, 4.0f, 4.0f, Kore.themeManager.getSecondaryColor().getRGB(), Kore.themeManager.getSecondaryColor().getRGB());
                }
                Fonts.getPrimary().drawStringWithShadow(window.getName(), getX() + 12.0, getY() + (double)settingsOffset + 5.0, Color.WHITE.getRGB());
            } else {
                if (window == selectedWindow) {
                    RenderUtils.drawBorderedRoundedRect((float)(getX() + 5.0), (float)(getY() + (double)categoryOffset + 3.0), 75.0f, 12.0f, 4.0f, 4.0f, Kore.themeManager.getSecondaryColor().getRGB(), Kore.themeManager.getSecondaryColor().getRGB());
                }
                Fonts.getPrimary().drawStringWithShadow(window.getName(), getX() + 12.0, getY() + (double)categoryOffset + 5.0, Color.WHITE.getRGB());
                categoryOffset += 14;
            }

            StencilUtils.enableStencilBuffer();
            RenderUtils.drawBorderedRoundedRect((float)ModernClickGui.getX() + 88.0f, (float)ModernClickGui.getY() + 25.0f, ModernClickGui.getWidth() - 88.0f, ModernClickGui.getHeight() - 25.0f, 6.0f, 2.0f, Kore.themeManager.getPrimaryColor().getRGB(), Kore.themeManager.getPrimaryColor().getRGB());
            StencilUtils.readStencilBuffer(1);

            if (selectedWindow == window)
            {
                selectedWindow.drawScreen(mouseX, mouseY, partialTicks);
            }

            StencilUtils.disableStencilBuffer();
        }

        GlStateManager.popMatrix();
    }

    public void drawTopBar(int mouseX, int mouseY)
    {
        Fonts.getPrimary().drawString("Welcome to Kore - v" + Kore.VERSION, (float)(getX() + 95), (float) (getY() + 6f), Color.WHITE.getRGB());
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        selectedWindow.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        int categoryOffset = 25;
        for (Window c : this.windowManager.windows) {
            if(c.getName().equals("Settings")) {
                if (this.isHovered(mouseX, mouseY, getX() + 4.0, getY() + (double)settingsOffset, 75.0, 16.0) && mouseButton == 0) {
                    selectedWindow = c;
                    settingsOpened = false;
                    ModuleWindow.selectedModule = null;

                    if (selectedWindow instanceof ModuleWindow)
                    {
                        ((ModuleWindow) c).close();
                    }
                }
            } else {
                if (this.isHovered(mouseX, mouseY, getX() + 4.0, getY() + (double)categoryOffset, 75.0, 16.0) && mouseButton == 0) {
                    selectedWindow = c;
                    settingsOpened = false;
                    ModuleWindow.selectedModule = null;

                    if (selectedWindow instanceof ModuleWindow)
                    {
                        ((ModuleWindow) c).close();
                    }
                }
                categoryOffset += 14;
            }
        }
        selectedWindow.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        selectedWindow.keyTyped(typedChar, keyCode);
        if (keyCode == 1) {
            if (selectedWindow instanceof ModuleWindow && ModuleWindow.changeBind != null)
            {
                ModuleWindow.changeBind.setKeycode(0);
                ModuleWindow.changeBind = null;
                return;
            }
            if (selectedWindow instanceof ModuleWindow && settingsOpened)
            {
                if (ModuleWindow.selectedString != null)
                {
                    ModuleWindow.selectedString = null;
                    return;
                }
                else if (ModuleWindow.selectedString == null)
                {
                    ModuleWindow moduleWindow = (ModuleWindow)selectedWindow;
                    ModuleWindow.selectedModule = null;
                    moduleWindow.close();
                }
            }
            else if (!settingsOpened)
            {
                this.mc.displayGuiScreen(null);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        settingsOpened = false;
        Kore.configManager.saveConfig();
        Kore.clickGui.setToggled(false);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static double getX() {
        return x;
    }

    public static double getY() {
        return y;
    }

    public static float getWidth() {
        return 305.0f;
    }

    public static float getHeight() {
        return 230.0f;
    }

    public boolean isHovered(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (double)mouseX > x && (double)mouseX < x + width && (double)mouseY > y && (double)mouseY < y + height;
    }

    static {
        settingsOpened = false;
        x = 100.0;
        y = 100.0;
    }
}
