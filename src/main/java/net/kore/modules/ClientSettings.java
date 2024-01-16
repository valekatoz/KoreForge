package net.kore.modules;

import com.google.common.net.InetAddresses;
import net.kore.Kore;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kore.utils.api.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import moe.nea.libautoupdate.UpdateContext;
import moe.nea.libautoupdate.CurrentVersion;
import moe.nea.libautoupdate.UpdateSource;
import moe.nea.libautoupdate.UpdateTarget;
import com.jagrosh.discordipc.entities.DiscordBuild;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.RichPresence;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.Random;

public class ClientSettings extends Module {
    public ModeSetting hideModules;
    public BooleanSetting debug;
    public BooleanSetting richPresence;
    public BooleanSetting autoUpdate;

    // Rich Presence

    public static IPCClient ipcClient = new IPCClient(1196540533611450588L);
    private static boolean hasConnected;
    private static RichPresence richPresenceData;

    public ClientSettings() {
        super("Client Settings", Category.SETTINGS);
        this.setToggled(true);
        this.hideModules = new ModeSetting("Hidden modules", "None", "None", "Detected", "Premium", "Premium + Detected");
        this.debug = new BooleanSetting("Developer Mode", false);
        this.richPresence = new BooleanSetting("Rich Presence", true);
        this.autoUpdate = new BooleanSetting("Auto Update", true);
        this.addSettings(hideModules, debug, richPresence, autoUpdate);

        // Auto Updater

        if(this.autoUpdate.isEnabled()) {
            String stream = "upstream";
            UpdateContext updateContext = new UpdateContext(
                    UpdateSource.gistSource("valekatoz","83a452dad0b31823d77f3b37e6a5ff3b"),
                    UpdateTarget.deleteAndSaveInTheSameFolder(Kore.class),
                    CurrentVersion.of(Integer.parseInt(Kore.VERSION_NUMBER)),
                    Base64.getEncoder().encodeToString(Kore.MOD_ID.getBytes())
            );
            updateContext.checkUpdate(stream)
                    .thenCompose(it -> {
                        System.out.println("Checked for update on " + stream + ": " + it);
                        System.out.println("Can update: " + it.isUpdateAvailable());
                        if (it.isUpdateAvailable()) {
                            return it.launchUpdate();
                        } else {
                            return CompletableFuture.completedFuture(null);
                        }
                    })
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    })
                    .join();

            updateContext.cleanup();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!hasConnected && this.richPresence.isEnabled()) {
            setupIPC();
        } else if(hasConnected && !this.richPresence.isEnabled()) {
            disableRichPresence();
        }
    }

    public void disableRichPresence() {
        try {
            ipcClient.close();
            hasConnected = false;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setupIPC() {
        if (Minecraft.isRunningOnMac) {
            return;
        }

        try {
            ipcClient.setListener(new IPCListener() {
                @Override
                public void onReady(final IPCClient client) {
                    final RichPresence.Builder builder = new RichPresence.Builder();

                    if(Kore.licenseManager.isPremium()) {
                        builder.setDetails("Premium Mode");
                        builder.setState("Enjoying Premium Features");
                    } else {
                        builder.setDetails("Free Mode");
                        builder.setState("Enjoying Free Features");
                    }
                    builder.setLargeImage(ServerUtils.logo);
                    builder.setStartTimestamp(System.currentTimeMillis());

                    richPresenceData = builder.build();
                    client.sendRichPresence(richPresenceData);

                    hasConnected = true;
                }
            });

            ipcClient.connect(new DiscordBuild[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void assign() {
        Kore.clientSettings = this;
    }

}