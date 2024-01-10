package net.kore.mixins.gui;

import net.kore.Kore;
import net.kore.modules.render.PopupAnimation;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ GuiContainer.class })
public abstract class MixinGuiContainer extends MixinGuiScreen {
    @Shadow
    public Container inventorySlots;

    @Inject(method = { "drawScreen" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/inventory/GuiContainer;drawGuiContainerBackgroundLayer(FII)V") }, cancellable = true)
    public void onDrawBackground(final int k1, final int slot, final float i1, final CallbackInfo ci) {
        if (PopupAnimation.shouldScale(Kore.mc.currentScreen)) {
            GL11.glPushMatrix();
            PopupAnimation.doScaling();
        }
    }

    @Inject(method = { "drawScreen" }, at = { @At("RETURN") }, cancellable = true)
    public void onDrawScreenPost(final int k1, final int slot, final float i1, final CallbackInfo ci) {
        if (PopupAnimation.shouldScale(Kore.mc.currentScreen)) {
            GL11.glPopMatrix();
        }
    }
}
