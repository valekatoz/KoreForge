package net.kore.modules.skyblock;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.settings.BooleanSetting;
import net.kore.utils.GuiUtils;

public class AutoExperiments extends Module {
    public NumberSetting autoExperimentsDelay;
    public ModeSetting delayRandomizer;
    public BooleanSetting chronomatronSolver;
    public BooleanSetting ultrasequencerSolver;
    // Global
    private int ticks = 0;
    private final Random rand = new Random(System.currentTimeMillis());
    private long lastClickTime = 0L;
    // Chronomatron
    static int lastChronomatronRound = 0;
    static List<String> chronomatronPattern = new ArrayList<>();
    static int chronomatronMouseClicks = 0;
    // Ultrasequencer
    static Slot[] clickInOrderSlots = new Slot[36];
    static int lastUltraSequencerClicked = 0;
    private int until = 0;

    public AutoExperiments()
    {
        super("Auto Experiments", Category.SKYBLOCK);
        this.autoExperimentsDelay = new NumberSetting("Click delay (Ticks)", 15, 0, 30, 1);
        this.delayRandomizer = new ModeSetting("Delay Randomizer", "High", new String[] { "Off", "Low", "Medium", "High" });
        this.chronomatronSolver = new BooleanSetting("Chronomatron",true);
        this.ultrasequencerSolver = new BooleanSetting("Ultrasequencer",true);
        this.addSettings(autoExperimentsDelay, delayRandomizer, chronomatronSolver, ultrasequencerSolver);
        setToggled(false);
    }

    @Override
    public void assign()
    {
        Kore.autoExperiments = this;
    }

    public int getRandDelay()
    {
        String delayRandomizerIs = delayRandomizer.getSelected();

        switch (delayRandomizerIs) {
            case "Off":
                return 0;
            case "Low":
                return rand.nextInt(75);
            case "High":
                return rand.nextInt(300);
            default:
                return rand.nextInt(150);
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        lastClickTime = 0;
        lastChronomatronRound = 0;
        chronomatronPattern.clear();
        chronomatronMouseClicks = 0;
        clickInOrderSlots = new Slot[36];
    }

    @SubscribeEvent
    public void onTick(GuiScreenEvent.BackgroundDrawnEvent event) {
        if(!Kore.autoExperiments.isToggled()) return;

        if (Kore.mc.currentScreen instanceof GuiChest) {
            GuiChest inventory = (GuiChest) event.gui;
            Container containerChest = inventory.inventorySlots;
            if (containerChest instanceof ContainerChest) {
                List<Slot> invSlots = containerChest.inventorySlots;
                String invName = ((ContainerChest) containerChest).getLowerChestInventory().getDisplayName().getUnformattedText().trim();

                if (chronomatronSolver.isEnabled() && invName.startsWith("Chronomatron (")) {
                    EntityPlayerSP player = Kore.mc.thePlayer;
                    if (player.inventory.getItemStack() == null && invSlots.size() > 48 && invSlots.get(49).getStack() != null) {
                        if (invSlots.get(49).getStack().getDisplayName().startsWith(Kore.fancy+"7Timer: "+Kore.fancy+"a") && invSlots.get(4).getStack() != null) { // §7Timer: §a
                            int round = invSlots.get(4).getStack().stackSize;
                            int timerSeconds = Integer.parseInt(StringUtils.stripControlCodes(invSlots.get(49).getStack().getDisplayName()).replaceAll("[^\\d]", ""));
                            if (round != lastChronomatronRound && timerSeconds == round + 2) {
                                lastChronomatronRound = round;
                                for (int i = 10; i <= 43; i++) {
                                    ItemStack stack = invSlots.get(i).getStack();
                                    if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.stained_hardened_clay)) {
                                        chronomatronPattern.add(stack.getDisplayName());
                                        break;
                                    }
                                }
                            }
                            if (player.inventory.getItemStack() == null && chronomatronMouseClicks < chronomatronPattern.size()) {
                                for (int i = 10; i <= 43; i++) {
                                    ItemStack glass = invSlots.get(i).getStack();
                                    if (player.inventory.getItemStack() == null && glass != null && ticks % 5 == 0  && lastClickTime+((autoExperimentsDelay.getValue()*50L)+getRandDelay()) < System.currentTimeMillis()) {
                                        Slot glassSlot = invSlots.get(i);
                                        if (glass.getDisplayName().equals(chronomatronPattern.get(chronomatronMouseClicks))) {
                                            Kore.mc.playerController.windowClick(Kore.mc.thePlayer.openContainer.windowId,glassSlot.slotNumber,2,3, Kore.mc.thePlayer);
                                            if(Kore.clientSettings.debug.isEnabled()) {
                                                Kore.sendMessageWithPrefix("(Chronomatron) Clicked Slot " + glassSlot.slotNumber + " (&c" + glassSlot.getStack().getDisplayName() + "&f)");
                                                if(lastClickTime > 0) {
                                                    Kore.sendMessageWithPrefix("(Chronomatron) Since last click &c"+(System.currentTimeMillis()-lastClickTime)+"ms&f passed");
                                                }
                                            }
                                            lastClickTime = System.currentTimeMillis();
                                            chronomatronMouseClicks++;
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (invSlots.get(49).getStack().getDisplayName().equals(Kore.fancy+"aRemember the pattern!")) { // §aRemember the pattern!
                            chronomatronMouseClicks = 0;
                        }
                    }
                }

                if (ultrasequencerSolver.isEnabled() && invName.startsWith("Ultrasequencer (")) {
                    EntityPlayerSP player = Kore.mc.thePlayer;
                    if (player.inventory.getItemStack() == null && invSlots.size() > 48 && invSlots.get(49).getStack() != null && invSlots.get(49).getStack().getDisplayName().startsWith(Kore.fancy+"7Timer: "+Kore.fancy+"a")) { // §7Timer: §a
                        lastUltraSequencerClicked = 0;
                        for (Slot slot: clickInOrderSlots) {
                            if (slot != null && slot.getStack() != null && StringUtils.stripControlCodes(slot.getStack().getDisplayName()).matches("\\d+")) {
                                int number = Integer.parseInt(StringUtils.stripControlCodes(slot.getStack().getDisplayName()));
                                if (number > lastUltraSequencerClicked) {
                                    lastUltraSequencerClicked = number;
                                }
                            }
                        }
                        if (player.inventory.getItemStack() == null && clickInOrderSlots[lastUltraSequencerClicked] != null && ticks % 2 == 0 && lastUltraSequencerClicked != 0 && until == lastUltraSequencerClicked) {
                            Slot nextSlot = clickInOrderSlots[lastUltraSequencerClicked];
                            if(lastClickTime+((autoExperimentsDelay.getValue()*50L)+getRandDelay()) < System.currentTimeMillis()) {
                                Kore.mc.playerController.windowClick(Kore.mc.thePlayer.openContainer.windowId, nextSlot.slotNumber, 2, 3, Kore.mc.thePlayer);
                                if(Kore.clientSettings.debug.isEnabled()) {
                                    Kore.sendMessageWithPrefix("(Ultrasequencer) Clicked Slot " + nextSlot.slotNumber + " (&c" + (lastUltraSequencerClicked+1) + "&f)");
                                    if(lastClickTime > 0) {
                                        Kore.sendMessageWithPrefix("(Ultrasequencer) Since last click &c"+(System.currentTimeMillis()-lastClickTime)+"ms&f passed");
                                    }
                                }
                                lastClickTime = System.currentTimeMillis();
                                until = lastUltraSequencerClicked + 1;
                                ticks = 0;
                            }
                        }
                        if (player.inventory.getItemStack() == null && clickInOrderSlots[lastUltraSequencerClicked] != null && ticks % 5 == 0 && lastUltraSequencerClicked < 1) {
                            Slot nextSlot = clickInOrderSlots[lastUltraSequencerClicked];
                            if(lastClickTime+((autoExperimentsDelay.getValue()*50L)+getRandDelay()) < System.currentTimeMillis()) {
                                Kore.mc.playerController.windowClick(Kore.mc.thePlayer.openContainer.windowId, nextSlot.slotNumber, 2, 3, Kore.mc.thePlayer);
                                if(Kore.clientSettings.debug.isEnabled()) {
                                    Kore.sendMessageWithPrefix("(Ultrasequencer) Clicked Slot " + nextSlot.slotNumber + " (&c" + (lastUltraSequencerClicked+1) + "&f)");
                                    if(lastClickTime > 0) {
                                        Kore.sendMessageWithPrefix("(Ultrasequencer) Since last click &c"+(System.currentTimeMillis()-lastClickTime)+"ms&f passed");
                                    }
                                }
                                lastClickTime = System.currentTimeMillis();
                                ticks = 0;
                                until = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if(!Kore.autoExperiments.isToggled()) return;

        if(GuiUtils.getInventoryName(event.gui).startsWith("Chronomatron") || GuiUtils.getInventoryName(event.gui).startsWith("Ultrasequencer") || GuiUtils.getInventoryName(event.gui).startsWith("Experimentation Table")) {
            Kore.mc.fontRendererObj.drawStringWithShadow("[KORE] ",5,5,new Color(255, 85, 85).getRGB());
            Kore.mc.fontRendererObj.drawStringWithShadow("AutoExperiments",42,5,Color.WHITE.getRGB());
            if(Kore.clientSettings.debug.isEnabled()) {
                if(chronomatronSolver.isEnabled()) {
                    Kore.mc.fontRendererObj.drawStringWithShadow("chronomatronSolver is ",5,15,Color.WHITE.getRGB());
                    Kore.mc.fontRendererObj.drawStringWithShadow("Enabled",124,15,Color.GREEN.getRGB());
                } else {
                    Kore.mc.fontRendererObj.drawStringWithShadow("chronomatronSolver is ",5,15,Color.WHITE.getRGB());
                    Kore.mc.fontRendererObj.drawStringWithShadow("Disabled",124,15,Color.RED.getRGB());
                }
                if(ultrasequencerSolver.isEnabled()) {
                    Kore.mc.fontRendererObj.drawStringWithShadow("ultrasequencerSolver is",5,25,Color.WHITE.getRGB());
                    Kore.mc.fontRendererObj.drawStringWithShadow("Enabled",132,25,Color.GREEN.getRGB());
                } else {
                    Kore.mc.fontRendererObj.drawStringWithShadow("ultrasequencerSolver is",5,25,Color.WHITE.getRGB());
                    Kore.mc.fontRendererObj.drawStringWithShadow("Disabled",132,25,Color.RED.getRGB());
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!Kore.autoExperiments.isToggled() || event.phase != TickEvent.Phase.START) return;

        ticks = (ticks + 1) % 20 == 0 ? 0 : ticks;

        if (Kore.mc.currentScreen instanceof GuiChest) {
            if (Kore.mc.thePlayer != null) {
                ContainerChest chest = (ContainerChest) Kore.mc.thePlayer.openContainer;
                List<Slot> invSlots = ((GuiChest) Kore.mc.currentScreen).inventorySlots.inventorySlots;
                String chestName = chest.getLowerChestInventory().getDisplayName().getUnformattedText().trim();

                if (ultrasequencerSolver.isEnabled() && chestName.startsWith("Ultrasequencer (")) {
                    if (invSlots.get(49).getStack() != null && invSlots.get(49).getStack().getDisplayName().equals(Kore.fancy+"aRemember the pattern!")) { // §aRemember the pattern!
                        for (int i = 9; i <= 44; i++) {
                            if (invSlots.get(i) != null && invSlots.get(i).getStack() != null) {
                                String itemName = StringUtils.stripControlCodes(invSlots.get(i).getStack().getDisplayName());
                                if (itemName.matches("\\d+")) {
                                    int number = Integer.parseInt(itemName);
                                    clickInOrderSlots[number - 1] = invSlots.get(i);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
