package net.kore.modules.misc;

import com.mojang.authlib.properties.Property;
import net.kore.Kore;
import net.kore.events.BlockChangeEvent;
import net.kore.events.JoinGameEvent;
import net.kore.events.PacketReceivedEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.MilliTimer;
import net.kore.utils.SkyblockUtils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.HashMap;

public class GhostBlocks extends Module {
    public NumberSetting range;
    public NumberSetting delay;
    public ModeSetting mode;
    public ModeSetting key;
    public BooleanSetting reset;
    public int activeKey;
    private boolean wasPressed;
    private MilliTimer timer;
    private static ArrayList<BlockPos> ghostBlocks;
    private static HashMap<Long, BlockChangeEvent> eventQueue;
    private boolean hasSent;
    private int ticks;

    public GhostBlocks() {
        super("Ghost Blocks", 0, Category.MISC);
        this.range = new NumberSetting("Range", 10.0, 1.0, 100.0, 1.0);
        this.delay = new NumberSetting("Delay (Seconds)", 3.0, 1.0, 15.0, 1.0);
        this.mode = new ModeSetting("Speed", "Fast", new String[]{"Slow", "Fast"});
        this.key = new ModeSetting("Key", "LCONTROL", new String[]{"LCONTROL", "G"});
        this.reset = new BooleanSetting("Restore ghost blocks when touching one", false);
        this.timer = new MilliTimer();
        this.addSettings(this.mode, this.range, this.delay, this.key, this.reset);
    }

    @Override
    public void assign()
    {
        Kore.ghostBlock = this;
    }

    @SubscribeEvent
    public void onKey(final TickEvent.ClientTickEvent event) {
        if (Kore.mc.currentScreen != null || Kore.mc.theWorld == null || !Kore.ghostBlock.isToggled() || event.phase != TickEvent.Phase.START) return;

        ticks = (ticks + 1) % 20 == 0 ? 0 : ticks;

        switch(key.getSelected()) {
            case "LCONTROL":
                activeKey = Keyboard.KEY_LCONTROL;
                break;
            case "G":
                activeKey = Keyboard.KEY_G;
                break;
        }
        this.hasSent = true;
        GhostBlocks.eventQueue.entrySet().removeIf(entry -> {
            if (entry.getKey() + (delay.getValue()*1000) <= System.currentTimeMillis() && !Keyboard.isKeyDown(activeKey)) {
                Kore.mc.theWorld.setBlockState((entry.getValue()).pos, (entry.getValue()).state);
                GhostBlocks.ghostBlocks.remove((entry.getValue()).pos);
                return true;
            } else {
                return false;
            }
        });
        this.hasSent = false;
        if (Keyboard.isKeyDown(activeKey) && ((this.mode.getSelected().equals("Slow") && !this.wasPressed) || this.mode.getSelected().equals("Fast") && ticks % 2 == 0)) {
            final Vec3 vec3 = Kore.mc.thePlayer.getPositionEyes(0.0f);
            final Vec3 vec4 = Kore.mc.thePlayer.getLook(0.0f);
            final Vec3 vec5 = vec3.addVector(vec4.xCoord * this.range.getValue(), vec4.yCoord * this.range.getValue(), vec4.zCoord * this.range.getValue());
            final BlockPos obj = Kore.mc.theWorld.rayTraceBlocks(vec3, vec5, true, false, true).getBlockPos();
            if (this.isValidBlock(obj)) {
                return;
            }
            eventQueue.put(System.currentTimeMillis(), new BlockChangeEvent(obj, Kore.mc.theWorld.getBlockState(obj)));
            Kore.mc.theWorld.setBlockToAir(obj);
            GhostBlocks.ghostBlocks.add(obj);
        }
        this.wasPressed = Keyboard.isKeyDown(activeKey);
    }

    @SubscribeEvent
    public void onWorldJoin(final JoinGameEvent event) {
        GhostBlocks.eventQueue.clear();
        GhostBlocks.ghostBlocks.clear();
    }

    private boolean isValidBlock(final BlockPos blockPos) {
        final Block block = Kore.mc.theWorld.getBlockState(blockPos).getBlock();
        if (block == Blocks.skull) {
            final TileEntitySkull tileEntity = (TileEntitySkull) Kore.mc.theWorld.getTileEntity(blockPos);
            if (tileEntity.getSkullType() == 3 && tileEntity.getPlayerProfile() != null && tileEntity.getPlayerProfile().getProperties() != null) {
                final Property property = SkyblockUtils.firstOrNull((Iterable<Property>) tileEntity.getPlayerProfile().getProperties().get("textures"));
                return property != null && property.getValue().equals("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRkYjRhZGZhOWJmNDhmZjVkNDE3MDdhZTM0ZWE3OGJkMjM3MTY1OWZjZDhjZDg5MzQ3NDlhZjRjY2U5YiJ9fX0=");
            }
        }
        return block == Blocks.lever || block == Blocks.chest || block == Blocks.trapped_chest || block == Blocks.air;
    }

    static {
        GhostBlocks.ghostBlocks = new ArrayList<BlockPos>();
        GhostBlocks.eventQueue = new HashMap<Long, BlockChangeEvent>();
    }
}