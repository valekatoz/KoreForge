package net.kore.modules.render;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.modules.combat.AntiBot;
import net.kore.utils.MathUtils;
import net.kore.utils.render.RenderUtils;
import net.kore.utils.font.*;

import net.minecraftforge.client.event.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;
import java.awt.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class Nametags extends Module
{
    public Nametags() {
        super("Nametags", Category.RENDER);
    }

    @Override
    public void assign()
    {
        Kore.nametags = this;
    }

    @SubscribeEvent
    public void onRender(final RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
        if (this.isToggled() && AntiBot.isValidEntity((Entity)event.entity) && event.entity instanceof EntityPlayer && event.entity != Kore.mc.thePlayer && event.entity.getDistanceToEntity((Entity)Kore.mc.thePlayer) < 100.0f) {
            event.setCanceled(true);
            GlStateManager.alphaFunc(516, 0.1f);
            String name = event.entity.getName();
            if(Kore.nickHider.isToggled() && name.equals(Kore.mc.getSession().getUsername())) {
                name = Kore.nickHider.nick.getValue();
            }
            final double x = event.x;
            final double y = event.y;
            final double z = event.z;
            final float f = Math.max(1.4f, event.entity.getDistanceToEntity((Entity)Kore.mc.thePlayer) / 10.0f);
            final float scale = 0.016666668f * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x + 0.0f, (float)y + event.entity.height + 0.5f, (float)z);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-Kore.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(Kore.mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-scale, -scale, scale);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            final float textWidth = (float)Math.max(Fonts.getPrimary().getStringWidth(name) / 2.0, 30.0);
            GlStateManager.disableTexture2D();
            RenderUtils.drawRect(-textWidth - 3.0f, (float)(Fonts.getPrimary().getHeight() + 3), textWidth + 3.0f, -3.0f, new Color(20, 20, 20, 80).getRGB());
            RenderUtils.drawRect(-textWidth - 3.0f, (float)(Fonts.getPrimary().getHeight() + 3), (float)((textWidth + 3.0f) * ((MathUtils.clamp(event.entity.getHealth() / event.entity.getMaxHealth(), 1.0, 0.0) - 0.5) * 2.0)), (float)(Fonts.getPrimary().getHeight() + 2), Kore.clickGui.getColor().getRGB());
            GlStateManager.enableTexture2D();
            Fonts.getPrimary().drawSmoothString(name, -Fonts.getPrimary().getStringWidth(name) / 2.0, 0.0f, Color.WHITE.getRGB());
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            Fonts.getPrimary().drawSmoothString(name, -Fonts.getPrimary().getStringWidth(name) / 2.0, 0.0f, Color.WHITE.getRGB());
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }
}