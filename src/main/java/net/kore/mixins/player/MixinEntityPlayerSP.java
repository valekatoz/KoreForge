package net.kore.mixins.player;

import com.jagrosh.discordipc.entities.Packet;
import net.kore.Kore;
import net.kore.events.MotionUpdateEvent;
import net.kore.events.MoveFlyingEvent;
import net.kore.events.PlayerUpdateEvent;
import net.kore.managers.CommandManager;
import net.kore.modules.combat.KillAura;
import net.kore.utils.MovementUtils;
import net.kore.utils.PlayerUtils;
import net.kore.utils.rotation.RotationUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatList;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = { EntityPlayerSP.class }, priority = 1)
public abstract class MixinEntityPlayerSP extends MixinPlayer {
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    private int positionUpdateTicks;
    @Shadow
    private boolean serverSneakState;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    public abstract boolean isSneaking();
    @Shadow
    protected abstract boolean isCurrentViewEntity();

    @Inject(method = { "sendChatMessage" }, at = { @At("HEAD") }, cancellable = true)
    public void onSenChatMessage(final String message, final CallbackInfo ci) {
        if (CommandManager.handle(message)) {
            ci.cancel();
        }
    }

    @Inject(method = { "onUpdate" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isRiding()Z") }, cancellable = true)
    private void onUpdate(final CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post((Event)new PlayerUpdateEvent())) {
            ci.cancel();
        }
    }

    @Override
    public void moveFlying(float strafe, float forward, float friction) {
        final MoveFlyingEvent event = new MoveFlyingEvent(forward, strafe, friction, this.rotationYaw);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        strafe = event.getStrafe();
        forward = event.getForward();
        friction = event.getFriction();
        float f = strafe * strafe + forward * forward;
        if (f >= 1.0E-4f) {
            f = MathHelper.sqrt_float(f);
            if (f < 1.0f) {
                f = 1.0f;
            }
            f = friction / f;
            strafe *= f;
            forward *= f;
            final float yaw = event.getYaw();
            final float f2 = MathHelper.sin(yaw * 3.1415927f / 180.0f);
            final float f3 = MathHelper.cos(yaw * 3.1415927f / 180.0f);
            this.motionX += strafe * f3 - forward * f2;
            this.motionZ += forward * f3 + strafe * f2;
        }
    }

    public void jump() {
        this.motionY = this.getJumpUpwardsMotion();
        if (this.isPotionActive(Potion.jump.id)) {
            this.motionY += (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
        }
        if (this.isSprinting() && MovementUtils.isMoving()) {
            final float f = ((Kore.killAura.isToggled() && Kore.killAura.target != null && Kore.killAura.movementFix.isEnabled()) ? RotationUtils.getRotations(KillAura.target).getYaw() : this.rotationYaw) * 0.017453292f;
            this.motionX -= MathHelper.sin(f) * 0.2f;
            this.motionZ += MathHelper.cos(f) * 0.2f;
        }
        this.isAirBorne = true;
        ForgeHooks.onLivingJump((EntityLivingBase)(Object)this);
        this.triggerAchievement(StatList.jumpStat);
        if (this.isSprinting()) {
            this.addExhaustion(0.8f);
        }
        else {
            this.addExhaustion(0.2f);
        }
    }

    /**
     * @author not me
     * @reason D:
     */
    @Overwrite
    public void onUpdateWalkingPlayer() {
        final MotionUpdateEvent event = new MotionUpdateEvent.Pre(this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround, this.isSprinting(), this.isSneaking());
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        final boolean flag = event.sprinting;
        if (flag != this.serverSprintState) {
            if (flag) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(Kore.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            }
            else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(Kore.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSprintState = flag;
        }
        final boolean flag2 = event.sneaking;
        if (flag2 != this.serverSneakState) {
            if (flag2) {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(Kore.mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }
            else {
                this.sendQueue.addToSendQueue(new C0BPacketEntityAction(Kore.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.serverSneakState = flag2;
        }
        if (this.isCurrentViewEntity()) {
            final double d0 = event.x - this.lastReportedPosX;
            final double d2 = event.y - this.lastReportedPosY;
            final double d3 = event.z - this.lastReportedPosZ;
            final double d4 = event.yaw - this.lastReportedYaw;
            final double d5 = event.pitch - this.lastReportedPitch;
            boolean flag3 = d0 * d0 + d2 * d2 + d3 * d3 > 9.0E-4 || this.positionUpdateTicks >= 20;
            final boolean flag4 = d4 != 0.0 || d5 != 0.0;
            if (this.ridingEntity == null) {
                if (flag3 && flag4) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
                }
                else if (flag3) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(event.x, event.y, event.z, event.onGround));
                }
                else if (flag4) {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(event.yaw, event.pitch, event.onGround));
                }
                else {
                    this.sendQueue.addToSendQueue(new C03PacketPlayer(event.onGround));
                }
            }
            else {
                this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, event.yaw, event.pitch, event.onGround));
                flag3 = false;
            }
            ++this.positionUpdateTicks;
            if (flag3) {
                this.lastReportedPosX = event.x;
                this.lastReportedPosY = event.y;
                this.lastReportedPosZ = event.z;
                this.positionUpdateTicks = 0;
            }
            PlayerUtils.lastGround = event.onGround;
            RotationUtils.lastLastReportedPitch = this.lastReportedPitch;
            if (flag4) {
                this.lastReportedYaw = event.yaw;
                this.lastReportedPitch = event.pitch;
            }
        }
        MinecraftForge.EVENT_BUS.post((Event)new MotionUpdateEvent.Post(event));
    }
}
