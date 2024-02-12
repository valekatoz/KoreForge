package net.kore.modules.combat;

import net.kore.Kore;
import net.kore.events.MotionUpdateEvent;
import net.kore.events.PacketSentEvent;
import net.kore.mixins.player.PlayerSPAccessor;
import net.kore.modules.Module;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.MathUtils;
import net.kore.utils.MilliTimer;
import net.kore.utils.PacketUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;

public class Criticals extends Module
{
    public static final NumberSetting delay;
    public static final NumberSetting hurtTime;
    public static final ModeSetting mode;
    private C02PacketUseEntity attack;
    private int ticks;
    private float[] offsets;
    private MilliTimer timer;

    public Criticals() {
        super("Criticals", Category.COMBAT);
        this.ticks = 0;
        this.offsets = new float[] { 0.0625f, 0.03125f };
        this.timer = new MilliTimer();
        this.addSettings(Criticals.mode, Criticals.delay, Criticals.hurtTime);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.criticals = this;
    }

    @SubscribeEvent
    public void onUpdate(final MotionUpdateEvent.Pre event) {
        if (this.isToggled() && this.attack != null && !Kore.speed.isToggled()) {
            final String selected = Criticals.mode.getSelected();
            switch (selected) {
                case "Hypixel 2": {
                    if (Kore.mc.thePlayer.onGround && event.onGround && this.attack.getEntityFromWorld((World)Kore.mc.theWorld) instanceof EntityLivingBase && ((EntityLivingBase)this.attack.getEntityFromWorld((World)Kore.mc.theWorld)).hurtTime <= Criticals.hurtTime.getValue()) {
                        Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(((PlayerSPAccessor)Kore.mc.thePlayer).getLastReportedPosX(), ((PlayerSPAccessor)Kore.mc.thePlayer).getLastReportedPosY() + this.offsets[0] + MathUtils.getRandomInRange(0.0, 0.0010000000474974513), ((PlayerSPAccessor)Kore.mc.thePlayer).getLastReportedPosZ(), false));
                        Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(((PlayerSPAccessor)Kore.mc.thePlayer).getLastReportedPosX(), ((PlayerSPAccessor)Kore.mc.thePlayer).getLastReportedPosY() + this.offsets[1] + MathUtils.getRandomInRange(0.0, 0.0010000000474974513), ((PlayerSPAccessor)Kore.mc.thePlayer).getLastReportedPosZ(), false));
                        PacketUtils.sendPacketNoEvent((Packet<?>)this.attack);
                        this.attack = null;
                        Kore.sendMessageWithPrefix("Hypixel");
                        break;
                    }
                    this.attack = null;
                    break;
                }
                case "Hypixel": {
                    if (Kore.mc.thePlayer.onGround && this.attack != null && event.onGround && this.attack.getEntityFromWorld((World)Kore.mc.theWorld) instanceof EntityLivingBase && ((EntityLivingBase)this.attack.getEntityFromWorld((World)Kore.mc.theWorld)).hurtTime <= Criticals.hurtTime.getValue()) {
                        switch (this.ticks++) {
                            case 0:
                            case 1: {
                                event.y += this.offsets[this.ticks - 1] + MathUtils.getRandomInRange(0.0, 0.0010000000474974513);
                                event.setOnGround(false);
                                Kore.sendMessageWithPrefix("Hypixel 2");
                                break;
                            }
                            case 2: {
                                PacketUtils.sendPacketNoEvent((Packet<?>)this.attack);
                                this.ticks = 0;
                                this.attack = null;
                                break;
                            }
                        }
                        break;
                    }
                    this.ticks = 0;
                    this.attack = null;
                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.attack = null;
        this.ticks = 0;
    }

    @SubscribeEvent
    public void onPacket(final PacketSentEvent event) {
        if (this.isToggled() && !Kore.speed.isToggled() && event.packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.packet).getAction() == C02PacketUseEntity.Action.ATTACK && ((C02PacketUseEntity)event.packet).getEntityFromWorld((World)Kore.mc.theWorld) instanceof EntityLivingBase && ((EntityLivingBase)((C02PacketUseEntity)event.packet).getEntityFromWorld((World)Kore.mc.theWorld)).hurtTime <= Criticals.hurtTime.getValue() && this.timer.hasTimePassed((long)Criticals.delay.getValue())) {
            this.attack = (C02PacketUseEntity)event.packet;
            event.setCanceled(true);
            this.timer.reset();
        }
    }

    static {
        delay = new NumberSetting("Delay", 500.0, 0.0, 2000.0, 50.0);
        hurtTime = new NumberSetting("Hurt time", 2.0, 0.0, 10.0, 1.0);
        mode = new ModeSetting("Mode", "Hypixel", new String[] { "Hypixel", "Hypixel 2" });
    }
}

