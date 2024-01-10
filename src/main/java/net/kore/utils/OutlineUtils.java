package net.kore.utils;

import net.kore.events.RenderLayersEvent;
import net.minecraft.client.model.*;
import java.awt.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.shader.*;
import org.lwjgl.opengl.*;

public class OutlineUtils
{
    public static void outlineESP(final EntityLivingBase entity, final float p_77036_2_, final float p_77036_3_, final float p_77036_4_, final float p_77036_5_, final float p_77036_6_, final float scaleFactor, final ModelBase modelBase, final Color color) {
        final Minecraft mc = Minecraft.getMinecraft();
        final boolean fancyGraphics = mc.gameSettings.fancyGraphics;
        final float gamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.fancyGraphics = false;
        mc.gameSettings.gammaSetting = 100000.0f;
        GlStateManager.resetColor();
        setColor(color);
        renderOne(2.0f);
        modelBase.render((Entity)entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
        setColor(color);
        renderTwo();
        modelBase.render((Entity)entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
        setColor(color);
        renderThree();
        modelBase.render((Entity)entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
        setColor(color);
        renderFour(color);
        modelBase.render((Entity)entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);
        setColor(color);
        renderFive();
        setColor(Color.WHITE);
        mc.gameSettings.fancyGraphics = fancyGraphics;
        mc.gameSettings.gammaSetting = gamma;
    }

    public static void outlineESP(final RenderLayersEvent event, final Color color) {
        final Minecraft mc = Minecraft.getMinecraft();
        final boolean fancyGraphics = mc.gameSettings.fancyGraphics;
        final float gamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.fancyGraphics = false;
        mc.gameSettings.gammaSetting = 100000.0f;
        GlStateManager.resetColor();
        setColor(color);
        renderOne(3.0f);
        event.modelBase.render((Entity)event.entity, event.p_77036_2_, event.p_77036_3_, event.p_77036_4_, event.p_77036_5_, event.p_77036_6_, event.scaleFactor);
        setColor(color);
        renderTwo();
        event.modelBase.render((Entity)event.entity, event.p_77036_2_, event.p_77036_3_, event.p_77036_4_, event.p_77036_5_, event.p_77036_6_, event.scaleFactor);
        setColor(color);
        renderThree();
        event.modelBase.render((Entity)event.entity, event.p_77036_2_, event.p_77036_3_, event.p_77036_4_, event.p_77036_5_, event.p_77036_6_, event.scaleFactor);
        setColor(color);
        renderFour(color);
        event.modelBase.render((Entity)event.entity, event.p_77036_2_, event.p_77036_3_, event.p_77036_4_, event.p_77036_5_, event.p_77036_6_, event.scaleFactor);
        setColor(color);
        renderFive();
        setColor(Color.WHITE);
        mc.gameSettings.fancyGraphics = fancyGraphics;
        mc.gameSettings.gammaSetting = gamma;
    }

    public static void renderOne(final float lineWidth) {
        checkSetupFBO();
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3008);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth);
        GL11.glEnable(2848);
        GL11.glEnable(2960);
        GL11.glClear(1024);
        GL11.glClearStencil(15);
        GL11.glStencilFunc(512, 1, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void renderTwo() {
        GL11.glStencilFunc(512, 0, 15);
        GL11.glStencilOp(7681, 7681, 7681);
        GL11.glPolygonMode(1032, 6914);
    }

    public static void renderThree() {
        GL11.glStencilFunc(514, 1, 15);
        GL11.glStencilOp(7680, 7680, 7680);
        GL11.glPolygonMode(1032, 6913);
    }

    public static void renderFour(final Color color) {
        setColor(color);
        GL11.glDepthMask(false);
        GL11.glDisable(2929);
        GL11.glEnable(10754);
        GL11.glPolygonOffset(1.0f, -2000000.0f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
    }

    public static void renderFive() {
        GL11.glPolygonOffset(1.0f, 2000000.0f);
        GL11.glDisable(10754);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(2960);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glEnable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        GL11.glPopAttrib();
    }

    public static void setColor(final Color color) {
        GL11.glColor4d((double)(color.getRed() / 255.0f), (double)(color.getGreen() / 255.0f), (double)(color.getBlue() / 255.0f), (double)(color.getAlpha() / 255.0f));
    }

    public static void checkSetupFBO() {
        final Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();
        if (fbo != null && fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    private static void setupFBO(final Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        final int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, stencil_depth_buffer_ID);
    }
}