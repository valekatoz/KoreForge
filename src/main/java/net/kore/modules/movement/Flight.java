package net.kore.modules.movement;

import net.kore.Kore;
import net.kore.events.MotionUpdateEvent;
import net.kore.events.MoveEvent;
import net.kore.events.MoveHeadingEvent;
import net.kore.events.PacketReceivedEvent;
import net.kore.mixins.MinecraftAccessor;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.entity.player.PlayerCapabilities;

public class Flight extends Module
{
    public static ModeSetting mode;
    public static NumberSetting speed;
    public static NumberSetting time;
    public static NumberSetting timerSpeed;
    public static NumberSetting autoDisable;
    public static NumberSetting test;
    public static BooleanSetting autoDisableHypixel;
    public static BooleanSetting timerBoost;
    public static BooleanSetting autoDisabler;
    public MilliTimer disablerTimer;
    public MilliTimer autoDisableTimer;
    private boolean isFlying;
    private boolean placed;
    private double distance;
    private int flyingTicks;
    private int stage;
    private int ticks;

    public Flight() {
        super("Flight", 0, Category.MOVEMENT);
        this.disablerTimer = new MilliTimer();
        this.autoDisableTimer = new MilliTimer();
        this.addSettings(Flight.speed, Flight.mode, Flight.timerSpeed, Flight.time, Flight.test, Flight.autoDisableHypixel, Flight.autoDisabler, Flight.autoDisable);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.fly = this;
    }

    @Override
    public void onDisable() {
        if (Flight.mode.is("Hypixel")) {
            if (this.distance > 4.0) {
                Kore.notificationManager.showNotification(String.format("Distance flown: %.1f", this.distance), 4000, Notification.NotificationType.INFO);
            }
            if (Kore.mc.thePlayer != null) {
                Kore.mc.thePlayer.motionX = 0.0;
                Kore.mc.thePlayer.motionZ = 0.0;
            }
        }
        else if (Kore.mc.thePlayer != null) {
            Kore.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
        }
        this.isFlying = false;
        ((MinecraftAccessor)Kore.mc).getTimer().timerSpeed = 1.0f;
    }

    @Override
    public void onEnable() {
        final boolean b = false;
        this.placed = b;
        this.isFlying = b;
        final int flyingTicks = 0;
        this.ticks = flyingTicks;
        this.stage = flyingTicks;
        this.flyingTicks = flyingTicks;
        this.distance = flyingTicks;
        this.autoDisableTimer.reset();
        if (Flight.mode.is("Hypixel") && Kore.mc.thePlayer != null) {
            if (!Kore.mc.thePlayer.onGround) {
                this.setToggled(false);
            }
            else {
                Kore.mc.thePlayer.jump();
                Kore.mc.thePlayer.motionY = 0.41999998688697815;
            }
        }
    }

    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (this.isToggled()) {
            final String selected = Flight.mode.getSelected();
            int n = -1;
            switch (selected.hashCode()) {
                case -1248403467: {
                    if (selected.equals("Hypixel")) {
                        n = 0;
                        break;
                    }
                    break;
                }
                case 376026813: {
                    if (selected.equals("Hypixel Slime")) {
                        n = 1;
                        break;
                    }
                    break;
                }
                case 1897755483: {
                    if (selected.equals("Vanilla")) {
                        n = 2;
                        break;
                    }
                    break;
                }
            }
            Label_0403: {
                switch (n) {
                    case 0: {
                        if (this.isFlying) {
                            event.setY(0.0);
                            Kore.mc.thePlayer.motionY = 0.0;
                        }
                        if (this.flyingTicks > 2) {
                            event.setMotion(event.getX() * Flight.test.getValue(), event.getY(), event.getZ() * Flight.test.getValue());
                            break;
                        }
                        event.setX(0.0).setZ(0.0);
                        break;
                    }
                    case 1: {
                        if (Kore.mc.thePlayer.capabilities.allowFlying) {
                            if (Kore.mc.thePlayer.ticksExisted % 6 == 0 || !this.isFlying || this.disablerTimer.hasTimePassed((long)Flight.time.getValue() - 150L)) {
                                final PlayerCapabilities capabilities = new PlayerCapabilities();
                                capabilities.allowFlying = true;
                                capabilities.isFlying = false;
                                Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C13PacketPlayerAbilities(capabilities));
                                capabilities.isFlying = true;
                                Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C13PacketPlayerAbilities(capabilities));
                                this.isFlying = true;
                                this.disablerTimer.reset();
                            }
                            break Label_0403;
                        }
                        else {
                            if (!this.disablerTimer.hasTimePassed((long)Flight.time.getValue())) {
                                break Label_0403;
                            }
                            if (this.isFlying) {
                                Kore.mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
                                this.isFlying = false;
                                ((MinecraftAccessor)Kore.mc).getTimer().timerSpeed = 1.0f;
                                break;
                            }
                            break;
                        }
                    }
                    case 2: {
                        if (Flight.mode.is("Vanilla") && this.autoDisabler.isEnabled() && this.autoDisableTimer.hasTimePassed((long)Flight.autoDisable.getValue()) && Flight.autoDisable.getValue() != 0.0) {
                            this.setToggled(false);
                            return;
                        }
                        TimerUtils.setSpeed((float)Flight.timerSpeed.getValue());
                        event.setY(0.0);
                        MovementUtils.setMotion(event, Flight.speed.getValue());
                        if (Kore.mc.gameSettings.keyBindJump.isKeyDown()) {
                            event.setY(Flight.speed.getValue());
                        }
                        if (Kore.mc.gameSettings.keyBindSneak.isKeyDown()) {
                            event.setY(Flight.speed.getValue() * -1.0);
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMoveHeading(final MoveHeadingEvent e) {
        if (this.isToggled() && Flight.mode.is("Hypixel") && this.isFlying) {
            e.setOnGround(true);
        }
    }

    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    public void onPacket(final PacketReceivedEvent event) {
        if (this.isToggled() && Flight.mode.is("Hypixel") && event.packet instanceof S08PacketPlayerPosLook) {
            if (!this.isFlying || this.flyingTicks < 5) {
                this.isFlying = true;
            }
            else if (Flight.autoDisableHypixel.isEnabled()) {
                this.setToggled(false);
            }
        }
    }

    @SubscribeEvent
    public void onUpdate(final MotionUpdateEvent.Pre event) {
        if (this.isToggled()) {
            final String selected = Flight.mode.getSelected();
            switch (selected) {
                case "Hypixel": {
                    if (!this.placed) {
                        event.setPitch(90.0f);
                        if (!PlayerUtils.isOnGround(1.0)) {
                            this.placeBlock();
                            if (!this.placed) {
                                this.setToggled(false);
                            }
                            event.setOnGround(true);
                            break;
                        }
                        break;
                    }
                    else {
                        double timer = Flight.timerSpeed.getValue();
                        if (!Kore.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                            timer = 0.699999988079071;
                        }
                        TimerUtils.setSpeed((float)timer);
                        if (this.isFlying) {
                            ++this.flyingTicks;
                            this.distance += Math.hypot(Kore.mc.thePlayer.posX - Kore.mc.thePlayer.prevPosX, Kore.mc.thePlayer.posZ - Kore.mc.thePlayer.prevPosZ);
                            break;
                        }
                        ++this.stage;
                        event.setOnGround(false);
                        if (this.stage > 1 && this.stage < 5) {
                            event.setY(event.y - 0.2);
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void placeBlock() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Kore.mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).block.isFullBlock()) {
                slot = i;
                break;
            }
        }
        if (slot != -1) {
            final int prev = Kore.mc.thePlayer.inventory.currentItem;
            PlayerUtils.swapToSlot(slot);
            final Vec3 vec3 = Kore.mc.thePlayer.getPositionEyes(1.0f);
            final Vec3 vec4 = PlayerUtils.getVectorForRotation(0.0f, 90.0f);
            final Vec3 vec5 = vec3.addVector(vec4.xCoord * Kore.mc.playerController.getBlockReachDistance(), vec4.yCoord * Kore.mc.playerController.getBlockReachDistance(), vec4.zCoord * Kore.mc.playerController.getBlockReachDistance());
            final MovingObjectPosition rayrace = Kore.mc.theWorld.rayTraceBlocks(vec3, vec5, false, true, true);
            if (rayrace != null) {
                final Vec3 hitVec = rayrace.hitVec;
                final BlockPos hitPos = rayrace.getBlockPos();
                final float f = (float)(hitVec.xCoord - hitPos.getX());
                final float f2 = (float)(hitVec.yCoord - hitPos.getY());
                final float f3 = (float)(hitVec.zCoord - hitPos.getZ());
                Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C08PacketPlayerBlockPlacement(rayrace.getBlockPos(), rayrace.sideHit.getIndex(), Kore.mc.thePlayer.getHeldItem(), f, f2, f3));
                Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C0APacketAnimation());
                Kore.mc.thePlayer.getHeldItem().onItemUse((EntityPlayer)Kore.mc.thePlayer, (World)Kore.mc.theWorld, hitPos, rayrace.sideHit, f, f2, f3);
                this.placed = true;
            }
            PlayerUtils.swapToSlot(prev);
        }
        else {
            Kore.notificationManager.showNotification("No blocks found", 2000, Notification.NotificationType.INFO);
        }
    }

    public boolean isFlying() {
        return this.isToggled() && (!Flight.mode.is("Hypixel Slime") || !this.disablerTimer.hasTimePassed((long)Flight.time.getValue()));
    }

    static {
        Flight.autoDisabler = new BooleanSetting("Auto Disable", false);
        Flight.mode = new ModeSetting("Mode", "Vanilla", new String[] { "Hypixel Slime", "Vanilla", "Hypixel" });
        Flight.speed = new NumberSetting("Speed", 1.0, 0.1, 5.0, 0.1, aBoolean -> Flight.mode.is("Hypixel"));
        Flight.time = new NumberSetting("Disabler timer", 1200.0, 250.0, 2500.0, 1.0) {
            @Override
            public boolean isHidden() {
                return !Flight.mode.is("Hypixel Slime");
            }
        };
        Flight.timerSpeed = new NumberSetting("Timer Speed", 1.0, 0.1, 3.0, 0.1);
        Flight.autoDisable = new NumberSetting("Auto disable", 1500.0, 0.0, 5000.0, 50.0) {
            @Override
            public boolean isHidden() {
                return !Flight.mode.is("Vanilla");
            }
        };
        Flight.test = new NumberSetting("Test", 1.0, 0.1, 10.0, 0.1, a -> true);
        Flight.autoDisableHypixel = new BooleanSetting("Disable on flag", true, aBoolean -> !Flight.mode.is("Hypixel"));
        Flight.timerBoost = new BooleanSetting("Timer boost", true, aBoolean -> !Flight.mode.is("Hypixel"));
    }
}
