package net.kore.utils;

import org.lwjgl.input.Mouse;

public class MouseUtils {


    // Fixing this some other time.

    public static boolean mouseInBounds(int mouseX, int mouseY, double x, double y, double width, double height) {
        return (mouseX > (int) x && mouseX < (int) (x + width)) && (mouseY > (int) y && mouseY < (int) (y + height));
    }

    public static Scroll scroll() {
        int mouse = Mouse.getDWheel();

        if (mouse > 0) {
            return Scroll.UP;
        } else if (mouse < 0) {
            return Scroll.DOWN;
        } else {
            return null;
        }
    }


    public enum Scroll {
        UP, DOWN
    }

    public enum PositionMode {
        UPLEFT,
        UPRIGHT,
        DOWNLEFT,
        DOWNRIGHT
    }
}
