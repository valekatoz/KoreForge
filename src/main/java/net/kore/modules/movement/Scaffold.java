package net.kore.modules.movement;

import net.kore.Kore;
import net.kore.events.MotionUpdateEvent;
import net.kore.events.MoveEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.*;
import net.kore.utils.rotation.Rotation;
import net.kore.utils.rotation.RotationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import java.util.Comparator;
import net.minecraft.util.Vec3;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.BlockPos;
import java.util.Random;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;

public class Scaffold extends Module
{
    public static final NumberSetting distance;
    public static final NumberSetting timer;
    public static final NumberSetting towerTimer;
    public static final NumberSetting maxDelay;
    public static final NumberSetting minDelay;
    public static final NumberSetting test;
    public static final BooleanSetting safeWalk;
    public static final BooleanSetting disableSpeed;
    public static final BooleanSetting disableAura;
    public static final BooleanSetting safe;
    public static final ModeSetting tower;
    public static final ModeSetting sprint;
    private int ticks;
    private MilliTimer timer1;
    private MilliTimer slowdowntimer;
    private int blocksPlaced;
    boolean flag;

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT);
        this.timer1 = new MilliTimer();
        this.slowdowntimer = new MilliTimer();
        this.addSettings(Scaffold.distance, Scaffold.minDelay, Scaffold.maxDelay, Scaffold.timer, Scaffold.towerTimer, Scaffold.tower, Scaffold.sprint, Scaffold.safeWalk, Scaffold.disableSpeed, Scaffold.disableAura, Scaffold.safe);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.scaffold = this;
    }

    @Override
    public void onEnable() {
        if (Kore.mc.thePlayer != null) {
            TimerUtils.setSpeed((float)Scaffold.timer.getValue());
            this.ticks = 0;
        }
    }

    @Override
    public void onDisable() {
        if (Kore.mc.thePlayer != null) {
            TimerUtils.setSpeed(1.0f);
            Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C09PacketHeldItemChange(Kore.mc.thePlayer.inventory.currentItem));
        }
    }

    @SubscribeEvent
    public void onUpdate(final MotionUpdateEvent event) {
        if (this.isToggled()) {
            if (event.isPre()) {
                event.setYaw(MovementUtils.getYaw() + 180.0f).setPitch(81.0f);
                this.flag = true;
                for (int j = 81; j > 72; --j) {
                    final MovingObjectPosition trace = rayTrace(event.yaw, (float)j);
                    if (trace != null) {
                        this.flag = false;
                        event.setPitch((float)(j + MathUtils.getRandomInRange(0.1, -0.1)));
                        break;
                    }
                }
                if (this.flag && !Scaffold.safe.isEnabled()) {
                    final BlockPos pos = this.getClosestBlock();
                    if (pos != null) {
                        final Rotation rotation = RotationUtils.getRotations(RotationUtils.getClosestPointInAABB(Kore.mc.thePlayer.getPositionEyes(1.0f), Kore.mc.theWorld.getBlockState(pos).getBlock().getSelectedBoundingBox((World)Kore.mc.theWorld, pos)));
                        final MovingObjectPosition position = rayTrace(rotation);
                        if (position != null) {
                            event.setRotation(rotation);
                        }
                    }
                }
                if (Kore.mc.gameSettings.keyBindJump.isKeyDown()) {
                    TimerUtils.setSpeed((float)Scaffold.towerTimer.getValue());
                    if (Scaffold.tower.is("Hypixel")) {
                        if (!Kore.mc.thePlayer.isPotionActive(Potion.jump) && PlayerUtils.isOnGround(0.3)) {
                            Kore.mc.thePlayer.motionY = 0.38999998569488525;
                        }
                        Kore.mc.thePlayer.setJumping(false);
                    }
                }
                else {
                    this.timer1.reset();
                    this.slowdowntimer.reset();
                    this.blocksPlaced = 0;
                    TimerUtils.setSpeed((float)Scaffold.timer.getValue());
                }
            }
            else {
                final int selectedSlot = this.getBlock();
                if (selectedSlot == -1) {
                    return;
                }
                Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C09PacketHeldItemChange(selectedSlot));
                if (this.ticks <= 0) {
                    final MovingObjectPosition rayrace = rayTrace(event.getRotation());
                    if (rayrace != null && rayrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && Kore.mc.theWorld.getBlockState(rayrace.getBlockPos()).getBlock().isFullBlock()) {
                        if (Kore.mc.gameSettings.keyBindJump.isKeyDown() && Scaffold.tower.is("Hypixel")) {
                            if (!PlayerUtils.isInsideBlock()) {
                                this.placeBlock();
                            }
                        }
                        else if (Kore.mc.playerController.onPlayerRightClick(Kore.mc.thePlayer, Kore.mc.theWorld, Kore.mc.thePlayer.inventory.getStackInSlot(selectedSlot), rayrace.getBlockPos(), rayrace.sideHit, rayrace.hitVec)) {
                            Kore.mc.thePlayer.swingItem();
                        }
                        ++this.blocksPlaced;
                        if (!this.flag) {
                            this.ticks = (int)(Scaffold.minDelay.getValue() + new Random().nextInt((int)(Scaffold.maxDelay.getValue() - Scaffold.minDelay.getValue() + 1.0)));
                        }
                        else {
                            this.ticks = Math.max(2, (int)(Scaffold.minDelay.getValue() + new Random().nextInt((int)(Scaffold.maxDelay.getValue() - Scaffold.minDelay.getValue() + 1.0))));
                        }
                        if (Kore.mc.thePlayer.inventory.getStackInSlot(selectedSlot) != null && Kore.mc.thePlayer.inventory.getStackInSlot(selectedSlot).stackSize <= 0) {
                            Kore.mc.thePlayer.inventory.removeStackFromSlot(selectedSlot);
                        }
                    }
                }
                --this.ticks;
            }
        }
    }

    private int getBlock() {
        int current = -1;
        int stackSize = 0;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Kore.mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stackSize < stack.stackSize && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).block.isFullBlock()) {
                stackSize = stack.stackSize;
                current = i;
            }
        }
        return current;
    }

    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (this.isToggled() && !Scaffold.sprint.is("Sprint")) {
            final double speed = Scaffold.sprint.is("Semi") ? 0.2575 : 0.225;
            final double x = MathUtils.clamp(event.getX(), speed, -speed);
            final double z = MathUtils.clamp(event.getZ(), speed, -speed);
            event.setX(x).setZ(z);
        }
    }

    private BlockPos getClosestBlock() {
        final ArrayList<Vec3> posList = new ArrayList<Vec3>();
        for (int range = (int)Math.ceil(Scaffold.distance.getValue()), x = -range; x <= range; ++x) {
            for (int y = -range + 2; y < 0; ++y) {
                for (int z = -range; z <= range; ++z) {
                    final Vec3 vec = new Vec3((double)x, (double)y, (double)z).addVector(Kore.mc.thePlayer.posX, Kore.mc.thePlayer.posY, Kore.mc.thePlayer.posZ);
                    final BlockPos pos2 = new BlockPos(vec);
                    if (Kore.mc.theWorld.getBlockState(pos2).getBlock().isFullBlock()) {
                        posList.add(vec);
                    }
                }
            }
        }
        if (posList.isEmpty()) {
            return null;
        }
        posList.sort(Comparator.comparingDouble(pos -> Kore.mc.thePlayer.getDistance(pos.xCoord, pos.yCoord + 1.0, pos.zCoord)));
        return new BlockPos((Vec3)posList.get(0));
    }

    private static MovingObjectPosition rayTrace(final Rotation rotation) {
        return rayTrace(rotation.getYaw(), rotation.getPitch());
    }

    private static MovingObjectPosition rayTrace(final float yaw, final float pitch) {
        final Vec3 vec3 = Kore.mc.thePlayer.getPositionEyes(1.0f);
        final Vec3 vec4 = PlayerUtils.getVectorForRotation(yaw, pitch);
        final Vec3 vec5 = vec3.addVector(vec4.xCoord * Kore.mc.playerController.getBlockReachDistance(), vec4.yCoord * Kore.mc.playerController.getBlockReachDistance(), vec4.zCoord * Kore.mc.playerController.getBlockReachDistance());
        return Kore.mc.theWorld.rayTraceBlocks(vec3, vec5);
    }

    private void placeBlock() {
        final MovingObjectPosition rayrace = rayTrace(0.0f, 90.0f);
        if (rayrace != null) {
            final Vec3 hitVec = rayrace.hitVec;
            final BlockPos hitPos = rayrace.getBlockPos();
            final float f = (float)(hitVec.xCoord - hitPos.getX());
            final float f2 = (float)(hitVec.yCoord - hitPos.getY());
            final float f3 = (float)(hitVec.zCoord - hitPos.getZ());
            Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C08PacketPlayerBlockPlacement(rayrace.getBlockPos(), rayrace.sideHit.getIndex(), Kore.mc.thePlayer.getHeldItem(), f, f2, f3));
            Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C0APacketAnimation());
            Kore.mc.thePlayer.getHeldItem().onItemUse((EntityPlayer)Kore.mc.thePlayer, (World)Kore.mc.theWorld, hitPos, rayrace.sideHit, f, f2, f3);
        }
    }

    static {
        distance = new NumberSetting("Range", 4.5, 1.0, 4.5, 0.1);
        timer = new NumberSetting("Timer", 1.0, 0.1, 3.0, 0.05);
        towerTimer = new NumberSetting("Tower timer", 1.0, 0.1, 3.0, 0.05);
        maxDelay = new NumberSetting("Max delay", 1.0, 0.0, 4.0, 1.0) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (this.getValue() < Scaffold.minDelay.getValue()) {
                    this.setValue(Scaffold.minDelay.getValue());
                }
            }
        };
        minDelay = new NumberSetting("Min delay", 1.0, 0.0, 4.0, 1.0) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (this.getValue() > Scaffold.maxDelay.getValue()) {
                    this.setValue(Scaffold.maxDelay.getValue());
                }
            }
        };
        test = new NumberSetting("Test", 5.15, 5.0, 5.5, 0.05);
        safeWalk = new BooleanSetting("Safe walk", true);
        disableSpeed = new BooleanSetting("Disable speed", true);
        disableAura = new BooleanSetting("Disable aura", true);
        safe = new BooleanSetting("Safe", true);
        tower = new ModeSetting("Tower", "None", new String[] { "None", "Hypixel" });
        sprint = new ModeSetting("Sprint", "Semi", new String[] { "None", "Semi", "Sprint" });
    }

    private static class BlockPlaceData
    {
        public final BlockPos pos;
        public final BlockPos targetPos;

        public BlockPlaceData(final BlockPos pos, final BlockPos targetPos) {
            this.pos = pos;
            this.targetPos = targetPos;
        }
    }
}
