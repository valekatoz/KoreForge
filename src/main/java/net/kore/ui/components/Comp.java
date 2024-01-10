package net.kore.ui.components;

public class Comp {
    public double x;
    public double y;
    public double width;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void drawScreen(int mouseX, int mouseY, double scrollY) {
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public boolean isHovered(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (double)mouseX > x && (double)mouseX < x + width && (double)mouseY > y && (double)mouseY < y + height;
    }
}
