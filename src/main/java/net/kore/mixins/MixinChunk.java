package net.kore.mixins;

import net.kore.events.BlockChangeEvent;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ Chunk.class })
public class MixinChunk
{
    @Inject(method = { "setBlockState" }, at = { @At("HEAD") }, cancellable = true)
    private void onBlockChange(final BlockPos pos, final IBlockState state, final CallbackInfoReturnable<IBlockState> cir) {
        if (MinecraftForge.EVENT_BUS.post((Event)new BlockChangeEvent(pos, state))) {
            cir.setReturnValue(state);
        }
    }
}
