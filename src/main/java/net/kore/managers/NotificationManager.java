package net.kore.managers;

import net.kore.Kore;
import net.kore.utils.Notification;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class NotificationManager {
    public static final ArrayList<Notification> notifications = new ArrayList<>();
    public NotificationManager()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void render()
    {
        GL11.glPushMatrix();
        notifications.removeIf(n -> n.getEnd() <= System.currentTimeMillis());
        if (notifications.size() > 0) {
            final ScaledResolution res = new ScaledResolution(Kore.mc);
            float y = (float)(res.getScaledHeight() - 37);
            for (int i = 0; i < notifications.size(); ++i) {
                final Notification notification = notifications.get(i);
                GL11.glPushMatrix();
                final float width = (float)Math.max(150.0, Fonts.getPrimary().getStringWidth(notification.getDescription()) + 10.0);
                float height = 35.0f;
                float x = res.getScaledWidth() - width - 2.0f;
                if (notification.getCurrentTime() <= 250L) {
                    if (notification.getCurrentTime() >= 100L) {
                        x += (float)((250L - notification.getCurrentTime()) / 150.0 * (width + 2.0f));
                    }
                    else {
                        x += 10000.0f;
                    }
                }
                else if (notification.getEnd() - System.currentTimeMillis() <= 250L) {
                    final long time = notification.getEnd() - System.currentTimeMillis();
                    if (time >= 100L) {
                        x += (float)((250L - time) / 150.0 * (width + 2.0f));
                    }
                    else {
                        x += 10000.0f;
                    }
                }
                RenderUtils.drawRoundedRect(x, y, x + width, y + height, 3.0, new Color(21, 21, 21, 90).getRGB());
                Fonts.getSecondary().drawSmoothStringWithShadow(notification.getTitle(), x + 3.0f, y + 5.0f, notification.getColor().getRGB());
                Fonts.getPrimary().drawSmoothStringWithShadow(notification.getDescription(), x + 5.0f, y + 10.0f + Fonts.getPrimary().getHeight(), Color.white.getRGB());
                RenderUtils.drawRect(x, y + height - 2.0f, x + width * notification.getCurrentTime() / notification.getTime(), y + height, notification.getColor().getRGB());
                if (notification.getCurrentTime() < 100L) {
                    height *= (float)(notification.getCurrentTime() / 100.0);
                }
                else if (notification.getEnd() - System.currentTimeMillis() < 100L) {
                    final long time = notification.getEnd() - System.currentTimeMillis();
                    height *= (float)(time / 100.0);
                }
                y -= height + 1.0f;
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    @SubscribeEvent
    public void renderOverlay(TickEvent.RenderTickEvent e)
    {
        if (e.phase == TickEvent.Phase.END)
            render();
    }

    public void showNotification(final String description, final int time, Notification.NotificationType type)
    {
        notifications.add(new Notification(description, time, type));
    }
}
