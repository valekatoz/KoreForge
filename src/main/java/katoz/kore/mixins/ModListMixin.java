package katoz.kore.mixins;

import io.netty.buffer.ByteBuf;
import katoz.kore.Kore;
import katoz.kore.config.KoreConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.handshake.FMLHandshakeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;

@Mixin(value = FMLHandshakeMessage.ModList.class)
public class ModListMixin {

    private static final Logger logger = LogManager.getLogger();

    @Shadow(remap = false)
    private Map<String, String> modTags;

    @Inject(method = "toBytes", at = @At(value = "HEAD"), cancellable = true, remap = false)
    public void toBytes(ByteBuf buffer, CallbackInfo callbackInfo) {
        if (!KoreConfig.modHider || Minecraft.getMinecraft().isSingleplayer()) return;

        callbackInfo.cancel();

        ArrayList<Map.Entry<String, String>> shownTags = new ArrayList<>();
        for (Map.Entry<String, String> modTag : this.modTags.entrySet()) {
            if (!modTag.getKey().equals(Kore.MODID)) {
                shownTags.add(modTag);
            } else {
                if(KoreConfig.devMode) {
                    logger.info(String.format("[KORE] Hidden Mod: %s %s", modTag.getKey(), modTag.getValue()));
                }
            }
        }

        ByteBufUtils.writeVarInt(buffer, shownTags.size(), 2);

        for (Map.Entry<String, String> modTag : shownTags) {
            if(KoreConfig.devMode) {
                logger.info(String.format("[KORE] Visible Mod: %s %s", modTag.getKey(), modTag.getValue()));
            }
            ByteBufUtils.writeUTF8String(buffer, modTag.getKey());
            ByteBufUtils.writeUTF8String(buffer, modTag.getValue());
        }

    }
}