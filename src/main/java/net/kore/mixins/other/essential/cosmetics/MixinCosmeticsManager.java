package net.kore.mixins.other.essential.cosmetics;

import com.google.common.collect.ImmutableMap;
import gg.essential.gui.elementa.state.v2.State;
import gg.essential.gui.elementa.state.v2.StateKt;
import gg.essential.gui.notification.Notifications;
import gg.essential.mod.cosmetics.CosmeticSlot;
import gg.essential.network.connectionmanager.ConnectionManager;
import gg.essential.network.connectionmanager.cosmetics.CosmeticsData;
import gg.essential.network.connectionmanager.cosmetics.CosmeticsManager;
import gg.essential.network.connectionmanager.cosmetics.EquippedCosmeticsManager;
import gg.essential.network.cosmetics.Cosmetic;
import gg.essential.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(value = CosmeticsManager.class, remap = false)
public abstract class MixinCosmeticsManager {
    public Map<CosmeticSlot, String> map = new HashMap<>();

    private boolean toggled;

    @Shadow
    @Final
    private @NotNull EquippedCosmeticsManager equippedCosmeticsManager;

    @Shadow
    public abstract @NotNull CosmeticsData getCosmeticsData();

    @Shadow
    public abstract boolean getOwnCosmeticsVisible();

    @Shadow
    private @NotNull State<Set<String>> unlockedCosmetics;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public @NotNull State<Set<String>> getUnlockedCosmetics() {
        if(toggled) {
            return StateKt.stateOf(getCosmeticsData().getCosmetics().get().stream().map(Cosmetic::getId).collect(Collectors.toSet()));
        } else {
            return this.unlockedCosmetics;
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void CosmeticManager(ConnectionManager connectionManager, File baseDir, CallbackInfo ci) {
        try {
            System.out.println("[Kore] Loading config");
            File configFile = new File(System.getenv("LOCALAPPDATA"), "koreCosmetics.json");

            if (!configFile.exists()) {
                JSONObject defaultConfig = new JSONObject();
                defaultConfig.put("enabled", true);
                defaultConfig.put("cosmetics", new JSONObject());
                Files.write(configFile.toPath(), Collections.singleton(defaultConfig.toString()));
            }

            JSONObject json = new JSONObject(new String(Files.readAllBytes(configFile.toPath())));

            toggled = json.optBoolean("enabled", true);

            if(toggled) {
                JSONObject cosmeticsJson = json.getJSONObject("cosmetics");
                for (String key : cosmeticsJson.keySet()) {
                    map.put(CosmeticSlot.Companion.of(key), cosmeticsJson.getString(key));
                }

                if (map.isEmpty()) return;
                System.out.println("[Kore] Config loaded");
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.out.println("[Kore] Could not load config file");
        }
    }

    @Inject(method = "resetState", at = @At("TAIL"))
    public void resetState(CallbackInfo ci) {
        if(toggled) {
            equippedCosmeticsManager.update(UUIDUtil.getClientUUID(), map, Collections.emptyMap());
        }
    }

    @Inject(method = "toggleOwnCosmeticVisibility", at = @At("HEAD"))
    public void toggleOwnCosmeticVisibility(boolean notification, CallbackInfo ci) {
        if(toggled) {
            if (getOwnCosmeticsVisible()) return;
            Notifications.INSTANCE.push("Kore", "Loaded cosmetics from config.");
            equippedCosmeticsManager.update(UUIDUtil.getClientUUID(), map, Collections.emptyMap());
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public @NotNull Map<CosmeticSlot, String> getEquippedCosmetics() {
        ImmutableMap<CosmeticSlot, String> result = null;
        if(toggled) {
            result = ImmutableMap.copyOf(map);
        }
        return result != null ? result : ImmutableMap.of();
    }

    @Inject(method = "updateEquippedCosmetic(Lgg/essential/mod/cosmetics/CosmeticSlot;Ljava/lang/String;)V", at = @At("HEAD"))
    public void updateEquippedCosmetic(CosmeticSlot slot, String cosmeticId, CallbackInfo ci) {
        if (toggled) {
            if (cosmeticId != null) map.put(slot, cosmeticId);
            else map.remove(slot);

            try {
                System.out.println("[Kore] Saving config");
                JSONObject json = new JSONObject();
                json.put("enabled", toggled);

                JSONObject cosmetics = new JSONObject();
                for (Map.Entry<CosmeticSlot, String> entry : map.entrySet()) {
                    cosmetics.put(entry.getKey().getId(), entry.getValue());
                }
                json.put("cosmetics", cosmetics);

                PrintWriter pw = new PrintWriter(new File(System.getenv("LOCALAPPDATA"), "koreCosmetics.json"));
                pw.print(json.toString(2));
                pw.close();
                System.out.println("[Kore] Config saved");
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("[Kore] Could not save config file");
            }
        }
    }
}
