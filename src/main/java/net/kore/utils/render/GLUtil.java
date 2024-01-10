package net.kore.utils.render;

import net.minecraft.client.renderer.GlStateManager;

import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.*;

public class GLUtil {
    public static void startScale(float x, float y, float scale) {
        pushMatrix();
        translate(x, y, 0);
        scale(scale, scale, 1);
        translate(-x, -y, 0);
    }

    public static void startScale(float x, float y, float width, float height, float scale) {
        pushMatrix();
        translate((x + (x + width)) / 2, (y + (y + height)) / 2, 0);
        scale(scale, scale, 1);
        translate(-(x + (x + width)) / 2, -(y + (y + height)) / 2, 0);
    }
    public static void enableDepth() {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
    }

    public static void disableDepth() {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }

    public static int[] enabledCaps = new int[32];

    public static void enableCaps(int... caps) {
        for (int cap : caps) glEnable(cap);
        enabledCaps = caps;
    }

    public static void disableCaps() {
        for (int cap : enabledCaps) glDisable(cap);
    }

    public static void startBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void endBlend() {
        GlStateManager.disableBlend();
    }

    public static void setup2DRendering(boolean blend) {
        if (blend) {
            startBlend();
        }
        GlStateManager.disableTexture2D();
    }

    public static void setup2DRendering() {
        setup2DRendering(true);
    }

    public static void end2DRendering() {
        GlStateManager.enableTexture2D();
        endBlend();
    }

    public static void startRotate(float x, float y, float rotate) {
        pushMatrix();
        translate(x, y, 0);
        GlStateManager.rotate(rotate, 0, 0, -1);
        translate(-x, -y, 0);
    }

    public static void endRotate(){
        GlStateManager.popMatrix();
    }


}
