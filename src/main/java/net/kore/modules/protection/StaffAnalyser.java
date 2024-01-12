package net.kore.modules.protection;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.NumberSetting;
import net.kore.utils.Multithreading;
import net.kore.utils.Notification;
import net.kore.utils.api.PlanckeScraper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.TimeUnit;

public class StaffAnalyser extends Module
{
    private NumberSetting delay;
    private int lastBans;
    private int ticks;

    public StaffAnalyser() {
        super("Staff Analyser", Category.PROTECTIONS);
        this.delay = new NumberSetting("Delay (Minutes)", 5.0, 5.0, 60.0, 1.0);
        this.lastBans = -1;
        this.addSettings(this.delay);
    }

    @Override
    public void assign()
    {
        Kore.staffAnalyser = this;
    }
    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if(!Kore.staffAnalyser.isToggled() || event.phase != TickEvent.Phase.START) return;

        ticks = (ticks + 1) % 20 == 0 ? 0 : ticks;

        if(ticks % 10 == 0) {
            Multithreading.schedule(() -> {
                final int bans = PlanckeScraper.getBans();
                if (bans != this.lastBans && this.lastBans != -1 && bans > this.lastBans) {
                    if(Kore.clientSettings.debug.isEnabled()) {
                        Kore.sendMessageWithPrefix("(StaffAnalyzer) Checking staff bans...");
                    }
                    Kore.notificationManager.showNotification(String.format("Staff has banned %s %s in the last %s minutes", bans - this.lastBans, (bans - this.lastBans > 1) ? "people" : "person", (int)this.delay.getValue()), 5000, (bans - this.lastBans > 2) ? Notification.NotificationType.WARNING : Notification.NotificationType.INFO);
                }
                this.lastBans = bans;
            }, (long)delay.getValue(), TimeUnit.MINUTES);
        }
    }
}