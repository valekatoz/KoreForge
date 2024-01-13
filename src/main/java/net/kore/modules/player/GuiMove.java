package net.kore.modules.player;

import net.kore.Kore;
import net.kore.events.PostGuiOpenEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GuiMove extends Module
{
    private BooleanSetting rotate;
    private BooleanSetting drag;
    private NumberSetting sensivity;
    public static KeyBinding[] binds;

    public GuiMove() {
        super("InvMove", Category.PLAYER);
        this.rotate = new BooleanSetting("Rotate", false);
        this.drag = new BooleanSetting("Alt drag", true) {
            @Override
            public boolean isHidden() {
                return !rotate.isEnabled();
            }
        };
        this.sensivity = new NumberSetting("Sensivity", 1.5, 0.1, 3.0, 0.01, aBoolean -> !this.rotate.isEnabled());
        this.addSettings(this.rotate, this.sensivity, this.drag);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.guiMove = this;
    }

    @Override
    public boolean isToggled() {
        return super.isToggled();
    }

    @Override
    public void onDisable() {
        if (Kore.mc.currentScreen != null) {
            for (final KeyBinding bind : GuiMove.binds) {
                KeyBinding.setKeyBindState(bind.getKeyCode(), false);
            }
        }
    }

    private void updateBinds()
    {
        binds = new KeyBinding[] { Kore.mc.gameSettings.keyBindSneak, Kore.mc.gameSettings.keyBindJump, Kore.mc.gameSettings.keyBindSprint, Kore.mc.gameSettings.keyBindForward, Kore.mc.gameSettings.keyBindBack, Kore.mc.gameSettings.keyBindLeft, Kore.mc.gameSettings.keyBindRight };
    }

    @SubscribeEvent
    public void onGui(final PostGuiOpenEvent event) {
        if (binds == null) updateBinds();
        if (!(event.gui instanceof GuiChat) && this.isToggled()) {
            for (final KeyBinding bind : GuiMove.binds) {
                KeyBinding.setKeyBindState(bind.getKeyCode(), GameSettings.isKeyDown(bind));
            }
        }
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent event) {
        if (Kore.mc.currentScreen != null && !(Kore.mc.currentScreen instanceof GuiChat) && this.isToggled()) {
            if (binds == null) updateBinds();
            for (final KeyBinding bind : GuiMove.binds) {
                KeyBinding.setKeyBindState(bind.getKeyCode(), GameSettings.isKeyDown(bind));
            }
            if ((Kore.mc.currentScreen instanceof GuiContainer || Kore.mc.currentScreen instanceof ICrafting) && this.rotate.isEnabled()) {
                Kore.mc.mouseHelper.mouseXYChange();
                float f = Kore.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
                f *= (float)this.sensivity.getValue();
                final float f2 = f * f * f * 8.0f;
                final float f3 = Kore.mc.mouseHelper.deltaX * f2;
                final float f4 = Kore.mc.mouseHelper.deltaY * f2;
                int i = 1;
                if (Kore.mc.gameSettings.invertMouse) {
                    i = -1;
                }
                if (Keyboard.isKeyDown(56) && Mouse.isButtonDown(2) && this.drag.isEnabled()) {
                    Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 6);
                    Kore.mc.setIngameNotInFocus();
                    Mouse.setGrabbed(false);
                }
                Kore.mc.thePlayer.setAngles(f3, f4 * i);
            }
        }
    }
}
