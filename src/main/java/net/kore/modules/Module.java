package net.kore.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.kore.Kore;
import net.kore.managers.ConfigManager;
import net.kore.settings.Setting;
import net.kore.utils.MilliTimer;
import net.kore.ui.notifications.Notification;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Module {
    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("toggled")
    private boolean toggled;

    @Expose
    @SerializedName("keyCode")
    private int keycode;

    private final Category category;

    public boolean extended;
    @Expose
    @SerializedName("settings")
    public ConfigManager.ConfigSetting[] cfgSettings;

    private boolean devOnly;

    public final MilliTimer toggledTime;

    public final List<Setting> settings;

    public FlagType flagType;

    public VersionType versionType;

    public Module(final String name, final int keycode, final Category category) {
        this.toggledTime = new MilliTimer();
        this.settings = new ArrayList<Setting>();
        this.name = name;
        this.keycode = keycode;
        this.category = category;
        this.flagType = FlagType.SAFE;
    }

    public Module(final String name, final Category category) {
        this(name, 0, category);
    }

    public boolean isToggled() {
        return this.toggled;
    }

    public String getName() {
        return this.name;
    }

    public Category getCategory() {
        return this.category;
    }

    public void toggle() {
        this.setToggled(!this.toggled);
    }

    public void onEnable() {

    }

    public void riskWarning() {
        if(this.flagType == FlagType.DETECTED) {
            Kore.notificationManager.showNotification("This module is detected, use it carefully", 2500, Notification.NotificationType.WARNING);
        }
    }

    public void assign()
    {

    }

    public void setFlagType(FlagType type)
    {
        this.flagType = type;
    }

    public FlagType getFlagType()
    {
        return this.flagType;
    }

    public void setVersionType(VersionType type)
    {
        this.versionType = type;
    }

    public VersionType getVersionType()
    {
        return this.versionType;
    }

    public void onSave() {

    }

    public String getSuffix()
    {
        return "";
    }

    public void addSetting(final Setting setting) {
        this.getSettings().add(setting);
    }

    public void addSettings(final Setting... settings) {
        for (final Setting setting : settings) {
            this.addSetting(setting);
        }
    }

    public boolean isPressed() {
        return this.keycode != 0 && Keyboard.isKeyDown(this.keycode);
    }

    public int getKeycode() {
        return this.keycode;
    }

    public void setKeycode(final int keycode) {
        this.keycode = keycode;
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public static List<Module> getModulesByCategory(final Category c) {
        return (List<Module>) Kore.moduleManager.getModules().stream().filter(module -> module.category == c).collect(Collectors.toList());
    }

    public void setToggled(final boolean toggled) {
        if (this.toggled != toggled) {
            this.toggled = toggled;
            this.toggledTime.reset();
            if (toggled) {
                this.onEnable();
                this.riskWarning();
            }
            else {
                this.onDisable();
            }
        }
    }

    public void onDisable() {

    }

    public void setDevOnly(final boolean devOnly) {
        this.devOnly = devOnly;
    }

    public boolean isDevOnly() {
        return this.devOnly;
    }

    public enum Category
    {
        RENDER("Render"),
        COMBAT("Combat"),
        PLAYER("Player"),
        MOVEMENT("Movement"),
        SKYBLOCK("Skyblock"),
        MISC("Miscellaneous"),
        PROTECTIONS("Protections"),
        SETTINGS("Settings");

        public String name;

        private Category(final String name) {
            this.name = name;
        }
    }

    public enum VersionType
    {
        FREE,
        PREMIUM
    }

    public enum FlagType
    {
        SAFE,
        DETECTED
    }
}
