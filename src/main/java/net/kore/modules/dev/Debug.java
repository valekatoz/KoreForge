package net.kore.modules.dev;

import net.kore.Kore;
import net.kore.modules.Module;

public class Debug extends Module {
    public Debug()
    {
        super("Debug Mode", Category.DEV);

        setToggled(false);
    }

    @Override
    public void assign()
    {
        Kore.Debug = this;
    }
}