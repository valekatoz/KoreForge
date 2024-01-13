package net.kore.modules.skyblock;

import net.kore.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import net.kore.Kore;
import net.kore.settings.NumberSetting;
import net.kore.utils.GuiUtils;
import net.kore.utils.Multithreading;

public class AutoHarp extends Module {
    public NumberSetting autoHarpDelay;
    // Global
    private boolean inHarp;
    private Slot slot;
    private long timestamp;
    private long startedSongTimestamp;
    private int updates;
    private final ArrayList<ItemStack> currentInventory = new ArrayList<>();
    private long lastContainerUpdate;
    private final Random rand = new Random(System.currentTimeMillis());

    public AutoHarp() {
        super("Auto Harp", Category.SKYBLOCK);
        this.autoHarpDelay = new NumberSetting("Click delay (Milliseconds)", 100, 0, 500, 1);
        this.addSetting(autoHarpDelay);
        setToggled(false);
    }

    @Override
    public void assign()
    {
        Kore.autoHarp = this;
    }

    public int getRandDelay()
    {
        return rand.nextInt(25);
    }

    @SubscribeEvent
    public final void onGuiOpen(GuiOpenEvent event) {
        inHarp = GuiUtils.getInventoryName(event.gui).startsWith("Harp -");
        updates = 0;
        currentInventory.clear();
    }

    @SubscribeEvent
    public void onBackgroundDraw(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!Kore.autoHarp.isToggled() || !inHarp) return;

        if (Kore.mc.thePlayer.openContainer.inventorySlots.size() != currentInventory.size()) {
            for (Slot slot : Kore.mc.thePlayer.openContainer.inventorySlots) {
                currentInventory.add(slot.getStack());
            }
            return;
        }

        boolean updated = false;

        if (System.currentTimeMillis() - lastContainerUpdate > 175) {
            for (int i = 0; i < Kore.mc.thePlayer.openContainer.inventorySlots.size(); i++) {
                ItemStack itemStack1 = Kore.mc.thePlayer.openContainer.inventorySlots.get(i).getStack();
                ItemStack itemStack2 = currentInventory.get(i);
                if (!ItemStack.areItemStacksEqual(itemStack1, itemStack2)) {
                    if (updates < 3) {
                        startedSongTimestamp = System.currentTimeMillis();
                    }

                    lastContainerUpdate = System.currentTimeMillis();
                    currentInventory.set(i, itemStack1);
                    updated = true;
                }
            }
        }

        if (updated) {
            updates++;
            for (int slotNumber = 0; slotNumber < currentInventory.size(); slotNumber++) {
                if (slotNumber > 26 && slotNumber < 36) {
                    ItemStack itemStack = currentInventory.get(slotNumber);
                    if (itemStack != null && itemStack.getItem() instanceof ItemBlock && ((ItemBlock) itemStack.getItem()).getBlock() == Blocks.wool) {
                        int finalSlotNumber = slotNumber;
                        Multithreading.schedule(() -> {
                            slot = Kore.mc.thePlayer.openContainer.inventorySlots.get(finalSlotNumber);
                            timestamp = System.currentTimeMillis();
                            if(Kore.clientSettings.debug.isEnabled()) {
                                Kore.sendMessageWithPrefix("(AutoHarp) Clicked Slot " + slot.slotNumber+9 + " (&c" + (timestamp - startedSongTimestamp) +"&f)");
                            }
                            Kore.mc.playerController.windowClick(Kore.mc.thePlayer.openContainer.windowId,finalSlotNumber + 9,2,3,Kore.mc.thePlayer);
                        }, (long)autoHarpDelay.getValue()+getRandDelay(), TimeUnit.MILLISECONDS);
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!Kore.autoHarp.isToggled() || !inHarp) return;

        GlStateManager.disableDepth();
        Kore.mc.fontRendererObj.drawStringWithShadow("[KORE] ",5,5,new Color(255, 85, 85).getRGB());
        Kore.mc.fontRendererObj.drawStringWithShadow("AutoHarp",42,5,Color.WHITE.getRGB());
        if(Kore.clientSettings.debug.isEnabled()) {
            Kore.mc.fontRendererObj.drawStringWithShadow("Song Speed: " + (System.currentTimeMillis() - startedSongTimestamp) / updates + "ms",5,15,Color.LIGHT_GRAY.getRGB());
            Kore.mc.fontRendererObj.drawStringWithShadow("Gui Updates: " + updates,5,25,Color.LIGHT_GRAY.getRGB());
            Kore.mc.fontRendererObj.drawStringWithShadow("Time Elapsed : " + (System.currentTimeMillis() - startedSongTimestamp),5,35,Color.LIGHT_GRAY.getRGB());
        }
        if (slot != null && System.currentTimeMillis() - timestamp < (autoHarpDelay.getValue()+getRandDelay())) {
            Kore.mc.fontRendererObj.drawStringWithShadow(
                    "Click",
                    (event.gui.width - 176) / 2f + slot.xDisplayPosition + 8 - Kore.mc.fontRendererObj.getStringWidth("Click") / 2f,
                    (event.gui.height - 222) / 2f + slot.yDisplayPosition + 24,
                    Color.RED.getRGB()
            );
        }
        GlStateManager.enableDepth();
    }
}
