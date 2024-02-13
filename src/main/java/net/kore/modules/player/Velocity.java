package net.kore.modules.player;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;

public class Velocity extends Module
{
    public NumberSetting vModifier;
    public NumberSetting hModifier;
    public BooleanSetting skyblockKB;

    public Velocity() {
        super("Velocity", 0, Category.PLAYER);
        this.vModifier = new NumberSetting("Vertical", 0.0, -2.0, 2.0, 0.05);
        this.hModifier = new NumberSetting("Horizontal", 0.0, -2.0, 2.0, 0.05);
        this.skyblockKB = new BooleanSetting("Skyblock kb", true);
        this.addSettings(this.hModifier, this.vModifier, this.skyblockKB);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.velocity = this;
    }
}
