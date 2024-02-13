package net.kore.modules.misc;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.ui.notifications.Notification;
import net.kore.utils.render.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import java.awt.Color;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.scoreboard.ScoreObjective;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import java.util.Arrays;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import java.util.ArrayList;
import java.util.List;

public class MurderFinder extends Module
{
    private ArrayList<Item> knives;
    public static ArrayList<EntityPlayer> murderers;
    public static ArrayList<EntityPlayer> detectives;
    private BooleanSetting autoSay;
    private BooleanSetting ingotESP;
    private BooleanSetting bowESP;
    private boolean inMurder;

    public MurderFinder() {
        super("Murder Finder", Category.MISC);
        this.knives = new ArrayList<Item>(Arrays.asList(Items.iron_sword, Items.stone_sword, Items.iron_shovel, Items.stick, Items.wooden_axe, Items.wooden_sword, Blocks.deadbush.getItem((World)null, (BlockPos)null), Items.stone_shovel, Items.diamond_shovel, Items.quartz, Items.pumpkin_pie, Items.golden_pickaxe, Items.apple, Items.name_tag, Blocks.sponge.getItem((World)null, (BlockPos)null), Items.carrot_on_a_stick, Items.bone, Items.carrot, Items.golden_carrot, Items.cookie, Items.diamond_axe, Blocks.red_flower.getItem((World)null, (BlockPos)null), Items.prismarine_shard, Items.cooked_beef, Items.golden_sword, Items.diamond_sword, Items.diamond_hoe, (Item)Items.shears, Items.fish, Items.dye, Items.boat, Items.speckled_melon, Items.blaze_rod, Items.fish));
        this.autoSay = new BooleanSetting("Say murderer", false);
        this.ingotESP = new BooleanSetting("Ingot ESP", true);
        this.bowESP = new BooleanSetting("Bow esp", true);
        this.addSettings(this.autoSay, this.ingotESP, this.bowESP);
        setToggled(false);
    }

    @Override
    public void assign()
    {
        Kore.murderFinder = this;
    }

    public boolean hasLine(final String line) {
        if (Kore.mc.thePlayer != null && Kore.mc.thePlayer.getWorldScoreboard() != null && Kore.mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1) != null) {
            final Scoreboard sb = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
            final List<Score> list = new ArrayList<Score>(sb.getSortedScores(sb.getObjectiveInDisplaySlot(1)));
            for (final Score score : list) {
                final ScorePlayerTeam team = sb.getPlayersTeam(score.getPlayerName());
                if (team != null) {
                    final String s = ChatFormatting.stripFormatting(team.getColorPrefix() + score.getPlayerName() + team.getColorSuffix());
                    final StringBuilder builder = new StringBuilder();
                    for (final char c : s.toCharArray()) {
                        if (c < Kore.fancy) {
                            builder.append(c);
                        }
                    }
                    if (builder.toString().toLowerCase().contains(line.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (!this.isToggled() || Kore.mc.thePlayer == null || Kore.mc.theWorld == null) {
            return;
        }

        try {
            if (Kore.mc.thePlayer.getWorldScoreboard() != null) {
                final ScoreObjective objective = Kore.mc.thePlayer.getWorldScoreboard().getObjectiveInDisplaySlot(1);
                if (objective != null && ChatFormatting.stripFormatting(objective.getDisplayName()).equals("MURDER MYSTERY") && hasLine("Innocents Left:")) {
                    this.inMurder = true;
                    for (final EntityPlayer player : Kore.mc.theWorld.playerEntities) {
                        if (!murderers.contains(player)) {
                            if (detectives.contains(player)) {
                                continue;
                            }
                            if (player.getHeldItem() == null) {
                                continue;
                            }
                            if (detectives.size() < 2 && player.getHeldItem().getItem().equals(Items.bow)) {
                                detectives.add(player);
                                Kore.sendMessageWithPrefix(String.format(Kore.fancy + "b%s is detective!", player.getName()));
                                Kore.notificationManager.showNotification(String.format(Kore.fancy + "b%s is detective!", player.getName()), 5000, Notification.NotificationType.WARNING);
                            }
                            if (!this.knives.contains(player.getHeldItem().getItem())) {
                                continue;
                            }
                            murderers.add(player);
                            Kore.sendMessageWithPrefix(String.format(Kore.fancy + "c%s is murderer!", player.getName()));
                            Kore.notificationManager.showNotification(String.format(Kore.fancy + "c%s is murderer!", player.getName()), 10000, Notification.NotificationType.WARNING);
                            if (!this.autoSay.isEnabled() || player == Kore.mc.thePlayer) {
                                continue;
                            }
                            Kore.mc.thePlayer.sendChatMessage(String.format("%s is murderer!", ChatFormatting.stripFormatting(player.getName())));
                        }
                    }
                    return;
                }
                this.inMurder = false;
                murderers.clear();
                detectives.clear();
            }
        }
        catch (Exception ex) {}
    }

    @SubscribeEvent
    public void onWorldRender(final RenderWorldLastEvent e) {
        if (!this.isToggled()) {
            return;
        }
        if (this.inMurder) {
            for (final Entity entity : Kore.mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityPlayer) {
                    if (((EntityPlayer)entity).isPlayerSleeping()) {
                        continue;
                    }
                    if (entity == Kore.mc.thePlayer) {
                        continue;
                    }
                    if (murderers.contains(entity)) {
                        RenderUtils.draw2D(entity, e.partialTicks, 1.0f, Color.red);
                        RenderUtils.tracerLine(entity, e.partialTicks, 1.0f, Color.RED);
                    }
                    else if (detectives.contains(entity)) {
                        RenderUtils.draw2D(entity, e.partialTicks, 1.0f, Color.blue);
                    }
                    else {
                        RenderUtils.draw2D(entity, e.partialTicks, 1.0f, Color.gray);
                    }
                }
                else if (entity instanceof EntityItem && ((EntityItem)entity).getEntityItem().getItem() == Items.gold_ingot && this.ingotESP.isEnabled()) {
                    RenderUtils.draw2D(entity, e.partialTicks, 1.0f, Color.yellow);
                }
                else {
                    if (!this.bowESP.isEnabled() || !(entity instanceof EntityArmorStand) || ((EntityArmorStand)entity).getEquipmentInSlot(0) == null || ((EntityArmorStand)entity).getEquipmentInSlot(0).getItem() != Items.bow) {
                        continue;
                    }
                    RenderUtils.draw2D(entity, e.partialTicks, 1.0f, Color.CYAN);
                }
            }
        }
    }

    static {
        murderers = new ArrayList<EntityPlayer>();
        detectives = new ArrayList<EntityPlayer>();
    }
}
