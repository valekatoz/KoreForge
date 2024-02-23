package net.kore.modules.combat;

import java.util.ArrayList;

import net.kore.Kore;
import net.kore.events.MotionUpdateEvent;
import net.kore.events.MoveFlyingEvent;
import net.kore.events.PacketReceivedEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.ui.notifications.Notification;
import net.kore.utils.*;
import net.kore.utils.rotation.Rotation;
import net.kore.utils.rotation.RotationUtils;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.BlockPos;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.monster.EntityMob;

import java.util.stream.Collectors;
import java.util.Comparator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraft.item.ItemSpade;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Random;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.item.EntityArmorStand;
import org.lwjgl.input.Mouse;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;

public class KillAura extends Module
{
    public static EntityLivingBase target;
    public BooleanSetting namesOnly;
    public BooleanSetting middleClick;
    public BooleanSetting players;
    public BooleanSetting mobs;
    public BooleanSetting walls;
    public BooleanSetting teams;
    public BooleanSetting toggleOnLoad;
    public BooleanSetting toggleInGui;
    public BooleanSetting onlySword;
    public BooleanSetting movementFix;
    public BooleanSetting rotationSwing;
    public BooleanSetting shovelSwap;
    public BooleanSetting attackOnly;
    public BooleanSetting invisibles;
    public BooleanSetting syncDisplay;
    public ModeSetting sorting;
    public ModeSetting rotationMode;
    public ModeSetting blockMode;
    public ModeSetting namesonlyMode;
    public ModeSetting mode;
    public NumberSetting range;
    public NumberSetting rotationRange;
    public NumberSetting fov;
    public NumberSetting maxRotation;
    public NumberSetting minRotation;
    public NumberSetting maxCps;
    public NumberSetting minCps;
    public NumberSetting smoothing;
    public NumberSetting switchDelay;
    public static List<String> names;
    private boolean wasDown;
    private boolean isBlocking;
    private int nextCps;
    private int lastSlot;
    private int targetIndex;
    private int attacks;
    private MilliTimer lastAttack;
    private MilliTimer switchDelayTimer;
    private MilliTimer blockDelay;
    public static final MilliTimer DISABLE;

    public KillAura() {
        super("Kill Aura", 0, Category.COMBAT);
        this.syncDisplay = new BooleanSetting("Sync with Target Display", true);
        this.namesOnly = new BooleanSetting("Names Only", false);
        this.middleClick = new BooleanSetting("Middle Click to Add", false);
        this.players = new BooleanSetting("Players", false);
        this.mobs = new BooleanSetting("Mobs", true);
        this.walls = new BooleanSetting("Through walls", true);
        this.teams = new BooleanSetting("Teams", true);
        this.toggleOnLoad = new BooleanSetting("Disable on join", true);
        this.toggleInGui = new BooleanSetting("No containers", true);
        this.onlySword = new BooleanSetting("Only swords", false);
        this.movementFix = new BooleanSetting("Movement Fix", false);
        this.rotationSwing = new BooleanSetting("Swing on rotation", false);
        this.shovelSwap = new BooleanSetting("Shovel swap", false);
        this.attackOnly = new BooleanSetting("Attack Held", false);
        this.invisibles = new BooleanSetting("Invisibles", false);
        this.sorting = new ModeSetting("Sorting", "Distance", new String[] { "Distance", "Health", "Hurt", "Hp reverse" });
        this.rotationMode = new ModeSetting("Rotation Mode", "Simple", new String[] { "Simple", "Smooth", "None" });
        this.blockMode = new ModeSetting("Autoblock", "None", new String[] { "Vanilla", "Hypixel", "Fake", "None" });
        this.namesonlyMode = new ModeSetting("Names Mode", "Friends", new String[] { "Friends", "Enemies" });
        this.mode = new ModeSetting("Mode", "Single", new String[] { "Single", "Switch" });
        this.range = new NumberSetting("Range", 4.2, 2.0, 6.0, 0.1) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (this.getValue() > KillAura.this.rotationRange.getValue()) {
                    this.setValue(KillAura.this.rotationRange.getValue());
                }
            }
        };
        this.rotationRange = new NumberSetting("Rotation Range", 6.0, 2.0, 12.0, 0.1) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (this.getValue() < KillAura.this.range.getValue()) {
                    this.setValue(KillAura.this.range.getValue());
                }
            }
        };
        this.fov = new NumberSetting("Fov", 360.0, 30.0, 360.0, 1.0);
        this.maxRotation = new NumberSetting("Max rotation", 100.0, 10.0, 180.0, 0.1) {
            @Override
            public boolean isHidden() {
                return !KillAura.this.rotationMode.is("Simple");
            }

            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (KillAura.this.minRotation.getValue() > this.getValue()) {
                    this.setValue(KillAura.this.minRotation.getValue());
                }
            }
        };
        this.minRotation = new NumberSetting("Min rotation", 60.0, 5.0, 180.0, 0.1) {
            @Override
            public boolean isHidden() {
                return !KillAura.this.rotationMode.is("Simple");
            }

            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (this.getValue() > KillAura.this.maxRotation.getValue()) {
                    this.setValue(KillAura.this.maxRotation.getValue());
                }
            }
        };
        this.maxCps = new NumberSetting("Max CPS", 13.0, 1.0, 20.0, 1.0) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (KillAura.this.minCps.getValue() > this.getValue()) {
                    this.setValue(KillAura.this.minCps.getValue());
                }
            }
        };
        this.minCps = new NumberSetting("Min CPS", 11.0, 1.0, 20.0, 1.0) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                if (KillAura.this.maxCps.getValue() < this.getValue()) {
                    this.setValue(KillAura.this.maxCps.getValue());
                }
            }
        };
        this.smoothing = new NumberSetting("Smoothing", 12.0, 1.0, 20.0, 0.1) {
            @Override
            public boolean isHidden() {
                return !KillAura.this.rotationMode.is("Smooth");
            }
        };
        this.switchDelay = new NumberSetting("Switch delay", 100.0, 0.0, 250.0, 1.0, aBoolean -> !this.mode.is("Switch"));
        this.nextCps = 10;
        this.lastSlot = -1;
        this.lastAttack = new MilliTimer();
        this.switchDelayTimer = new MilliTimer();
        this.blockDelay = new MilliTimer();

        this.addSettings("Client", this.syncDisplay);
        this.addSettings("Rotations", this.rotationMode, this.rotationRange, this.smoothing, this.maxRotation, this.minRotation, this.fov);
        this.addSettings("Targets", this.players, this.mobs, this.invisibles, this.teams, this.namesonlyMode, this.namesOnly, this.middleClick);
        this.addSettings("Settings", this.mode, this.switchDelay, this.range, this.minCps, this.maxCps, this.sorting, this.blockMode, this.movementFix, this.attackOnly, this.rotationSwing, this.walls, this.toggleInGui, this.toggleOnLoad, this.onlySword, this.shovelSwap);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.killAura = this;
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (Kore.mc.thePlayer == null || Kore.mc.theWorld == null || !this.middleClick.isEnabled()) {
            return;
        }
        if (Mouse.isButtonDown(2) && Kore.mc.currentScreen == null) {
            if (Kore.mc.pointedEntity != null && !this.wasDown && !(Kore.mc.pointedEntity instanceof EntityArmorStand) && Kore.mc.pointedEntity instanceof EntityLivingBase) {
                final String name = ChatFormatting.stripFormatting(Kore.mc.pointedEntity.getName());
                if (!KillAura.names.contains(name)) {
                    KillAura.names.add(name);
                    Kore.notificationManager.showNotification("Added " + ChatFormatting.AQUA + name + ChatFormatting.RESET + " to name sorting", 2000, Notification.NotificationType.INFO);
                }
                else {
                    KillAura.names.remove(name);
                    Kore.notificationManager.showNotification("Removed " + ChatFormatting.AQUA + name + ChatFormatting.RESET + " from name sorting", 2000, Notification.NotificationType.INFO);
                }
            }
            this.wasDown = true;
        }
        else {
            this.wasDown = false;
        }
    }

    @Override
    public void onEnable() {
        if(syncDisplay.isEnabled()) {
            Kore.targetDisplay.setToggled(true);
        }
        this.attacks = 0;
    }

    @Override
    public void onDisable() {
        if(syncDisplay.isEnabled()) {
            Kore.targetDisplay.setToggled(false);
        }
        KillAura.target = null;
        this.isBlocking = false;
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onMovePre(final MotionUpdateEvent.Pre event) {
        KillAura.target = this.getTarget();
        if (!this.isToggled() || (this.attackOnly.isEnabled() && !Kore.mc.gameSettings.keyBindAttack.isKeyDown())) {
            return;
        }
        if (KillAura.target != null) {
            final Rotation angles = RotationUtils.getRotations(KillAura.target, 0.2f);
            final String selected = this.rotationMode.getSelected();
            switch (selected) {
                case "Smooth": {
                    event.setRotation(RotationUtils.getSmoothRotation(RotationUtils.getLastReportedRotation(), angles, (float)this.smoothing.getValue()));
                    break;
                }
                case "Simple": {
                    event.setRotation(RotationUtils.getLimitedRotation(RotationUtils.getLastReportedRotation(), angles, (float)(this.minRotation.getValue() + Math.abs(this.maxRotation.getValue() - this.minRotation.getValue()) * new Random().nextFloat())));
                    break;
                }
            }
            event.setPitch(MathUtils.clamp(event.pitch, 90.0f, -90.0f));
            final String selected2 = this.blockMode.getSelected();
            switch (selected2) {
            }
            if (this.shovelSwap.isEnabled() && KillAura.target instanceof EntityPlayer && this.hasDiamondArmor((EntityPlayer)KillAura.target)) {
                this.lastSlot = Kore.mc.thePlayer.inventory.currentItem;
                for (int i = 0; i < 9; ++i) {
                    if (Kore.mc.thePlayer.inventory.getStackInSlot(i) != null && Kore.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemSpade) {
                        PlayerUtils.swapToSlot(i);
                        this.isBlocking = false;
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onMoveFlying(final MoveFlyingEvent event) {
        if (this.isToggled() && KillAura.target != null && this.movementFix.isEnabled()) {
            event.setYaw(RotationUtils.getRotations(KillAura.target).getYaw());
        }
    }

    private boolean hasDiamondArmor(final EntityPlayer player) {
        for (int i = 1; i < 5; ++i) {
            if (player.getEquipmentInSlot(i) != null && player.getEquipmentInSlot(i).getItem() instanceof ItemArmor && ((ItemArmor)player.getEquipmentInSlot(i).getItem()).getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMovePost(final MotionUpdateEvent.Post event) {
        if(!this.isToggled()) {
            return;
        }
        if (this.attackOnly.isEnabled() && !Kore.mc.gameSettings.keyBindAttack.isKeyDown()) {
            this.attacks = 0;
            return;
        }
        if (KillAura.target != null && Kore.mc.thePlayer.getDistanceToEntity((Entity)KillAura.target) < Math.max(this.rotationRange.getValue(), this.range.getValue()) && this.attacks > 0) {
            final String selected = this.blockMode.getSelected();
            switch (selected) {
                case "None":
                case "Fake": {}
                case "Vanilla": {
                    this.stopBlocking();
                    break;
                }
            }
            while (this.attacks > 0) {
                Kore.mc.thePlayer.swingItem();
                if (Kore.mc.thePlayer.getDistanceToEntity((Entity)KillAura.target) < this.range.getValue() && (RotationUtils.getRotationDifference(RotationUtils.getRotations(KillAura.target), RotationUtils.getLastReportedRotation()) < 15.0 || this.rotationMode.is("None"))) {
                    Kore.mc.playerController.attackEntity((EntityPlayer)Kore.mc.thePlayer, (Entity)KillAura.target);
                    if (this.switchDelayTimer.hasTimePassed((long)this.switchDelay.getValue())) {
                        ++this.targetIndex;
                        this.switchDelayTimer.reset();
                    }
                }
                --this.attacks;
            }
            if (Kore.mc.thePlayer.getHeldItem() != null && Kore.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                final String selected2 = this.blockMode.getSelected();
                switch (selected2) {
                    case "Vanilla": {
                        if (!this.isBlocking) {
                            this.startBlocking();
                            break;
                        }
                        break;
                    }
                    case "Hypixel": {
                        if (this.blockDelay.hasTimePassed(250L)) {
                            this.startBlocking();
                            Kore.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)Kore.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                            Kore.mc.thePlayer.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)Kore.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                            this.blockDelay.reset();
                            break;
                        }
                        break;
                    }
                }
            }
        }
        else {
            this.attacks = 0;
        }
        if (this.shovelSwap.isEnabled() && this.lastSlot != -1) {
            PlayerUtils.swapToSlot(this.lastSlot);
            this.lastSlot = -1;
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onPacket(final PacketReceivedEvent event) {
        if (event.packet instanceof S08PacketPlayerPosLook) {
            KillAura.DISABLE.reset();
        }
    }

    @SubscribeEvent
    public void onRender(final RenderWorldLastEvent event) {
        if (this.isToggled() && KillAura.target != null && this.lastAttack.hasTimePassed(1000 / this.nextCps) && Kore.mc.thePlayer.getDistanceToEntity((Entity)KillAura.target) < (this.rotationSwing.isEnabled() ? this.getRotationRange() : this.range.getValue())) {
            this.nextCps = (int)(this.minCps.getValue() + Math.abs(this.maxCps.getValue() - this.minCps.getValue()) * new Random().nextFloat());
            this.lastAttack.reset();
            ++this.attacks;
        }
    }

    private EntityLivingBase getTarget() {
        if ((Kore.mc.currentScreen instanceof GuiContainer && this.toggleInGui.isEnabled()) || Kore.mc.theWorld == null) {
            return null;
        }
        final List<Entity> validTargets = (List<Entity>)Kore.mc.theWorld.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityLivingBase).filter(entity -> this.isValid((EntityLivingBase) entity)).sorted(Comparator.comparingDouble(e -> e.getDistanceToEntity((Entity)Kore.mc.thePlayer))).collect(Collectors.toList());
        final String selected = this.sorting.getSelected();
        switch (selected) {
            case "Health": {
                validTargets.sort(Comparator.comparingDouble(e -> ((EntityLivingBase)e).getHealth()));
                break;
            }
            case "Hurt": {
                validTargets.sort(Comparator.comparing(e -> ((EntityLivingBase)e).hurtTime));
                break;
            }
            case "Hp reverse": {
                validTargets.sort(Comparator.comparingDouble(e -> ((EntityLivingBase)e).getHealth()).reversed());
                break;
            }
        }
        if (!validTargets.isEmpty()) {
            if (this.targetIndex >= validTargets.size()) {
                this.targetIndex = 0;
            }
            final String selected2 = this.mode.getSelected();
            switch (selected2) {
                case "Switch": {
                    return (EntityLivingBase)validTargets.get(this.targetIndex);
                }
                case "Single": {
                    return (EntityLivingBase)validTargets.get(0);
                }
            }
        }
        return null;
    }

    private boolean isValid(final EntityLivingBase entity) {
        if (entity == Kore.mc.thePlayer || !AntiBot.isValidEntity((Entity)entity) || (!this.invisibles.isEnabled() && entity.isInvisible()) || entity instanceof EntityArmorStand || (!Kore.mc.thePlayer.canEntityBeSeen((Entity)entity) && !this.walls.isEnabled()) || entity.getHealth() <= 0.0f || entity.getDistanceToEntity((Entity)Kore.mc.thePlayer) > ((KillAura.target != null && KillAura.target != entity) ? this.range.getValue() : Math.max(this.rotationRange.getValue(), this.range.getValue())) || RotationUtils.getRotationDifference(RotationUtils.getRotations(entity), RotationUtils.getPlayerRotation()) > this.fov.getValue()) {
            return false;
        }
        if (this.namesOnly.isEnabled()) {
            final boolean flag = KillAura.names.contains(ChatFormatting.stripFormatting(entity.getName()));
            if (this.namesonlyMode.is("Enemies") || flag) {
                return this.namesonlyMode.is("Enemies") && flag;
            }
        }
        return ((!(entity instanceof EntityMob) && !(entity instanceof EntityAmbientCreature) && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityAnimal) && !(entity instanceof EntitySlime)) || this.mobs.isEnabled()) && (!(entity instanceof EntityPlayer) || ((!EntityUtils.isTeam(entity) || !this.teams.isEnabled()) && this.players.isEnabled())) && !(entity instanceof EntityVillager);
    }

    private double getRotationRange() {
        return Math.max(this.rotationRange.getValue(), this.range.getValue());
    }

    private void startBlocking() {
        PacketUtils.sendPacketNoEvent((Packet<?>)new C08PacketPlayerBlockPlacement(Kore.mc.thePlayer.getHeldItem()));
        this.isBlocking = true;
    }

    private void stopBlocking() {
        if (this.isBlocking) {
            Kore.mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.isBlocking = false;
        }
    }

    @SubscribeEvent
    public void onWorldLoad(final WorldEvent.Load event) {
        if (this.isToggled() && this.toggleOnLoad.isEnabled()) {
            this.toggle();
        }
    }

    static {
        KillAura.names = new ArrayList<String>();
        DISABLE = new MilliTimer();
    }
}