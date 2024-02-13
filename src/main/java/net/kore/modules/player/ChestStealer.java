package net.kore.modules.player;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.MilliTimer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraft.inventory.Container;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemEnderPearl;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraftforge.client.event.GuiScreenEvent;

public class ChestStealer extends Module
{
    private MilliTimer timer;
    public NumberSetting delay;
    public BooleanSetting close;
    public BooleanSetting nameCheck;
    public BooleanSetting stealTrash;

    public ChestStealer() {
        super("Chest Stealer", 0, Category.PLAYER);
        this.timer = new MilliTimer();
        this.delay = new NumberSetting("Delay", 100.0, 30.0, 200.0, 1.0);
        this.close = new BooleanSetting("Auto close", true);
        this.nameCheck = new BooleanSetting("Name check", true);
        this.stealTrash = new BooleanSetting("Steal trash", false);
        this.addSettings(this.delay, this.nameCheck, this.stealTrash, this.close);
    }

    @Override
    public void assign()
    {
        Kore.chestStealer = this;
    }

    @SubscribeEvent
    public void onGui(final GuiScreenEvent.BackgroundDrawnEvent event) {
        if (event.gui instanceof GuiChest && this.isToggled()) {
            final Container container = ((GuiChest)event.gui).inventorySlots;
            if (container instanceof ContainerChest && (!this.nameCheck.isEnabled() || ChatFormatting.stripFormatting(((ContainerChest)container).getLowerChestInventory().getDisplayName().getFormattedText()).equals("Chest") || ChatFormatting.stripFormatting(((ContainerChest)container).getLowerChestInventory().getDisplayName().getFormattedText()).equals("LOW"))) {
                for (int i = 0; i < ((ContainerChest)container).getLowerChestInventory().getSizeInventory(); ++i) {
                    if (container.getSlot(i).getHasStack() && this.timer.hasTimePassed((long)this.delay.getValue())) {
                        final Item item = container.getSlot(i).getStack().getItem();
                        if (this.stealTrash.isEnabled() || item instanceof ItemEnderPearl || item instanceof ItemTool || item instanceof ItemArmor || item instanceof ItemBow || item instanceof ItemPotion || item == Items.arrow || item instanceof ItemAppleGold || item instanceof ItemSword || item instanceof ItemBlock) {
                            Kore.mc.playerController.windowClick(container.windowId, i, 0, 1, (EntityPlayer)Kore.mc.thePlayer);
                            this.timer.reset();
                            return;
                        }
                    }
                }
                for (int i = 0; i < ((ContainerChest)container).getLowerChestInventory().getSizeInventory(); ++i) {
                    if (container.getSlot(i).getHasStack()) {
                        final Item item = container.getSlot(i).getStack().getItem();
                        if (this.stealTrash.isEnabled() || item instanceof ItemEnderPearl || item instanceof ItemTool || item instanceof ItemArmor || item instanceof ItemBow || item instanceof ItemPotion || item == Items.arrow || item instanceof ItemAppleGold || item instanceof ItemSword || item instanceof ItemBlock) {
                            return;
                        }
                    }
                }
                if (this.close.isEnabled()) {
                    Kore.mc.thePlayer.closeScreen();
                }
            }
        }
    }
}