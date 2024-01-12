package net.kore.modules.render;

import net.kore.Kore;
import net.kore.events.PacketReceivedEvent;
import net.kore.modules.Module;
import net.kore.settings.NumberSetting;
import net.kore.utils.Notification;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;

public class PurseSpoofer extends Module {
    public NumberSetting additionalCoins = new NumberSetting("Coins", 1000d, Double.MIN_VALUE + 1, Double.MAX_VALUE - 1, 0, aBoolean -> true);

    public PurseSpoofer() {
        super("Purse Spoofer", Module.Category.RENDER);
    }

    @Override
    public void assign() {
        Kore.purseSpoofer = this;
    }

    @Override
    public void onEnable() {
        Kore.sendMessage("(PurseSpoofer) Usage -> .setpurse <value>");
    }

    @SubscribeEvent
    public void packet(PacketReceivedEvent event) {
        if (!isToggled() || Kore.mc.thePlayer == null || Kore.mc.theWorld == null) return;

        Packet<?> packet = event.packet;
        if (packet instanceof S3EPacketTeams) {
            S3EPacketTeams team = (S3EPacketTeams) packet;
            String strip = StringUtils.stripControlCodes(team.getPrefix()).toLowerCase();

            if (!strip.startsWith("purse: ")) return;

            final double purseValue = Double.parseDouble(strip.split(" ")[1].replaceAll(",", ""));
            final double addCoins = additionalCoins.getValue();

            String newPurse = Kore.fancy + "fPurse: " + Kore.fancy + "6" + String.format("%,.1f", (purseValue + addCoins));
            System.out.println("found purse. New purse is " + newPurse);
            System.out.println("Values are purse: " + purseValue + " and add is " + addCoins);

            try {
                Field field = S3EPacketTeams.class.getDeclaredField("prefix");
                field.setAccessible(true);
                field.set(team, newPurse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}