package net.kore.mixins.input;

import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.util.MovementInput;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ MovementInput.class })
public class MixinMovementInput
{
    @Shadow
    public boolean sneak;
    @Shadow
    public boolean jump;
    @Shadow
    public float moveStrafe;
    @Shadow
    public float moveForward;
}
