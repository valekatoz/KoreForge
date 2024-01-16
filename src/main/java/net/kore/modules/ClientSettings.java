package net.kore.modules;

import moe.nea.libautoupdate.*;
import net.kore.Kore;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;

import java.util.Base64;

public class ClientSettings extends Module {
    public ModeSetting hideModules;
    public BooleanSetting debug;
    public BooleanSetting richPresence;
    public BooleanSetting autoUpdate;

    public ClientSettings() {
        super("Client Settings", Category.SETTINGS);
        this.setToggled(true);
        this.hideModules = new ModeSetting("Hidden modules", "None", "None", "Detected", "Premium", "Premium + Detected");
        this.debug = new BooleanSetting("Developer Mode", false);
        this.richPresence = new BooleanSetting("Rich Presence", true);
        this.autoUpdate = new BooleanSetting("Auto Update", true);
        this.addSettings(hideModules, debug, richPresence, autoUpdate);

        if(this.autoUpdate.isEnabled()) {
            String stream = "upstream";
            UpdateContext updateContext = new UpdateContext(
                    UpdateSource.gistSource("valekatoz","83a452dad0b31823d77f3b37e6a5ff3b"),
                    UpdateTarget.deleteAndSaveInTheSameFolder(Main.class),
                    CurrentVersion.of(Integer.parseInt(Kore.VERSION_NUMBER)),
                    Base64.getEncoder().encodeToString(Kore.MOD_ID.getBytes())
            );
            updateContext.cleanup();
            updateContext.checkUpdate(stream).thenCompose(it -> {
                System.out.println("Checked for update on " + stream + ": " + it);
                System.out.println("Can update: " + it.isUpdateAvailable());
                return it.launchUpdate();
            }).join();
        }
    }

    @Override
    public void assign() {
        Kore.clientSettings = this;
    }

}