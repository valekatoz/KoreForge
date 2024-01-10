package net.kore.mixins;

import net.kore.Kore;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "startGame", at = @At("TAIL"), cancellable = false)
    public void startGame(CallbackInfo ci)
    {
        Kore.mc = Minecraft.getMinecraft();
    }

    @Shadow private int rightClickDelayTimer;
    @Shadow private Entity renderViewEntity;

    @Inject(method = { "runTick" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V") })
    public void keyPresses(final CallbackInfo ci) {
        final int k = (Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + '\u0100') : Keyboard.getEventKey();
        final char aChar = Keyboard.getEventCharacter();
        if (Keyboard.getEventKeyState()) {
            if (Kore.mc.currentScreen == null) {
                Kore.handleKey(k);
            }
        }
    }

    @Inject(method = { "rightClickMouse" }, at = { @At("RETURN") }, cancellable = true)
    public void onRightClickPost(final CallbackInfo callbackInfo) {
        if (Kore.fastPlace.isToggled()) {
            this.rightClickDelayTimer = (int)Kore.fastPlace.placeDelay.getValue();
        }
    }

    @Inject(method = { "sendClickBlockToController" }, at = { @At("RETURN") })
    public void sendClickBlock(final CallbackInfo callbackInfo) {
        final boolean click = Kore.mc.currentScreen == null && Kore.mc.gameSettings.keyBindAttack.isKeyDown() && Kore.mc.inGameHasFocus;
        if (Kore.fastBreak.isToggled() && click && Kore.mc.objectMouseOver != null && Kore.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            for (int i = 0; i < Kore.fastBreak.maxBlocks.getValue(); ++i) {
                final BlockPos prevBlockPos = Kore.mc.objectMouseOver.getBlockPos();
                Kore.mc.objectMouseOver = this.renderViewEntity.rayTrace((double)Kore.mc.playerController.getBlockReachDistance(), 1.0f);
                final BlockPos blockpos = Kore.mc.objectMouseOver.getBlockPos();
                if (Kore.mc.objectMouseOver == null || blockpos == null || Kore.mc.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || blockpos == prevBlockPos || Kore.mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() == Material.air) {
                    break;
                }
                Kore.mc.thePlayer.swingItem();
                Kore.mc.playerController.clickBlock(blockpos, Kore.mc.objectMouseOver.sideHit);
            }
        }
    }
}
