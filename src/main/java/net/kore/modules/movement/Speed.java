package net.kore.modules.movement;

import net.kore.Kore;
import net.kore.events.*;
import net.kore.mixins.MinecraftAccessor;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.MilliTimer;
import net.kore.utils.MovementUtils;
import net.kore.utils.Notification;
import net.kore.utils.TimerUtils;
import net.kore.utils.font.Fonts;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Speed extends Module
{
    public BooleanSetting stopOnDisable;
    public BooleanSetting disableOnFlag;
    public BooleanSetting sneak;
    public NumberSetting timer;
    public NumberSetting sneakTimer;
    private static MilliTimer disable;
    int airTicks;
    boolean canApplySpeed;

    public Speed() {
        super("Speed", Category.MOVEMENT);
        this.stopOnDisable = new BooleanSetting("Stop on disable", true);
        this.disableOnFlag = new BooleanSetting("Disable on flag", true);
        this.sneak = new BooleanSetting("Sneak timer", true);
        this.timer = new NumberSetting("Timer", 1.0, 0.1, 3.0, 0.05);
        this.sneakTimer = new NumberSetting("SneakTimer", 1.0, 0.1, 3.0, 0.05, aBoolean -> !this.sneak.isEnabled());
        this.addSettings(this.stopOnDisable, this.disableOnFlag, this.sneak, this.sneakTimer, this.timer);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.speed = this;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdate(final MotionUpdateEvent.Pre event) {
        if (this.isToggled() && !isDisabled()) {
            ((MinecraftAccessor)Kore.mc).getTimer().timerSpeed = (float)((this.sneak.isEnabled() && Kore.mc.gameSettings.keyBindSneak.isKeyDown()) ? this.sneakTimer.getValue() : this.timer.getValue());
            if (MovementUtils.isMoving()) {
                event.setYaw(MovementUtils.getYaw());
            }
        }
    }

    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (this.isToggled() && !isDisabled()) {
            if (MovementUtils.isMoving()) {
                double multi = 1.0;
                if (Kore.mc.thePlayer.isPotionActive(Potion.moveSpeed) && this.canApplySpeed) {
                    multi += 0.015f * (Kore.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
                }
                if (Kore.mc.thePlayer.capabilities.getWalkSpeed() > 0.2f) {
                    multi = 0.8999999761581421;
                }
                final EntityPlayerSP thePlayer = Kore.mc.thePlayer;
                thePlayer.motionX *= multi;
                final EntityPlayerSP thePlayer2 = Kore.mc.thePlayer;
                thePlayer2.motionZ *= multi;
            }
            else {
                Kore.mc.thePlayer.motionX = 0.0;
                Kore.mc.thePlayer.motionZ = 0.0;
            }
            event.setX(Kore.mc.thePlayer.motionX).setZ(Kore.mc.thePlayer.motionZ);
        }
    }

    @SubscribeEvent
    public void onUpdateMove(final MoveStateUpdateEvent event) {
        if (this.isToggled() && !isDisabled()) {
            event.setSneak(false);
        }
    }

    @SubscribeEvent
    public void onMoveFlying(final MoveHeadingEvent event) {
        if (this.isToggled() && MovementUtils.isMoving() && !isDisabled()) {
            if (Kore.mc.thePlayer.onGround) {
                this.jump();
                this.canApplySpeed = Kore.mc.thePlayer.isPotionActive(Potion.moveSpeed);
                this.airTicks = 0;
            }
            else {
                ++this.airTicks;
                event.setOnGround(true);
                if (!Kore.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    if (!this.canApplySpeed) {
                        if (Kore.mc.thePlayer.fallDistance < 0.4 && Kore.mc.thePlayer.capabilities.getWalkSpeed() < 0.2f) {
                            event.setFriction2Multi(0.95f);
                        }
                    }
                    else {
                        event.setFriction2Multi(0.87f);
                    }
                }
            }
        }
    }

    public static boolean isDisabled() {
        return !Speed.disable.hasTimePassed(2000L);
    }

    private String getBPS() {
        final double bps = Math.hypot(Kore.mc.thePlayer.posX - Kore.mc.thePlayer.prevPosX, Kore.mc.thePlayer.posZ - Kore.mc.thePlayer.prevPosZ) * TimerUtils.getTimer().timerSpeed * 20.0;
        return String.format("%.2f", bps);
    }

    @SubscribeEvent
    public void onRender(final RenderGameOverlayEvent.Post event) {
        if (Kore.mc.theWorld == null || Kore.mc.thePlayer == null || !this.isToggled()) {
            return;
        }
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            final ScaledResolution resolution = new ScaledResolution(Kore.mc);
            Fonts.getPrimary().drawSmoothCenteredStringWithShadow(this.getBPS(), 20.0, resolution.getScaledHeight() - 20, Kore.clickGui.getColor().getRGB());
        }
    }

    @Override
    public void onDisable() {
        if (TimerUtils.getTimer() != null) {
            ((MinecraftAccessor)Kore.mc).getTimer().timerSpeed = 1.0f;
            this.canApplySpeed = false;
        }
        if (Kore.mc.thePlayer != null && this.stopOnDisable.isEnabled()) {
            Kore.mc.thePlayer.motionX = 0.0;
            Kore.mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onEnable() {
        this.airTicks = 0;
    }

    private void jump() {
        Kore.mc.thePlayer.motionY = 0.41999998688697815;
        if (Kore.mc.thePlayer.isSprinting()) {
            final float f = MovementUtils.getYaw() * 0.017453292f;
            final EntityPlayerSP thePlayer = Kore.mc.thePlayer;
            thePlayer.motionX -= MathHelper.sin(f) * 0.2f;
            final EntityPlayerSP thePlayer2 = Kore.mc.thePlayer;
            thePlayer2.motionZ += MathHelper.cos(f) * 0.2f;
        }
        Kore.mc.thePlayer.isAirBorne = true;
        Kore.mc.thePlayer.triggerAchievement(StatList.jumpStat);
        if (Kore.mc.thePlayer.isSprinting()) {
            Kore.mc.thePlayer.addExhaustion(0.8f);
        }
        else {
            Kore.mc.thePlayer.addExhaustion(0.2f);
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onPacket(final PacketReceivedEvent event) {
        if (event.packet instanceof S08PacketPlayerPosLook && this.disableOnFlag.isEnabled()) {
            if (!isDisabled() && this.isToggled()) {
                Kore.notificationManager.showNotification("Disabled speed due to a flag", 1500, Notification.NotificationType.WARNING);
                ((MinecraftAccessor)Kore.mc).getTimer().timerSpeed = 1.0f;
                this.canApplySpeed = false;
                if (Kore.mc.thePlayer != null) {
                    Kore.mc.thePlayer.motionX = 0.0;
                    Kore.mc.thePlayer.motionZ = 0.0;
                }
            }
            Speed.disable.reset();
        }
    }

    static {
        Speed.disable = new MilliTimer();
    }
}