package net.kore.modules.combat;

import net.kore.Kore;
import net.kore.events.MotionUpdateEvent;
import net.kore.events.PacketReceivedEvent;
import net.kore.events.PacketSentEvent;
import net.kore.modules.Module;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.MilliTimer;
import net.kore.utils.PacketUtils;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S30PacketWindowItems;

public class NoSlow extends Module
{
    public NumberSetting eatingSlowdown;
    public NumberSetting swordSlowdown;
    public NumberSetting bowSlowdown;
    public ModeSetting mode;
    private final MilliTimer blockDelay;

    public NoSlow() {
        super("NoSlow", 0, Category.COMBAT);
        this.eatingSlowdown = new NumberSetting("Eating slow", 1.0, 0.2, 1.0, 0.1);
        this.swordSlowdown = new NumberSetting("Sword slow", 1.0, 0.2, 1.0, 0.1);
        this.bowSlowdown = new NumberSetting("Bow slow", 1.0, 0.2, 1.0, 0.1);
        this.mode = new ModeSetting("Mode", "Hypixel", new String[] { "Hypixel", "Vanilla" });
        this.blockDelay = new MilliTimer();
        this.addSettings(this.mode, this.swordSlowdown, this.bowSlowdown, this.eatingSlowdown);
    }

    @Override
    public void assign()
    {
        Kore.noSlow = this;
    }

    @SubscribeEvent
    public void onPacket(final PacketReceivedEvent event) {
        if (event.packet instanceof S30PacketWindowItems && Kore.mc.thePlayer != null && this.isToggled() && this.mode.is("Hypixel") && Kore.mc.thePlayer.isUsingItem() && Kore.mc.thePlayer.getItemInUse().getItem() instanceof ItemSword) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void unUpdate(final MotionUpdateEvent.Post event) {
        if (this.isToggled() && Kore.mc.thePlayer.isUsingItem() && this.mode.is("Hypixel")) {
            if (this.blockDelay.hasTimePassed(250L) && Kore.mc.thePlayer.getItemInUse().getItem() instanceof ItemSword) {
                Kore.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C08PacketPlayerBlockPlacement(Kore.mc.thePlayer.getHeldItem()));
                Kore.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)Kore.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                Kore.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)Kore.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                this.blockDelay.reset();
            }
            PacketUtils.sendPacketNoEvent((Packet<?>)new C09PacketHeldItemChange(Kore.mc.thePlayer.inventory.currentItem));
        }
    }

    @SubscribeEvent
    public void onPacket(final PacketSentEvent event) {
        if (this.isToggled() && this.mode.is("Hypixel") && event.packet instanceof C08PacketPlayerBlockPlacement && ((C08PacketPlayerBlockPlacement)event.packet).getStack() != null && ((C08PacketPlayerBlockPlacement)event.packet).getStack().getItem() instanceof ItemSword) {
            this.blockDelay.reset();
        }
    }
}
