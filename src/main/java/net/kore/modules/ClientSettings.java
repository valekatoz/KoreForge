package net.kore.modules;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;

public class ClientSettings extends Module {
    public BooleanSetting cosmeticsUnlocker = new BooleanSetting("Unlock Cosmetics", false);
    public BooleanSetting debug = new BooleanSetting("Developer Mode", false);

    public ClientSettings()
    {
        super("Client Settings", Category.SETTINGS);
        this.addSettings(cosmeticsUnlocker, debug);
    }

    @Override
    public void assign()
    {
        Kore.clientSettings = this;
    }
}