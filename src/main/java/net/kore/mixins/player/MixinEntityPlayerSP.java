package net.kore.mixins.player;

import net.kore.Kore;
import net.kore.events.*;
import net.kore.managers.CommandManager;
import net.kore.modules.combat.KillAura;
import net.kore.modules.movement.Scaffold;
import net.kore.modules.movement.Speed;
import net.kore.utils.MovementUtils;
import net.kore.utils.PlayerUtils;
import net.kore.utils.rotation.RotationUtils;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.stats.AchievementList;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.block.Block;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.ReportedException;
import net.minecraft.crash.CrashReport;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.item.EnumAction;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.stats.StatList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.ForgeHooks;
import net.minecraft.util.MathHelper;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.DamageSource;
import net.minecraft.stats.StatBase;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.util.MovementInput;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { EntityPlayerSP.class }, priority = 1)
public abstract class MixinEntityPlayerSP extends MixinPlayer
{
    @Shadow
    public MovementInput movementInput;
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;
    @Shadow
    public float timeInPortal;
    @Shadow
    public float prevRenderArmPitch;
    @Shadow
    public float prevRenderArmYaw;
    @Shadow
    public float renderArmPitch;
    @Shadow
    public float renderArmYaw;
    @Shadow
    private boolean serverSprintState;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private int positionUpdateTicks;
    @Shadow
    private boolean serverSneakState;
    @Shadow
    public int sprintingTicksLeft;
    @Shadow
    protected int sprintToggleTimer;
    @Shadow
    protected Minecraft mc;

    @Shadow
    @Override
    public abstract void setSprinting(final boolean p0);

    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    public abstract void onCriticalHit(final Entity p0);

    @Shadow
    public abstract void onEnchantmentCritical(final Entity p0);

    @Shadow
    public abstract void addStat(final StatBase p0, final int p1);

    @Shadow
    protected abstract boolean isCurrentViewEntity();

    @Shadow
    public abstract void playSound(final String p0, final float p1, final float p2);

    @Shadow
    @Override
    public abstract boolean attackEntityFrom(final DamageSource p0, final float p1);

    @Shadow
    public abstract boolean isServerWorld();

    @Shadow
    public abstract void mountEntity(final Entity p0);

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

    /**
     * @author
     * @reason
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
                this.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction(Kore.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            }
            else {
                this.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction(Kore.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSprintState = flag;
        }
        final boolean flag2 = event.sneaking;
        if (flag2 != this.serverSneakState) {
            if (flag2) {
                this.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction(Kore.mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
            }
            else {
                this.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction(Kore.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
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
                    this.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C06PacketPlayerPosLook(event.x, event.y, event.z, event.yaw, event.pitch, event.onGround));
                }
                else if (flag3) {
                    this.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C04PacketPlayerPosition(event.x, event.y, event.z, event.onGround));
                }
                else if (flag4) {
                    this.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C05PacketPlayerLook(event.yaw, event.pitch, event.onGround));
                }
                else {
                    this.sendQueue.addToSendQueue((Packet)new C03PacketPlayer(event.onGround));
                }
            }
            else {
                this.sendQueue.addToSendQueue((Packet)new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, event.yaw, event.pitch, event.onGround));
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

    public void jump() {
        this.motionY = this.getJumpUpwardsMotion();
        if (this.isPotionActive(Potion.jump.id)) {
            this.motionY += (this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1f;
        }
        if (this.isSprinting() && MovementUtils.isMoving()) {
            final float f = ((Kore.sprint.isToggled() && Kore.sprint.omni.isEnabled()) ? MovementUtils.getYaw() : ((Kore.killAura.isToggled() && KillAura.target != null && Kore.killAura.movementFix.isEnabled()) ? RotationUtils.getRotations(KillAura.target).getYaw() : this.rotationYaw)) * 0.017453292f;
            this.motionX -= MathHelper.sin(f) * 0.2f;
            this.motionZ += MathHelper.cos(f) * 0.2f;
        }
        this.isAirBorne = true;
        ForgeHooks.onLivingJump((EntityLivingBase)Kore.mc.thePlayer);
        this.triggerAchievement(StatList.jumpStat);
        if (this.isSprinting()) {
            this.addExhaustion(0.8f);
        }
        else {
            this.addExhaustion(0.2f);
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

    public void superMoveEntityWithHeading(final float strafe, final float forward, final boolean onGround, final float friction2Multi) {
        if (this.isServerWorld()) {
            if (!this.isInWater() || ((Kore.mc.thePlayer) instanceof EntityPlayer && this.capabilities.isFlying)) {
                if (!this.isInLava() || ((Kore.mc.thePlayer) instanceof EntityPlayer && this.capabilities.isFlying)) {
                    float f4 = 0.91f;
                    if (onGround) {
                        f4 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * 0.91f;
                    }
                    final float f5 = 0.16277136f / (f4 * f4 * f4);
                    float f6;
                    if (onGround) {
                        f6 = this.getAIMoveSpeed() * f5;
                    }
                    else {
                        f6 = this.jumpMovementFactor;
                    }
                    this.moveFlying(strafe, forward, f6);
                    f4 = 0.91f;
                    if (onGround) {
                        f4 = this.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ))).getBlock().slipperiness * friction2Multi;
                    }
                    if (this.isOnLadder()) {
                        final float f7 = 0.15f;
                        this.motionX = MathHelper.clamp_double(this.motionX, (double)(-f7), (double)f7);
                        this.motionZ = MathHelper.clamp_double(this.motionZ, (double)(-f7), (double)f7);
                        this.fallDistance = 0.0f;
                        if (this.motionY < -0.15) {
                            this.motionY = -0.15;
                        }
                        final boolean flag = this.isSneaking() && (Kore.mc.thePlayer) instanceof EntityPlayer;
                        if (flag && this.motionY < 0.0) {
                            this.motionY = 0.0;
                        }
                    }
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    if (this.isCollidedHorizontally && this.isOnLadder()) {
                        this.motionY = 0.2;
                    }
                    if (this.worldObj.isRemote && (!this.worldObj.isBlockLoaded(new BlockPos((int)this.posX, 0, (int)this.posZ)) || !this.worldObj.getChunkFromBlockCoords(new BlockPos((int)this.posX, 0, (int)this.posZ)).isLoaded())) {
                        if (this.posY > 0.0) {
                            this.motionY = -0.1;
                        }
                        else {
                            this.motionY = 0.0;
                        }
                    }
                    else {
                        this.motionY -= 0.08;
                    }
                    this.motionY *= 0.9800000190734863;
                    this.motionX *= f4;
                    this.motionZ *= f4;
                }
                else {
                    final double d1 = this.posY;
                    this.moveFlying(strafe, forward, 0.02f);
                    this.moveEntity(this.motionX, this.motionY, this.motionZ);
                    this.motionX *= 0.5;
                    this.motionY *= 0.5;
                    this.motionZ *= 0.5;
                    this.motionY -= 0.02;
                    if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579 - this.posY + d1, this.motionZ)) {
                        this.motionY = 0.30000001192092896;
                    }
                }
            }
            else {
                final double d2 = this.posY;
                float f8 = 0.8f;
                float f9 = 0.02f;
                float f10 = (float)EnchantmentHelper.getDepthStriderModifier(Kore.mc.thePlayer);
                if (f10 > 3.0f) {
                    f10 = 3.0f;
                }
                if (!this.onGround) {
                    f10 *= 0.5f;
                }
                if (f10 > 0.0f) {
                    f8 += (0.54600006f - f8) * f10 / 3.0f;
                    f9 += (this.getAIMoveSpeed() * 1.0f - f9) * f10 / 3.0f;
                }
                this.moveFlying(strafe, forward, f9);
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
                this.motionX *= f8;
                this.motionY *= 0.800000011920929;
                this.motionZ *= f8;
                this.motionY -= 0.02;
                if (this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + 0.6000000238418579 - this.posY + d2, this.motionZ)) {
                    this.motionY = 0.30000001192092896;
                }
            }
        }
        this.prevLimbSwingAmount = this.limbSwingAmount;
        final double d3 = this.posX - this.prevPosX;
        final double d4 = this.posZ - this.prevPosZ;
        float f11 = MathHelper.sqrt_double(d3 * d3 + d4 * d4) * 4.0f;
        if (f11 > 1.0f) {
            f11 = 1.0f;
        }
        this.limbSwingAmount += (f11 - this.limbSwingAmount) * 0.4f;
        this.limbSwing += this.limbSwingAmount;
    }

    @Inject(method = { "pushOutOfBlocks" }, at = { @At("HEAD") }, cancellable = true)
    public void pushOutOfBlocks(final double d2, final double f, final double blockpos, final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Redirect(method = { "onLivingUpdate" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isUsingItem()Z"))
    public boolean isUsingItem(final EntityPlayerSP instance) {
        return !Kore.noSlow.isToggled() && instance.isUsingItem();
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return false;
    }

    @Inject(method = { "onLivingUpdate" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;onLivingUpdate()V") }, cancellable = true)
    public void onLivingUpdate(final CallbackInfo ci) {
        if (Kore.sprint.omni.isEnabled() && Kore.sprint.isToggled()) {
            if (!MovementUtils.isMoving() || this.isSneaking() || (this.getFoodStats().getFoodLevel() <= 6.0f && !this.capabilities.allowFlying)) {
                if (this.isSprinting()) {
                    this.setSprinting(false);
                }
            }
            else if (!this.isSprinting()) {
                this.setSprinting(true);
            }
        }
        if (Kore.speed.isToggled() && !Speed.isDisabled() && this.getFoodStats().getFoodLevel() > 6.0f && !this.isSprinting()) {
            this.setSprinting(true);
        }
        if (Kore.scaffold.isToggled()) {
            if (Scaffold.sprint.is("None") && this.isSprinting()) {
                this.setSprinting(false);
            }
            else if (!this.isSprinting() && MovementUtils.isMoving()) {
                this.setSprinting(true);
            }
        }
        if (Kore.noSlow.isToggled() && this.isUsingItem()) {
            final EnumAction action = this.getHeldItem().getItem().getItemUseAction(this.getHeldItem());
            if (action == EnumAction.BLOCK) {
                final MovementInput movementInput = this.movementInput;
                movementInput.moveForward *= (float)Kore.noSlow.swordSlowdown.getValue();
                final MovementInput movementInput2 = this.movementInput;
                movementInput2.moveStrafe *= (float)Kore.noSlow.swordSlowdown.getValue();
            }
            else if (action == EnumAction.BOW) {
                final MovementInput movementInput3 = this.movementInput;
                movementInput3.moveForward *= (float)Kore.noSlow.bowSlowdown.getValue();
                final MovementInput movementInput4 = this.movementInput;
                movementInput4.moveStrafe *= (float)Kore.noSlow.bowSlowdown.getValue();
            }
            else if (action != EnumAction.NONE) {
                final MovementInput movementInput5 = this.movementInput;
                movementInput5.moveForward *= (float)Kore.noSlow.eatingSlowdown.getValue();
                final MovementInput movementInput6 = this.movementInput;
                movementInput6.moveStrafe *= (float)Kore.noSlow.eatingSlowdown.getValue();
            }
        }
        if (Kore.freeCam.isToggled()) {
            this.noClip = true;
        }
    }

    @Override
    public void moveEntityWithHeading(final float strafe, final float forward) {
        final MoveHeadingEvent event = new MoveHeadingEvent(this.onGround);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        final double d0 = this.posX;
        final double d2 = this.posY;
        final double d3 = this.posZ;
        if (this.capabilities.isFlying && this.ridingEntity == null) {
            final double d4 = this.motionY;
            final float f = this.jumpMovementFactor;
            this.jumpMovementFactor = this.capabilities.getFlySpeed() * (this.isSprinting() ? 2 : 1);
            super.moveEntityWithHeading(strafe, forward);
            this.motionY = d4 * 0.6;
            this.jumpMovementFactor = f;
        }
        else {
            this.superMoveEntityWithHeading(strafe, forward, event.isOnGround(), event.getFriction2Multi());
        }
        this.addMovementStat(this.posX - d0, this.posY - d2, this.posZ - d3);
    }

    @Override
    public void moveEntity(double x, double y, double z) {
        final MoveEvent event = new MoveEvent(x, y, z);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        x = event.getX();
        y = event.getY();
        z = event.getZ();
        if (this.noClip) {
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
            this.doResetPositionToBB();
        }
        else {
            this.worldObj.theProfiler.startSection("move");
            final double d0 = this.posX;
            final double d2 = this.posY;
            final double d3 = this.posZ;
            if (this.isInWeb) {
                this.isInWeb = false;
                x *= 0.25;
                y *= 0.05000000074505806;
                z *= 0.25;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }
            double d4 = x;
            final double d5 = y;
            double d6 = z;
            final boolean flag = ((this.onGround && this.isSneaking()) || (PlayerUtils.isOnGround(1.0) && Kore.scaffold.isToggled() && Scaffold.safeWalk.isEnabled())) && (Kore.mc.thePlayer) instanceof EntityPlayer;
            if (flag) {
                final double d7 = 0.05;
                while (x != 0.0 && this.worldObj.getCollidingBoundingBoxes(Kore.mc.thePlayer, this.getEntityBoundingBox().offset(x, -1.0, 0.0)).isEmpty()) {
                    if (x < d7 && x >= -d7) {
                        x = 0.0;
                    }
                    else if (x > 0.0) {
                        x -= d7;
                    }
                    else {
                        x += d7;
                    }
                    d4 = x;
                }
                while (z != 0.0 && this.worldObj.getCollidingBoundingBoxes(Kore.mc.thePlayer, this.getEntityBoundingBox().offset(0.0, -1.0, z)).isEmpty()) {
                    if (z < d7 && z >= -d7) {
                        z = 0.0;
                    }
                    else if (z > 0.0) {
                        z -= d7;
                    }
                    else {
                        z += d7;
                    }
                    d6 = z;
                }
                while (x != 0.0 && z != 0.0 && this.worldObj.getCollidingBoundingBoxes(Kore.mc.thePlayer, this.getEntityBoundingBox().offset(x, -1.0, z)).isEmpty()) {
                    if (x < d7 && x >= -d7) {
                        x = 0.0;
                    }
                    else if (x > 0.0) {
                        x -= d7;
                    }
                    else {
                        x += d7;
                    }
                    d4 = x;
                    if (z < d7 && z >= -d7) {
                        z = 0.0;
                    }
                    else if (z > 0.0) {
                        z -= d7;
                    }
                    else {
                        z += d7;
                    }
                    d6 = z;
                }
            }
            final List<AxisAlignedBB> list1 = (List<AxisAlignedBB>)this.worldObj.getCollidingBoundingBoxes(Kore.mc.thePlayer, this.getEntityBoundingBox().addCoord(x, y, z));
            final AxisAlignedBB axisalignedbb = this.getEntityBoundingBox();
            for (final AxisAlignedBB axisalignedbb2 : list1) {
                y = axisalignedbb2.calculateYOffset(this.getEntityBoundingBox(), y);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
            final boolean flag2 = this.onGround || (d5 != y && d5 < 0.0);
            for (final AxisAlignedBB axisalignedbb3 : list1) {
                x = axisalignedbb3.calculateXOffset(this.getEntityBoundingBox(), x);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0, 0.0));
            for (final AxisAlignedBB axisalignedbb4 : list1) {
                z = axisalignedbb4.calculateZOffset(this.getEntityBoundingBox(), z);
            }
            this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, 0.0, z));
            if (this.stepHeight > 0.0f && flag2 && (d4 != x || d6 != z)) {
                final double d8 = x;
                final double d9 = y;
                final double d10 = z;
                final AxisAlignedBB axisalignedbb5 = this.getEntityBoundingBox();
                this.setEntityBoundingBox(axisalignedbb);
                final StepEvent.Pre stepEvent = new StepEvent.Pre(this.stepHeight);
                MinecraftForge.EVENT_BUS.post((Event)stepEvent);
                y = stepEvent.getHeight();
                final List<AxisAlignedBB> list2 = (List<AxisAlignedBB>)this.worldObj.getCollidingBoundingBoxes(Kore.mc.thePlayer, this.getEntityBoundingBox().addCoord(d4, y, d6));
                AxisAlignedBB axisalignedbb6 = this.getEntityBoundingBox();
                final AxisAlignedBB axisalignedbb7 = axisalignedbb6.addCoord(d4, 0.0, d6);
                double d11 = y;
                for (final AxisAlignedBB axisalignedbb8 : list2) {
                    d11 = axisalignedbb8.calculateYOffset(axisalignedbb7, d11);
                }
                axisalignedbb6 = axisalignedbb6.offset(0.0, d11, 0.0);
                double d12 = d4;
                for (final AxisAlignedBB axisalignedbb9 : list2) {
                    d12 = axisalignedbb9.calculateXOffset(axisalignedbb6, d12);
                }
                axisalignedbb6 = axisalignedbb6.offset(d12, 0.0, 0.0);
                double d13 = d6;
                for (final AxisAlignedBB axisalignedbb10 : list2) {
                    d13 = axisalignedbb10.calculateZOffset(axisalignedbb6, d13);
                }
                axisalignedbb6 = axisalignedbb6.offset(0.0, 0.0, d13);
                AxisAlignedBB axisalignedbb11 = this.getEntityBoundingBox();
                double d14 = y;
                for (final AxisAlignedBB axisalignedbb12 : list2) {
                    d14 = axisalignedbb12.calculateYOffset(axisalignedbb11, d14);
                }
                axisalignedbb11 = axisalignedbb11.offset(0.0, d14, 0.0);
                double d15 = d4;
                for (final AxisAlignedBB axisalignedbb13 : list2) {
                    d15 = axisalignedbb13.calculateXOffset(axisalignedbb11, d15);
                }
                axisalignedbb11 = axisalignedbb11.offset(d15, 0.0, 0.0);
                double d16 = d6;
                for (final AxisAlignedBB axisalignedbb14 : list2) {
                    d16 = axisalignedbb14.calculateZOffset(axisalignedbb11, d16);
                }
                axisalignedbb11 = axisalignedbb11.offset(0.0, 0.0, d16);
                final double d17 = d12 * d12 + d13 * d13;
                final double d18 = d15 * d15 + d16 * d16;
                if (d17 > d18) {
                    x = d12;
                    z = d13;
                    y = -d11;
                    this.setEntityBoundingBox(axisalignedbb6);
                }
                else {
                    x = d15;
                    z = d16;
                    y = -d14;
                    this.setEntityBoundingBox(axisalignedbb11);
                }
                for (final AxisAlignedBB axisalignedbb15 : list2) {
                    y = axisalignedbb15.calculateYOffset(this.getEntityBoundingBox(), y);
                }
                this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0, y, 0.0));
                if (d8 * d8 + d10 * d10 >= x * x + z * z) {
                    x = d8;
                    y = d9;
                    z = d10;
                    this.setEntityBoundingBox(axisalignedbb5);
                }
                else {
                    MinecraftForge.EVENT_BUS.post((Event)new StepEvent.Post(1.0 + y));
                }
            }
            this.worldObj.theProfiler.endSection();
            this.worldObj.theProfiler.startSection("rest");
            this.doResetPositionToBB();
            this.isCollidedHorizontally = (d4 != x || d6 != z);
            this.isCollidedVertically = (d5 != y);
            this.onGround = (this.isCollidedVertically && d5 < 0.0);
            this.isCollided = (this.isCollidedHorizontally || this.isCollidedVertically);
            final int i = MathHelper.floor_double(this.posX);
            final int j = MathHelper.floor_double(this.posY - 0.20000000298023224);
            final int k = MathHelper.floor_double(this.posZ);
            BlockPos blockpos = new BlockPos(i, j, k);
            Block block1 = this.worldObj.getBlockState(blockpos).getBlock();
            if (block1.getMaterial() == Material.air) {
                final Block block2 = this.worldObj.getBlockState(blockpos.down()).getBlock();
                if (block2 instanceof BlockFence || block2 instanceof BlockWall || block2 instanceof BlockFenceGate) {
                    block1 = block2;
                    blockpos = blockpos.down();
                }
            }
            this.updateFallState(y, this.onGround, block1, blockpos);
            if (d4 != x) {
                this.motionX = 0.0;
            }
            if (d6 != z) {
                this.motionZ = 0.0;
            }
            if (d5 != y) {
                block1.onLanded(this.worldObj, Kore.mc.thePlayer);
            }
            if (this.canTriggerWalking() && !flag && this.ridingEntity == null) {
                final double d19 = this.posX - d0;
                double d20 = this.posY - d2;
                final double d21 = this.posZ - d3;
                if (block1 != Blocks.ladder) {
                    d20 = 0.0;
                }
                if (block1 != null && this.onGround) {
                    block1.onEntityCollidedWithBlock(this.worldObj, blockpos, Kore.mc.thePlayer);
                }
                this.distanceWalkedModified += (float)(MathHelper.sqrt_double(d19 * d19 + d21 * d21) * 0.6);
                this.distanceWalkedOnStepModified += (float)(MathHelper.sqrt_double(d19 * d19 + d20 * d20 + d21 * d21) * 0.6);
                if (this.distanceWalkedOnStepModified > this.getNextStepDistance() && block1.getMaterial() != Material.air) {
                    this.setNextStepDistance((int)this.distanceWalkedOnStepModified + 1);
                    if (this.isInWater()) {
                        float f = MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224 + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224) * 0.35f;
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        this.playSound(this.getSwimSound(), f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                    }
                    this.playStepSound(blockpos, block1);
                }
            }
            try {
                this.doBlockCollisions();
            }
            catch (Throwable throwable) {
                final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
                final CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
                this.addEntityCrashInfo(crashreportcategory);
                throw new ReportedException(crashreport);
            }
            final boolean flag3 = this.isWet();
            if (this.worldObj.isFlammableWithin(this.getEntityBoundingBox().contract(0.001, 0.001, 0.001))) {
                this.dealFireDamage(1);
                if (!flag3) {
                    this.plusPlusFire();
                    if (this.getFire() == 0) {
                        this.setFire(8);
                    }
                }
            }
            else if (this.getFire() <= 0) {
                this.SetFire(-this.fireResistance);
            }
            if (flag3 && this.getFire() > 0) {
                this.playSound("random.fizz", 0.7f, 1.6f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4f);
                this.SetFire(-this.fireResistance);
            }
            this.worldObj.theProfiler.endSection();
        }
    }

    @Inject(method = { "updateEntityActionState" }, at = { @At("RETURN") })
    public void onUpdateAction(final CallbackInfo ci) {
        if (Kore.speed.isToggled() && !Speed.isDisabled() && MovementUtils.isMoving()) {
            this.isJumping = false;
        }
    }

    @Override
    public void attackTargetEntityWithCurrentItem(final Entity targetEntity) {
        if (ForgeHooks.onPlayerAttackTarget((EntityPlayer)Kore.mc.thePlayer, targetEntity) && targetEntity.canAttackWithItem() && !targetEntity.hitByEntity(Kore.mc.thePlayer)) {
            float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
            int i = 0;
            float f2 = 0.0f;
            if (targetEntity instanceof EntityLivingBase) {
                f2 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), ((EntityLivingBase)targetEntity).getCreatureAttribute());
            }
            else {
                f2 = EnchantmentHelper.getModifierForCreature(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
            }
            i += EnchantmentHelper.getKnockbackModifier((EntityLivingBase)Kore.mc.thePlayer);
            if (this.isSprinting()) {
                ++i;
            }
            if (f > 0.0f || f2 > 0.0f) {
                final boolean flag = this.fallDistance > 0.0f && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null && targetEntity instanceof EntityLivingBase;
                if (flag && f > 0.0f) {
                    f *= 1.5f;
                }
                f += f2;
                boolean flag2 = false;
                final int j = EnchantmentHelper.getFireAspectModifier((EntityLivingBase)Kore.mc.thePlayer);
                if (targetEntity instanceof EntityLivingBase && j > 0 && !targetEntity.isBurning()) {
                    flag2 = true;
                    targetEntity.setFire(1);
                }
                final double d0 = targetEntity.motionX;
                final double d2 = targetEntity.motionY;
                final double d3 = targetEntity.motionZ;
                final boolean flag3 = targetEntity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)Kore.mc.thePlayer), f);
                if (flag3) {
                    if (i > 0) {
                        targetEntity.addVelocity((double)(-MathHelper.sin(this.rotationYaw * 3.1415927f / 180.0f) * i * 0.5f), 0.1, (double)(MathHelper.cos(this.rotationYaw * 3.1415927f / 180.0f) * i * 0.5f));
                        if (!Kore.sprint.isToggled() || !Kore.sprint.keep.isEnabled()) {
                            this.motionX *= 0.6;
                            this.motionZ *= 0.6;
                            this.setSprinting(false);
                        }
                    }
                    if (targetEntity instanceof EntityPlayerMP && targetEntity.velocityChanged) {
                        ((EntityPlayerMP)targetEntity).playerNetServerHandler.sendPacket((Packet)new S12PacketEntityVelocity(targetEntity));
                        targetEntity.velocityChanged = false;
                        targetEntity.motionX = d0;
                        targetEntity.motionY = d2;
                        targetEntity.motionZ = d3;
                    }
                    if (flag) {
                        this.onCriticalHit(targetEntity);
                    }
                    if (f2 > 0.0f) {
                        this.onEnchantmentCritical(targetEntity);
                    }
                    if (f >= 18.0f) {
                        this.triggerAchievement((StatBase)AchievementList.overkill);
                    }
                    this.setLastAttacker(targetEntity);
                    if (targetEntity instanceof EntityLivingBase) {
                        EnchantmentHelper.applyThornEnchantments((EntityLivingBase)targetEntity, Kore.mc.thePlayer);
                    }
                    EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase)Kore.mc.thePlayer, targetEntity);
                    final ItemStack itemstack = this.getCurrentEquippedItem();
                    Entity entity = targetEntity;
                    if (targetEntity instanceof EntityDragonPart) {
                        final IEntityMultiPart ientitymultipart = ((EntityDragonPart)targetEntity).entityDragonObj;
                        if (ientitymultipart instanceof EntityLivingBase) {
                            entity = (Entity)ientitymultipart;
                        }
                    }
                    if (itemstack != null && entity instanceof EntityLivingBase) {
                        itemstack.hitEntity((EntityLivingBase)entity, (EntityPlayer)Kore.mc.thePlayer);
                        if (itemstack.stackSize <= 0) {
                            this.destroyCurrentEquippedItem();
                        }
                    }
                    if (targetEntity instanceof EntityLivingBase) {
                        this.addStat(StatList.damageDealtStat, Math.round(f * 10.0f));
                        if (j > 0) {
                            targetEntity.setFire(j * 4);
                        }
                    }
                    this.addExhaustion(0.3f);
                }
                else if (flag2) {
                    targetEntity.extinguish();
                }
            }
        }
    }
}