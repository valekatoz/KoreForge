package net.kore.modules.player;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.NumberSetting;

public class FastPlace extends Module {
    public NumberSetting placeDelay;
    public FastPlace()
    {
        super("Fast Place", Category.PLAYER);

        this.addSettings(this.placeDelay = new NumberSetting("Place delay", 2.0, 0.0, 4.0, 1.0));
    }

    @Override
    public void assign()
    {
        Kore.fastPlace = this;
    }
}