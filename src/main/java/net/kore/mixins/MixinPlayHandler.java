package net.kore.mixins;

import net.kore.Kore;
import net.kore.utils.SkyblockUtils;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.util.IThreadListener;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.play.server.S27PacketExplosion;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.network.NetworkManager;
import net.minecraft.client.multiplayer.WorldClient;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { NetHandlerPlayClient.class }, priority = 1)
public abstract class MixinPlayHandler
{
    @Shadow
    private Minecraft gameController;
    @Shadow
    private WorldClient clientWorldController;
    @Shadow
    private boolean doneLoadingTerrain;
    @Shadow
    @Final
    private NetworkManager netManager;

    @Inject(method = { "handleExplosion" }, at = { @At("HEAD") }, cancellable = true)
    private void handleExplosion(final S27PacketExplosion packetIn, final CallbackInfo ci) {
        if (Kore.velocity.isToggled()) {
            PacketThreadUtil.checkThreadAndEnqueue((Packet)packetIn, (INetHandler)Kore.mc.getNetHandler(), (IThreadListener)this.gameController);
            final Explosion explosion = new Explosion((World)this.gameController.theWorld, (Entity)null, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getStrength(), packetIn.getAffectedBlockPositions());
            explosion.doExplosionB(true);
            final boolean shouldTakeKB = Kore.velocity.skyblockKB.isEnabled() && (Minecraft.getMinecraft().thePlayer.isInLava() || SkyblockUtils.getDisplayName(Minecraft.getMinecraft().thePlayer.getHeldItem()).contains("Bonzo's Staff") || SkyblockUtils.getDisplayName(Minecraft.getMinecraft().thePlayer.getHeldItem()).contains("Jerry-chine Gun"));
            if ((shouldTakeKB || Kore.velocity.hModifier.getValue() != 0.0 || Kore.velocity.vModifier.getValue() != 0.0)) {
                final EntityPlayerSP thePlayer = this.gameController.thePlayer;
                thePlayer.motionX += packetIn.func_149149_c() * (shouldTakeKB ? 1.0 : Kore.velocity.hModifier.getValue());
                final EntityPlayerSP thePlayer2 = this.gameController.thePlayer;
                thePlayer2.motionY += packetIn.func_149144_d() * (shouldTakeKB ? 1.0 : Kore.velocity.vModifier.getValue());
                final EntityPlayerSP thePlayer3 = this.gameController.thePlayer;
                thePlayer3.motionZ += packetIn.func_149147_e() * (shouldTakeKB ? 1.0 : Kore.velocity.hModifier.getValue());
            }
            ci.cancel();
        }
    }

    @Inject(method = { "handleEntityVelocity" }, at = { @At("HEAD") }, cancellable = true)
    public void handleEntityVelocity(final S12PacketEntityVelocity packetIn, final CallbackInfo ci) {
        if (Kore.velocity.isToggled()) {
            PacketThreadUtil.checkThreadAndEnqueue((Packet)packetIn, (INetHandler)Kore.mc.getNetHandler(), (IThreadListener)this.gameController);
            final Entity entity = this.clientWorldController.getEntityByID(packetIn.getEntityID());
            if (entity != null) {
                if (entity.equals((Object)Kore.mc.thePlayer)) {
                    final boolean shouldTakeKB = Kore.velocity.skyblockKB.isEnabled() && (Minecraft.getMinecraft().thePlayer.isInLava() || SkyblockUtils.getDisplayName(Minecraft.getMinecraft().thePlayer.getHeldItem()).contains("Bonzo's Staff") || SkyblockUtils.getDisplayName(Minecraft.getMinecraft().thePlayer.getHeldItem()).contains("Jerry-chine Gun"));
                    if ((shouldTakeKB || Kore.velocity.hModifier.getValue() != 0.0 || Kore.velocity.vModifier.getValue() != 0.0)) {
                        entity.setVelocity(packetIn.getMotionX() * (shouldTakeKB ? 1.0 : Kore.velocity.hModifier.getValue()) / 8000.0, packetIn.getMotionY() * (shouldTakeKB ? 1.0 : Kore.velocity.vModifier.getValue()) / 8000.0, packetIn.getMotionZ() * (shouldTakeKB ? 1.0 : Kore.velocity.hModifier.getValue()) / 8000.0);
                    }
                }
                else {
                    entity.setVelocity(packetIn.getMotionX() / 8000.0, packetIn.getMotionY() / 8000.0, packetIn.getMotionZ() / 8000.0);
                }
            }
            ci.cancel();
        }
    }
}
