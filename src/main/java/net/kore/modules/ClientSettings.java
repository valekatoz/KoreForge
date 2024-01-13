package net.kore.modules;

import net.kore.Kore;
import net.kore.settings.BooleanSetting;

public class ClientSettings extends Module {
    public BooleanSetting unlockCosmetics;
    public BooleanSetting debug;

    public ClientSettings()
    {
        super("Client Settings", Category.SETTINGS);
        this.unlockCosmetics = new BooleanSetting("Unlock Cosmetics", false);
        this.debug = new BooleanSetting("Developer Mode", false);
        this.addSettings(unlockCosmetics, debug);
    }

    @Override
    public void assign()
    {
        Kore.clientSettings = this;
    }
}