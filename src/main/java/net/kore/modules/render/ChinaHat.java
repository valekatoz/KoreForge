package net.kore.modules.render;

import java.awt.Color;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;

public class ChinaHat extends Module
{
    public NumberSetting radius;
    public NumberSetting height;
    public NumberSetting pos;
    public NumberSetting rotation;
    public NumberSetting angles;
    public BooleanSetting firstPerson;
    public BooleanSetting shade;

    public ChinaHat() {
        super("China Hat", Category.RENDER);
        this.radius = new NumberSetting("Radius", 0.7, 0.5, 1.0, 0.01);
        this.height = new NumberSetting("Height", 0.3, 0.10000000149011612, 0.699999988079071, 0.01);
        this.pos = new NumberSetting("Position", 0.1, -0.5, 0.5, 0.01);
        this.rotation = new NumberSetting("Rotation", 5.0, 0.0, 5.0, 0.1);
        this.angles = new NumberSetting("Angles", 8.0, 4.0, 90.0, 1.0);
        this.firstPerson = new BooleanSetting("Show in first person", false);
        this.shade = new BooleanSetting("Shade", true);
        this.addSettings(this.radius, this.height, this.firstPerson, this.rotation, this.pos, this.angles, this.shade);
    }

    @Override
    public void assign()
    {
        Kore.chinaHat = this;
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent event) {
        if (this.isToggled() && (Kore.mc.gameSettings.thirdPersonView != 0 || this.firstPerson.isEnabled())) {
            this.drawChinaHat((EntityLivingBase)Kore.mc.thePlayer, event.partialTicks);
        }
    }

    private void drawChinaHat(final EntityLivingBase entity, final float partialTicks) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        if (this.shade.isEnabled()) {
            GL11.glShadeModel(7425);
        }
        GL11.glDisable(3553);
        GL11.glDisable(2884);
        GlStateManager.disableLighting();
        GL11.glTranslated(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - Kore.mc.getRenderManager().viewerPosX, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - Kore.mc.getRenderManager().viewerPosY + entity.height + (entity.isSneaking() ? (this.pos.getValue() - 0.23000000417232513) : this.pos.getValue()), entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - Kore.mc.getRenderManager().viewerPosZ);
        GL11.glRotatef((float)((entity.ticksExisted + partialTicks) * this.rotation.getValue()) - 90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-(Kore.mc.thePlayer.prevRotationYawHead + (Kore.mc.thePlayer.rotationYawHead - Kore.mc.thePlayer.prevRotationYawHead) * partialTicks), 0.0f, 1.0f, 0.0f);
        final double radius = this.radius.getValue();
        GL11.glLineWidth(2.0f);
        GL11.glBegin(2);
        for (int i = 0; i <= this.angles.getValue(); ++i) {
            final Color color = this.getColor(i, (int)this.angles.getValue(), false);
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
            GL11.glVertex3d(Math.cos(i * 3.141592653589793 / (this.angles.getValue() / 2.0)) * radius, 0.0, Math.sin(i * 3.141592653589793 / (this.angles.getValue() / 2.0)) * radius);
        }
        GL11.glEnd();
        GL11.glBegin(6);
        final Color c1 = this.getColor(0.0, this.angles.getValue(), true);
        GL11.glColor4f(c1.getRed() / 255.0f, c1.getGreen() / 255.0f, c1.getBlue() / 255.0f, 0.8f);
        GL11.glVertex3d(0.0, this.height.getValue(), 0.0);
        for (int j = 0; j <= this.angles.getValue(); ++j) {
            final Color color2 = this.getColor(j, (int)this.angles.getValue(), false);
            GL11.glColor4f(color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f, 0.3f);
            GL11.glVertex3d(Math.cos(j * 3.141592653589793 / (this.angles.getValue() / 2.0)) * radius, 0.0, Math.sin(j * 3.141592653589793 / (this.angles.getValue() / 2.0)) * radius);
        }
        GL11.glVertex3d(0.0, this.height.getValue(), 0.0);
        GL11.glEnd();
        GL11.glShadeModel(7424);
        GL11.glEnable(2884);
        GlStateManager.resetColor();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public Color getColor(final double i, final double max, final boolean first) {
        double c = i / max * 10.0;
        if (i > max / 2.0) {
            c = 10.0 - c;
        }
        return Kore.clickGui.getColor((int)c);
    }
}

