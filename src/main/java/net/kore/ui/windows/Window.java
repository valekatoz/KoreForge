package net.kore.ui.windows;

public abstract class Window {
    private final String name;
    public static final int LEFT_CLICK = 0;
    public static final int RIGHT_CLICK = 1;
    public static final int MIDDLE_CLICK = 2;

    public Window(String name) {
        this.name = name;
    }

    public abstract void initGui();

    public abstract void drawScreen(int var1, int var2, float var3);

    public abstract void mouseClicked(int var1, int var2, int var3);

    public abstract void mouseReleased(int var1, int var2, int var3);

    public abstract void keyTyped(char var1, int var2);

    public String getName() {
        return this.name;
    }

    public boolean isHovered(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (double)mouseX > x && (double)mouseX < x + width && (double)mouseY > y && (double)mouseY < y + height;
    }
}
