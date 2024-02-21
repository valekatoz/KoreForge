package net.kore.modules.protection;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.ui.notifications.Notification;
import net.kore.utils.MilliTimer;
import net.kore.utils.api.PlanckeScraper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class StaffAnalyser extends Module
{
    private ModeSetting mode;
    private NumberSetting delay;
    private MilliTimer timer;
    private int lastBans;

    public StaffAnalyser() {
        super("Staff Analyser", Category.PROTECTIONS);
        this.mode = new ModeSetting("Mode", "Chat", "Chat", "Notification");
        this.delay = new NumberSetting("Check Delay (Seconds)", 5.0, 5.0, 60.0, 1.0);
        this.timer = new MilliTimer();
        this.lastBans = -1;
        this.addSettings(this.mode, this.delay);
    }

    @Override
    public void assign()
    {
        Kore.staffAnalyser = this;
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (this.isToggled() && this.timer.hasTimePassed((long)(this.delay.getValue() * 1000.0))) {
            this.timer.reset();
            new Thread(() -> {
                final int bans = PlanckeScraper.getBans();
                if (bans != this.lastBans && this.lastBans != -1 && bans > this.lastBans) {
                    if(this.mode.is("Notification")) {
                        Kore.notificationManager.showNotification(String.format("Staff has banned %s %s in the last %s seconds", bans - this.lastBans, (bans - this.lastBans > 1) ? "people" : "person", (int)this.delay.getValue()), 2500, (bans - this.lastBans > 2) ? Notification.NotificationType.WARNING : Notification.NotificationType.INFO);
                    } else {
                        Kore.sendMessageWithPrefix(String.format("Staff has banned %s %s in the last %s seconds", bans - this.lastBans, (bans - this.lastBans > 1) ? "people" : "person", (int)this.delay.getValue()));
                    }
                }
                this.lastBans = bans;
            }).start();
        }
    }
}
