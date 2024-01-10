package net.kore.utils;

import net.kore.Kore;
import net.kore.managers.NotificationManager;

import java.awt.*;

public class Notification
{
    private final NotificationType type;
    private final String description;
    private final long end;
    private final int time;
    private final int colorIndex;

    public Notification(final String description, int time, final NotificationType type) {
        time += 500;
        this.description = description;
        this.end = System.currentTimeMillis() + time;
        this.time = time;
        this.type = type;
        this.colorIndex = NotificationManager.notifications.size() + 1;
    }

    public int getTime() {
        return this.time;
    }

    public String getTitle() {
        return this.type.getName();
    }

    public long getCurrentTime() {
        return System.currentTimeMillis() - this.end + this.time;
    }

    public String getDescription() {
        return this.description;
    }

    public long getEnd() {
        return this.end;
    }

    public Color getColor() {
        return this.type.getColor(this.colorIndex);
    }
    public enum NotificationType
    {
        WARNING("Warning", new Color(255, 204, 0)),
        INFO("Notification", Color.white),
        ERROR("Error", new Color(208, 3, 3));

        private final String name;
        private final Color color;

        private NotificationType(final String name, final Color color) {
            this.name = name;
            this.color = color;
        }

        public String getName() {
            return this.name;
        }

        public Color getColor(final int i) {
            return (this == NotificationType.INFO) ? Kore.themeManager.getSecondaryColor(i) : this.color;
        }
    }
}
