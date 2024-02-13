package net.kore.modules.movement;

import net.kore.Kore;
import net.kore.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.GameSettings;

public class SafeWalk extends Module
{
    public SafeWalk() {
        super("Safe Walk", 0, Category.MOVEMENT);
    }

    @Override
    public void assign()
    {
        Kore.safeWalk = this;
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(Kore.mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(Kore.mc.gameSettings.keyBindSneak));
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (Kore.mc.thePlayer == null || Kore.mc.theWorld == null || !this.isToggled() || Kore.mc.currentScreen != null) {
            return;
        }
        final BlockPos BP = new BlockPos(Kore.mc.thePlayer.posX, Kore.mc.thePlayer.posY - 0.5, Kore.mc.thePlayer.posZ);
        if (Kore.mc.theWorld.getBlockState(BP).getBlock() == Blocks.air && Kore.mc.theWorld.getBlockState(BP.down()).getBlock() == Blocks.air && Kore.mc.thePlayer.onGround && Kore.mc.thePlayer.movementInput.moveForward < 0.1f) {
            KeyBinding.setKeyBindState(Kore.mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
        else {
            KeyBinding.setKeyBindState(Kore.mc.gameSettings.keyBindSneak.getKeyCode(), GameSettings.isKeyDown(Kore.mc.gameSettings.keyBindSneak));
        }
    }
}
