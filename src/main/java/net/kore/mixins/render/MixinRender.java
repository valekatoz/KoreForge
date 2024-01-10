package net.kore.mixins.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({ Render.class })
public abstract class MixinRender
{
    @Shadow
    public <T extends Entity> void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
    }
}
