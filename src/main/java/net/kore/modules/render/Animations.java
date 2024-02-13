package net.kore.modules.render;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;

public class Animations extends Module
{
    public NumberSetting x;
    public NumberSetting y;
    public NumberSetting z;
    public NumberSetting size;
    public NumberSetting speed;
    public ModeSetting mode;
    public BooleanSetting showSwing;

    public Animations() {
        super("Animations", Module.Category.RENDER);
        this.x = new NumberSetting("x", 1.0, 0.01, 3.0, 0.02);
        this.y = new NumberSetting("y", 1.0, 0.01, 3.0, 0.02);
        this.z = new NumberSetting("z", 1.0, 0.01, 3.0, 0.02);
        this.size = new NumberSetting("size", 1.0, 0.01, 3.0, 0.02);
        this.speed = new NumberSetting("Speed", 6.0D, 1.0D, 50.0D, 1.0D, a -> true);
        this.mode = new ModeSetting("Mode", "1.7", new String[] { "1.7", "chill", "push", "spin", "vertical spin", "helicopter", "custom" });
        this.showSwing = new BooleanSetting("Swing progress", false);
        this.addSettings(this.x, this.y, this.z, this.size, this.speed, this.mode, this.showSwing);
    }

    @Override
    public void assign()
    {
        Kore.animations = this;
    }
}
