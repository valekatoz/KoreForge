package net.kore.modules.player;

import net.kore.Kore;
import net.kore.events.PacketSentEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.MovementUtils;
import net.kore.utils.render.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeCam extends Module
{
    private EntityOtherPlayerMP playerEntity;
    public NumberSetting speed;
    public BooleanSetting tracer;

    public FreeCam() {
        super("FreeCam", Category.PLAYER);
        this.speed = new NumberSetting("Speed", 3.0, 0.1, 5.0, 0.1);
        this.tracer = new BooleanSetting("Show tracer", false);
        this.addSettings(this.speed, this.tracer);
    }

    @Override
    public void assign()
    {
        Kore.freeCam = this;
    }

    @Override
    public void onEnable() {
        if (Kore.mc.theWorld != null) {
            (this.playerEntity = new EntityOtherPlayerMP((World) Kore.mc.theWorld, Kore.mc.thePlayer.getGameProfile())).copyLocationAndAnglesFrom((Entity)Kore.mc.thePlayer);
            this.playerEntity.onGround = Kore.mc.thePlayer.onGround;
            Kore.mc.theWorld.addEntityToWorld(-2137, (Entity)this.playerEntity);
        }
    }

    @Override
    public void onDisable() {
        if (Kore.mc.thePlayer == null || Kore.mc.theWorld == null || this.playerEntity == null) {
            return;
        }
        Kore.mc.thePlayer.noClip = false;
        Kore.mc.thePlayer.setPosition(this.playerEntity.posX, this.playerEntity.posY, this.playerEntity.posZ);
        Kore.mc.theWorld.removeEntityFromWorld(-2137);
        this.playerEntity = null;
        Kore.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
    }

    @SubscribeEvent
    public void onLivingUpdate(final LivingEvent.LivingUpdateEvent event) {
        if (this.isToggled()) {
            Kore.mc.thePlayer.noClip = true;
            Kore.mc.thePlayer.fallDistance = 0.0f;
            Kore.mc.thePlayer.onGround = false;
            Kore.mc.thePlayer.capabilities.isFlying = false;
            Kore.mc.thePlayer.motionY = 0.0;
            if (!MovementUtils.isMoving()) {
                Kore.mc.thePlayer.motionZ = 0.0;
                Kore.mc.thePlayer.motionX = 0.0;
            }
            final double speed = this.speed.getValue() * 0.1;
            Kore.mc.thePlayer.jumpMovementFactor = (float)speed;
            if (Kore.mc.gameSettings.keyBindJump.isKeyDown()) {
                final EntityPlayerSP thePlayer = Kore.mc.thePlayer;
                thePlayer.motionY += speed * 3.0;
            }
            if (Kore.mc.gameSettings.keyBindSneak.isKeyDown()) {
                final EntityPlayerSP thePlayer2 = Kore.mc.thePlayer;
                thePlayer2.motionY -= speed * 3.0;
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(final RenderWorldLastEvent event) {
        if (this.isToggled() && this.playerEntity != null && this.tracer.isEnabled()) {
            RenderUtils.tracerLine((Entity)this.playerEntity, event.partialTicks, 1.0f, Kore.clickGui.getColor());
        }
    }

    @SubscribeEvent
    public void onWorldChange(final WorldEvent.Load event) {
        if (this.isToggled()) {
            this.toggle();
        }
    }

    @SubscribeEvent
    public void onPacket(final PacketSentEvent event) {
        if (this.isToggled() && event.packet instanceof C03PacketPlayer) {
            event.setCanceled(true);
        }
    }
}