package net.kore.ui.hud;

import net.kore.Kore;
import net.kore.utils.MilliTimer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class Component
{
    public double x;
    protected double y;
    protected double width;
    protected double height;
    protected boolean hidden;
    protected MilliTimer hideTimer;

    public void onTick() {
    }

    protected HudVec drawScreen() {
        return null;
    }

    public boolean isHovered(final int mouseX, final int mouseY) {
        return mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height;
    }

    public boolean isHovered() {
        return this.isHovered(getMouseX(), getMouseY());
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public Component setPosition(final double x, final double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Component setHidden(final boolean hidden) {
        if (hidden != this.hidden) {
            this.hidden = hidden;
            this.hideTimer.reset();
        }
        return this;
    }

    public Component setSize(final double width, final double height) {
        this.width = width;
        this.height = height;
        return this;
    }

    protected static int getMouseX() {
        return Mouse.getX() * getScaledResolution().getScaledWidth() / Kore.mc.displayWidth;
    }

    protected static int getMouseY() {
        final int height = getScaledResolution().getScaledHeight();
        return height - Mouse.getY() * height / Kore.mc.displayHeight - 1;
    }

    protected static ScaledResolution getScaledResolution() {
        return new ScaledResolution(Kore.mc);
    }

    public double getHeight() {
        return this.height;
    }

    public double getWidth() {
        return this.width;
    }

    public double getY() {
        return this.y;
    }

    public double getX() {
        return this.x;
    }
}


