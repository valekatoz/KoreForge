package net.kore.modules;

import net.kore.Kore;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.utils.Notification;
import net.kore.utils.api.ServerUtils;

import org.json.JSONObject;
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.Base64;

public class ClientSettings extends Module {
    public ModeSetting hideModules;
    public BooleanSetting debug;
    public BooleanSetting richPresence;
    public BooleanSetting autoUpdate;
    public BooleanSetting cosmeticsUnlocker;

    // Rich Presence

    public static IPCClient ipcClient = new IPCClient(1196540533611450588L);
    private static boolean hasConnected;
    private static RichPresence richPresenceData;

    // Cosmetics Unlocker

    private boolean unlockerToggle;

    public ClientSettings() {
        super("Client Settings", Category.SETTINGS);
        this.hideModules = new ModeSetting("Hidden modules", "None", "None", "Detected", "Premium", "Premium + Detected");
        this.debug = new BooleanSetting("Developer Mode", false);
        this.richPresence = new BooleanSetting("Rich Presence", true);
        this.autoUpdate = new BooleanSetting("Auto Update", true);
        this.cosmeticsUnlocker = new BooleanSetting("Unlock Cosmetics", true);

        unlockerToggle = unlockerToggle();

        this.addSettings(hideModules, debug, autoUpdate, richPresence, cosmeticsUnlocker);

        // Auto Updater (Licensed only)

        if(Boolean.parseBoolean(Kore.licensed) && this.autoUpdate.isEnabled()) {
            String stream = "upstream";
            UpdateContext updateContext = new UpdateContext(
                    UpdateSource.gistSource("valekatoz","83a452dad0b31823d77f3b37e6a5ff3b"),
                    UpdateTarget.deleteAndSaveInTheSameFolder(Kore.class),
                    CurrentVersion.of(Integer.parseInt(Kore.VERSION_NUMBER)),
                    Base64.getEncoder().encodeToString(Kore.MOD_ID.getBytes())
            );
            updateContext.cleanup();

            System.out.println("Update cleaned");
            System.out.println("Created update context: " + updateContext);

            updateContext.checkUpdate(stream).thenCompose(it -> {
                System.out.println("Checked for update on " + stream + ": " + it);
                System.out.println("Can update: " + it.isUpdateAvailable());
                return it.launchUpdate();
            }).join();

        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if(!hasConnected && this.richPresence.isEnabled()) {
            setupIPC();
        } else if(hasConnected && !this.richPresence.isEnabled()) {
            disableRichPresence();
        }

        if (cosmeticsUnlocker.isEnabled() && !unlockerToggle) {
            Kore.notificationManager.showNotification("Please reboot to apply changes", 5000, Notification.NotificationType.INFO);
            toggleUnlocker(true);
        } else if (!cosmeticsUnlocker.isEnabled() && unlockerToggle) {
            Kore.notificationManager.showNotification("Please reboot to apply changes", 5000, Notification.NotificationType.INFO);
            toggleUnlocker(false);
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

                    if(Boolean.parseBoolean(Kore.licensed)) {
                        if(Kore.licenseManager.isPremium()) {
                            builder.setDetails("Premium Mode");
                            builder.setState("Enjoying Premium Features");
                        } else {
                            builder.setDetails("Free Mode");
                            builder.setState("Enjoying Free Features");
                        }
                    } else {
                        builder.setDetails("Unlicensed Mode");
                        builder.setState("Enjoying Life");
                    }
                    builder.setLargeImage(ServerUtils.logo);
                    builder.setStartTimestamp(OffsetDateTime.now());

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

    private boolean unlockerToggle() {
        try {
            File configFile = new File(System.getenv("LOCALAPPDATA"), "koreCosmetics.json");
            if (!configFile.exists()) {
                return true;
            }

            JSONObject json = new JSONObject(new String(Files.readAllBytes(configFile.toPath())));

            return json.optBoolean("enabled", true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Kore] Could not read 'enabled' value from config file, returning default value (true)");
            return true;
        }
    }

    private void toggleUnlocker(boolean toggle) {
        try {
            File configFile = new File(System.getenv("LOCALAPPDATA"), "koreCosmetics.json");
            if (!configFile.exists()) {
                return; // it should exist tho since the mixin creates the file before this class is initialized
            }

            JSONObject json = new JSONObject(new String(Files.readAllBytes(configFile.toPath())));

            json.put("enabled", toggle);
            unlockerToggle = toggle;

            PrintWriter pw = new PrintWriter(configFile);
            pw.print(json.toString(2));
            pw.close();

            System.out.println("[Kore] toggled 'enabled' value in config file");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[Kore] Could not update 'enabled' value in config file");
        }
    }

    @Override
    public void assign() {
        Kore.clientSettings = this;
    }

}