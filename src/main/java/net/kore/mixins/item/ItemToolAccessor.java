package net.kore.mixins.item;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.item.ItemTool;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ItemTool.class })
public interface ItemToolAccessor
{
    @Accessor("toolClass")
    String getToolClass();
}
