package net.kore.mixins.other.essential.cosmetics;

import com.google.common.collect.ImmutableMap;
import gg.essential.gui.elementa.state.v2.State;
import gg.essential.gui.elementa.state.v2.StateKt;
import gg.essential.gui.notification.Notifications;
import gg.essential.mod.cosmetics.CosmeticSlot;
import gg.essential.network.connectionmanager.ConnectionManager;
import gg.essential.network.connectionmanager.cosmetics.CosmeticsData;
import gg.essential.network.connectionmanager.cosmetics.CosmeticsManager;
import gg.essential.network.cosmetics.Cosmetic;
import gg.essential.util.UUIDUtil;
import net.kore.Kore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Mixin(value = CosmeticsManager.class, remap = false)
public abstract class MixinCosmeticsManager {
    public Map<CosmeticSlot, String> map = new HashMap<>();

    @Shadow
    public abstract @NotNull CosmeticsData getCosmeticsData();

    @Shadow
    public abstract void setEquippedCosmetics(@NotNull UUID playerId, @NotNull Map<CosmeticSlot, String> equippedCosmetics);

    @Shadow
    public abstract @Nullable ImmutableMap<CosmeticSlot, String> getEquippedCosmetics(UUID playerId);

    @Shadow
    private boolean ownCosmeticsVisible;

    @Shadow @Final private @NotNull State<Set<String>> unlockedCosmetics;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public @NotNull State<Set<String>> getUnlockedCosmetics() {
        if(Kore.themeManager != null && Kore.clientSettings.cosmeticsUnlocker != null && Kore.clientSettings.cosmeticsUnlocker.isEnabled()) {
            return StateKt.stateOf(getCosmeticsData().getCosmetics().get().stream().map(Cosmetic::getId).collect(Collectors.toSet()));
        } else {
            return unlockedCosmetics;
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void CosmeticManager(ConnectionManager connectionManager, File baseDir, CallbackInfo ci) {
        if(Kore.clientSettings != null && Kore.clientSettings.cosmeticsUnlocker != null && Kore.clientSettings.cosmeticsUnlocker.isEnabled()) {
            //load config
            try {
                System.out.println("[Kore] Loading config");
                Scanner sc = new Scanner(new File(System.getenv("LOCALAPPDATA"), "kore.cosmetics.txt"));

                while (sc.hasNextLine()) {
                    String[] line = sc.nextLine().split("=");
                    map.put(CosmeticSlot.Companion.of(line[0]), line[1]);
                }

                if (map.isEmpty()) return;
                System.out.println("[Kore] Config loaded");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[Kore] Could not load config file");
            }
        }
    }

    @Inject(method = "resetState", at = @At("TAIL"))
    public void resetState(CallbackInfo ci) {
        if(Kore.clientSettings != null && Kore.clientSettings.cosmeticsUnlocker != null && Kore.clientSettings.cosmeticsUnlocker.isEnabled()) {
            setEquippedCosmetics(UUIDUtil.getClientUUID(), map);
        }
    }

    @Inject(method = "toggleOwnCosmeticVisibility", at = @At("HEAD"))
    public void toggleOwnCosmeticVisibility(boolean notification, CallbackInfo ci) {
        if(Kore.clientSettings != null && Kore.clientSettings.cosmeticsUnlocker != null && Kore.clientSettings.cosmeticsUnlocker.isEnabled()) {
            if (ownCosmeticsVisible) return;
            Notifications.INSTANCE.push("Kore", "Loaded cosmetics from config.");
            setEquippedCosmetics(UUIDUtil.getClientUUID(), map);
        }
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public @NotNull ImmutableMap<CosmeticSlot, String> getEquippedCosmetics() {
        if(Kore.clientSettings != null && Kore.clientSettings != null && Kore.clientSettings.cosmeticsUnlocker != null && Kore.clientSettings.cosmeticsUnlocker.isEnabled()) {
            ImmutableMap<CosmeticSlot, String> result = ImmutableMap.copyOf(map);
            return result != null ? result : ImmutableMap.of();
        } else {
            ImmutableMap<CosmeticSlot, String> result = getEquippedCosmetics(UUIDUtil.getClientUUID());
            return result != null ? result : ImmutableMap.of();
        }
    }

    @Inject(method = "updateEquippedCosmetic(Lgg/essential/mod/cosmetics/CosmeticSlot;Ljava/lang/String;)V", at = @At("HEAD"))
    public void updateEquippedCosmetic(CosmeticSlot slot, String cosmeticId, CallbackInfo ci) {
        if(Kore.clientSettings != null && Kore.clientSettings.cosmeticsUnlocker != null && Kore.clientSettings.cosmeticsUnlocker.isEnabled()) {
            if (cosmeticId != null) map.put(slot, cosmeticId);
            else map.remove(slot);

            //save config
            try {
                System.out.println("[Kore] Saving config");
                PrintWriter pw = new PrintWriter(new File(System.getenv("LOCALAPPDATA"), "kore.cosmetics.txt"));

                for (Map.Entry<CosmeticSlot, String> entry : map.entrySet()) {
                    pw.println(entry.getKey().getId() + "=" + entry.getValue());
                }

                pw.close();
                System.out.println("[Kore] Config saved");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[Kore] Could not save config file");
            }
        }
    }
}