package net.kore.modules.protection;

import net.kore.Kore;
import net.kore.modules.Module;

public class ModHider extends Module {
    public ModHider()
    {
        super("Mod Hider", Category.PROTECTIONS);
        setToggled(true);
    }

    @Override
    public void assign()
    {
        Kore.modHider = this;
    }
}
