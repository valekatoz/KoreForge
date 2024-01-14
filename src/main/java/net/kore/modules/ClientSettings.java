package net.kore.modules;

import net.kore.Kore;
import net.kore.settings.BooleanSetting;
import net.kore.settings.RunnableSetting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ClientSettings extends Module {
    public BooleanSetting hideDetectedModules;
    public BooleanSetting debug;

    public ClientSettings()
    {
        super("Client Settings", Category.SETTINGS);
        this.debug = new BooleanSetting("Developer Mode", false);
        this.hideDetectedModules = new BooleanSetting("Hide Detected", false);
        this.addSettings(debug, hideDetectedModules);
    }

    @Override
    public void assign()
    {
        Kore.clientSettings = this;
    }
}