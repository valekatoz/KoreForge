package net.kore.modules.movement;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;

public class Sprint extends Module
{
    public BooleanSetting omni;
    public BooleanSetting keep;

    public Sprint() {
        super("Sprint", 0, Category.MOVEMENT);
        this.omni = new BooleanSetting("OmniSprint", true);
        this.keep = new BooleanSetting("KeepSprint", true);
        this.addSettings(this.keep, this.omni);
    }

    @Override
    public void assign()
    {
        Kore.sprint = this;
    }
}
