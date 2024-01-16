package net.kore.mixins.player;

import net.kore.Kore;
import net.kore.events.PreAttackEvent;
import org.spongepowered.asm.mixin.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.entity.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin({ PlayerControllerMP.class })
public class MixinPlayerController
{
    @Redirect(method = { "onPlayerDamageBlock" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getPlayerRelativeBlockHardness(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/util/BlockPos;)F"))
    public float onPlayerDamageBlock(final Block instance, final EntityPlayer playerIn, final World worldIn, final BlockPos pos) {
        float hardness = instance.getPlayerRelativeBlockHardness(playerIn, worldIn, pos);
        if (Kore.fastBreak != null && Kore.fastBreak.isToggled()) {
            hardness *= (float)Kore.fastBreak.mineSpeed.getValue();
        }
        return hardness;
    }

    @Inject(method = { "attackEntity" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/client/network/NetHandlerPlayClient;addToSendQueue(Lnet/minecraft/network/Packet;)V") })
    public void attackEntity(final EntityPlayer playerIn, final Entity targetEntity, final CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post((Event)new PreAttackEvent(targetEntity))) {
            ci.cancel();
        }
    }
}
