package net.kore.managers;

import moe.nea.libautoupdate.CurrentVersion;
import moe.nea.libautoupdate.UpdateContext;
import moe.nea.libautoupdate.UpdateSource;
import moe.nea.libautoupdate.UpdateTarget;
import net.kore.Kore;

import java.util.concurrent.CompletableFuture;
import java.util.Base64;

public class UpdateManager {
    private boolean checked = false;
    private UpdateContext updateContext;
    private boolean updateAvailable = false;
    private String stream = "upstream";

    public UpdateManager() {
        // Auto Updater (Licensed only)
        updateContext = new UpdateContext(
                UpdateSource.gistSource("valekatoz","83a452dad0b31823d77f3b37e6a5ff3b"),
                UpdateTarget.deleteAndSaveInTheSameFolder(Kore.class),
                CurrentVersion.of(Integer.parseInt(Kore.VERSION_NUMBER)),
                Base64.getEncoder().encodeToString(Kore.MOD_ID.getBytes())
        );
        updateContext.cleanup();
    }

    public void update() {
        updateContext.checkUpdate(stream).thenCompose(it -> {
            if(it.isUpdateAvailable()) {
                updateAvailable = false;
            }

            return it.launchUpdate();
        }).join();
    }

    public boolean checkUpdate() {
        updateContext.checkUpdate(stream).thenCompose(it -> {
            if(it.isUpdateAvailable()) {
                updateAvailable = true;
            }

            return CompletableFuture.completedFuture(null);
        }).join();

        return updateAvailable;
    }

    public boolean hasChecked() {
        return checked;
    }

    public void setChecked(boolean hasChecked) {
        this.checked = hasChecked;
    }
}
