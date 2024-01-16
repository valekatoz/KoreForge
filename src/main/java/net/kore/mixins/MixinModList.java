package net.kore.mixins;

import net.kore.Kore;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(FMLHandshakeMessage.ModList.class)
public class MixinModList {

    @Shadow(remap = false) private Map<String, String> modTags;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At("RETURN"), cancellable = true, remap = false)
    public void constructor(List<ModContainer> containerList, CallbackInfo ci)
    {
        if(Kore.modHider == null || Kore.modHider.isToggled()) {
            modTags.clear();

            for (ModContainer mod : containerList)
            {
                if (mod.getModId().equals(Kore.MOD_ID) && !Kore.mc.isIntegratedServerRunning())
                    continue;

                modTags.put(mod.getModId(), mod.getVersion());
            }
        }
    }
}
