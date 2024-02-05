package net.kore.modules.player;

import net.kore.Kore;
import net.kore.events.PacketSentEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.utils.MilliTimer;
import net.kore.utils.PlayerUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;

public class AutoTool extends Module
{
    public BooleanSetting tools;
    public BooleanSetting swords;
    private MilliTimer delay;

    public AutoTool() {
        super("Auto Tool", Category.PLAYER);
        this.tools = new BooleanSetting("Tools", true);
        this.swords = new BooleanSetting("Swords", true);
        this.delay = new MilliTimer();
        this.addSettings(this.tools, this.swords);
    }

    @Override
    public void assign()
    {
        Kore.autoTool = this;
    }

    @SubscribeEvent
    public void onPacket(final PacketSentEvent event) {
        if (!this.isToggled() || Kore.mc.thePlayer == null) {
            return;
        }
        if (this.tools.isEnabled() && !Kore.mc.thePlayer.isUsingItem() && event.packet instanceof C07PacketPlayerDigging && ((C07PacketPlayerDigging)event.packet).getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = Kore.mc.thePlayer.inventory.getStackInSlot(i);
                final Block block = Kore.mc.theWorld.getBlockState(((C07PacketPlayerDigging)event.packet).getPosition()).getBlock();
                if (stack != null && block != null && stack.getStrVsBlock(block) > ((Kore.mc.thePlayer.inventory.getCurrentItem() == null) ? 1.0f : Kore.mc.thePlayer.inventory.getCurrentItem().getStrVsBlock(block))) {
                    Kore.mc.thePlayer.inventory.currentItem = i;
                }
            }
            PlayerUtils.syncHeldItem();
        }
        else if (this.delay.hasTimePassed(500L) && !Kore.mc.thePlayer.isUsingItem() && this.swords.isEnabled() && event.packet instanceof C02PacketUseEntity && ((C02PacketUseEntity)event.packet).getAction() == C02PacketUseEntity.Action.ATTACK) {
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = Kore.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && getToolDamage(stack) > ((Kore.mc.thePlayer.inventory.getCurrentItem() == null) ? 0.0f : getToolDamage(Kore.mc.thePlayer.inventory.getCurrentItem()))) {
                    Kore.mc.thePlayer.inventory.currentItem = i;
                }
            }
            PlayerUtils.syncHeldItem();
        }
        if ((event.packet instanceof C09PacketHeldItemChange && Kore.mc.thePlayer.inventory.getStackInSlot(((C09PacketHeldItemChange)event.packet).getSlotId()) != null) || (event.packet instanceof C08PacketPlayerBlockPlacement && ((C08PacketPlayerBlockPlacement)event.packet).getStack() != null)) {
            this.delay.reset();
        }
    }

    public static float getToolDamage(final ItemStack tool) {
        float damage = 0.0f;
        if (tool != null && (tool.getItem() instanceof ItemTool || tool.getItem() instanceof ItemSword)) {
            if (tool.getItem() instanceof ItemSword) {
                damage += 4.0f;
            }
            else if (tool.getItem() instanceof ItemAxe) {
                damage += 3.0f;
            }
            else if (tool.getItem() instanceof ItemPickaxe) {
                damage += 2.0f;
            }
            else if (tool.getItem() instanceof ItemSpade) {
                ++damage;
            }
            damage += ((tool.getItem() instanceof ItemTool) ? ((ItemTool)tool.getItem()).getToolMaterial().getDamageVsEntity() : ((ItemSword)tool.getItem()).getDamageVsEntity());
            damage += (float)(1.25 * EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, tool));
            damage += (float)(EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, tool) * 0.5);
        }
        return damage;
    }
}
