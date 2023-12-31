package katoz.kore.modules.player;

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

import katoz.kore.Kore;
import katoz.kore.config.KoreConfig;
import katoz.kore.utils.LocationUtils;
import katoz.kore.utils.GuiUtils;
import katoz.kore.utils.ModUtils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoExperiments {
    private int tickAmount = 0;
    private final Random rand = new Random(System.currentTimeMillis());
    private int randDelay = 0;
    // Chronomatron
    static int lastChronomatronRound = 0;
    static List<String> chronomatronPattern = new ArrayList<>();
    static int chronomatronMouseClicks = 0;
    private long lastClickTime = 0L;
    // Ultrasequencer
    static Slot[] clickInOrderSlots = new Slot[36];
    static int lastUltraSequencerClicked = 0;
    private int until = 0;

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        lastChronomatronRound = 0;
        chronomatronPattern.clear();
        chronomatronMouseClicks = 0;
        clickInOrderSlots = new Slot[36];
    }
    
    @SubscribeEvent
    public void onTick(GuiScreenEvent.BackgroundDrawnEvent event) {
        if(!isEnabled()) return;

        if (Kore.mc.currentScreen instanceof GuiChest) {
            GuiChest inventory = (GuiChest) event.gui;
            Container containerChest = inventory.inventorySlots;
            if (containerChest instanceof ContainerChest) {
                List<Slot> invSlots = containerChest.inventorySlots;
                String invName = ((ContainerChest) containerChest).getLowerChestInventory().getDisplayName().getUnformattedText().trim();

                if (KoreConfig.chronomatronSolver && invName.startsWith("Chronomatron (")) {
                    EntityPlayerSP player = Kore.mc.thePlayer;
                    if (player.inventory.getItemStack() == null && invSlots.size() > 48 && invSlots.get(49).getStack() != null) {
                        if (invSlots.get(49).getStack().getDisplayName().startsWith("§7Timer: §a") && invSlots.get(4).getStack() != null) {
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
                                    if (player.inventory.getItemStack() == null && glass != null && tickAmount % 5 == 0  && lastClickTime+((KoreConfig.autoExperimentsDelay+randDelay)*50L) < System.currentTimeMillis() ) { //tickAmount % 5 == 0
                                        Slot glassSlot = invSlots.get(i);
                                        if (glass.getDisplayName().equals(chronomatronPattern.get(chronomatronMouseClicks))) {
                                            randDelay = rand.nextInt(3);
                                            Kore.mc.playerController.windowClick(Kore.mc.thePlayer.openContainer.windowId,glassSlot.slotNumber,2,3, Kore.mc.thePlayer);
                                            if(KoreConfig.devMode) {
                                                ModUtils.sendMessage("(Chronomatron) Clicked Slot " + glassSlot.slotNumber + " (" + glassSlot.getStack().getDisplayName() + "§f)");
                                            }
                                            lastClickTime = System.currentTimeMillis();
                                            chronomatronMouseClicks++;
                                            break;
                                        }
                                    }
                                }
                            }
                        } else if (invSlots.get(49).getStack().getDisplayName().equals("§aRemember the pattern!")) {
                            chronomatronMouseClicks = 0;
                        }
                    }
                }

                if (KoreConfig.ultrasequencerSolver && invName.startsWith("Ultrasequencer (")) {
                    EntityPlayerSP player = Kore.mc.thePlayer;
                    if (player.inventory.getItemStack() == null && invSlots.size() > 48 && invSlots.get(49).getStack() != null && invSlots.get(49).getStack().getDisplayName().startsWith("§7Timer: §a")) {
                        lastUltraSequencerClicked = 0;
                        for (Slot slot: clickInOrderSlots) {
                            if (slot != null && slot.getStack() != null && StringUtils.stripControlCodes(slot.getStack().getDisplayName()).matches("\\d+")) {
                                int number = Integer.parseInt(StringUtils.stripControlCodes(slot.getStack().getDisplayName()));
                                if (number > lastUltraSequencerClicked) {
                                    lastUltraSequencerClicked = number;
                                }
                            }
                        }
                        if (player.inventory.getItemStack() == null && clickInOrderSlots[lastUltraSequencerClicked] != null && tickAmount % (2+(KoreConfig.autoExperimentsDelay+randDelay)) == 0 && lastUltraSequencerClicked != 0 && until == lastUltraSequencerClicked) {
                            Slot nextSlot = clickInOrderSlots[lastUltraSequencerClicked];
                            randDelay = rand.nextInt(3);
                            Kore.mc.playerController.windowClick(Kore.mc.thePlayer.openContainer.windowId, nextSlot.slotNumber, 2, 3, Kore.mc.thePlayer);
                            if(KoreConfig.devMode) {
                                ModUtils.sendMessage("(Ultrasequencer) Clicked Slot " + nextSlot.slotNumber + " (§c" + (lastUltraSequencerClicked+1) + "§f)");
                            }
                            until = lastUltraSequencerClicked + 1;
                            tickAmount = 0;
                        }
                        if (player.inventory.getItemStack() == null && clickInOrderSlots[lastUltraSequencerClicked] != null && tickAmount == (18+(KoreConfig.autoExperimentsDelay+randDelay)) && lastUltraSequencerClicked < 1) {
                            Slot nextSlot = clickInOrderSlots[lastUltraSequencerClicked];
                            randDelay = rand.nextInt(3);
                            Kore.mc.playerController.windowClick(Kore.mc.thePlayer.openContainer.windowId, nextSlot.slotNumber, 2, 3, Kore.mc.thePlayer);
                            if(KoreConfig.devMode) {
                                ModUtils.sendMessage("(Ultrasequencer) Clicked Slot " + nextSlot.slotNumber + " (§c" + (lastUltraSequencerClicked+1) + "§f)");
                            }
                            tickAmount = 0;
                            until = 1;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
        if(!isEnabled()) return;

        if(GuiUtils.getInventoryName(event.gui).startsWith("Chronomatron") || GuiUtils.getInventoryName(event.gui).startsWith("Ultrasequencer") || GuiUtils.getInventoryName(event.gui).startsWith("Experimentation Table")) {
            Kore.mc.fontRendererObj.drawStringWithShadow("[KORE] ",5,5,new Color(255, 85, 85).getRGB());
            Kore.mc.fontRendererObj.drawStringWithShadow("AutoExperiments is active",42,5,Color.WHITE.getRGB());
            if(KoreConfig.devMode) {
                if(KoreConfig.chronomatronSolver) {
                    Kore.mc.fontRendererObj.drawStringWithShadow("chronomatronSolver",42,15,Color.GREEN.getRGB());
                } else {
                    Kore.mc.fontRendererObj.drawStringWithShadow("chronomatronSolver",42,15,Color.RED.getRGB());
                }
                if(KoreConfig.ultrasequencerSolver) {
                    Kore.mc.fontRendererObj.drawStringWithShadow("ultrasequencerSolver",42,25,Color.GREEN.getRGB());
                } else {
                    Kore.mc.fontRendererObj.drawStringWithShadow("ultrasequencerSolver",42,25,Color.RED.getRGB());
                }
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!isEnabled() || event.phase != TickEvent.Phase.START) return;

        tickAmount++;
        if (tickAmount % (20+(KoreConfig.autoExperimentsDelay+randDelay)) == 0) {
            tickAmount = 0;
        }

        if (Kore.mc.currentScreen instanceof GuiChest) {
            if (Kore.mc.thePlayer != null) {
                ContainerChest chest = (ContainerChest) Kore.mc.thePlayer.openContainer;
                List<Slot> invSlots = ((GuiChest) Kore.mc.currentScreen).inventorySlots.inventorySlots;
                String chestName = chest.getLowerChestInventory().getDisplayName().getUnformattedText().trim();

                if (KoreConfig.ultrasequencerSolver && chestName.startsWith("Ultrasequencer (")) {
                    if (invSlots.get(49).getStack() != null && invSlots.get(49).getStack().getDisplayName().equals("§aRemember the pattern!")) {
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

    public static boolean isEnabled() {
        return KoreConfig.autoExperiments && LocationUtils.onSkyblock;
    }
}