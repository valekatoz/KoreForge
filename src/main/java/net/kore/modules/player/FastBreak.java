package net.kore.modules.player;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.NumberSetting;

public class FastBreak extends Module
{
    public NumberSetting mineSpeed;
    public NumberSetting maxBlocks;

    public FastBreak() {
        super("Fast break", 0, Category.PLAYER);
        this.mineSpeed = new NumberSetting("Mining speed", 1.4, 1.0, 1.6, 0.1);
        this.maxBlocks = new NumberSetting("Additional blocks", 0.0, 0.0, 4.0, 1.0);
        this.addSettings(this.maxBlocks, this.mineSpeed);
        this.setFlagType(FlagType.RISKY);
    }

    @Override
    public void assign()
    {
        Kore.fastBreak = this;
    }
}