package net.kore.mixins.input;

import net.kore.events.MoveStateUpdateEvent;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = { MovementInputFromOptions.class }, priority = 1)
public abstract class MixinMovementInputFromOptions extends MixinMovementInput
{
    @Shadow
    @Final
    private GameSettings gameSettings;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0f;
        this.moveForward = 0.0f;
        if (this.gameSettings.keyBindForward.isKeyDown()) {
            ++this.moveForward;
        }
        if (this.gameSettings.keyBindBack.isKeyDown()) {
            --this.moveForward;
        }
        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            ++this.moveStrafe;
        }
        if (this.gameSettings.keyBindRight.isKeyDown()) {
            --this.moveStrafe;
        }
        final MoveStateUpdateEvent event = new MoveStateUpdateEvent(this.moveForward, this.moveStrafe, this.gameSettings.keyBindJump.isKeyDown(), this.gameSettings.keyBindSneak.isKeyDown());
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        this.jump = event.isJump();
        this.sneak = event.isSneak();
        this.moveForward = event.getForward();
        this.moveStrafe = event.getStrafe();
        if (this.sneak) {
            this.moveStrafe *= (float)0.3;
            this.moveForward *= (float)0.3;
        }
    }
}
