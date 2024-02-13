package net.kore.modules.protection;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.StringSetting;

public class NickHider extends Module {
    public StringSetting nick;
    public NickHider()
    {
        super("Nick Hider", Category.PROTECTIONS);
        this.nick = new StringSetting("Name", Kore.mc.getSession().getUsername());
        addSettings(nick);
    }

    @Override
    public void assign()
    {
        Kore.nickHider = this;
    }

    @Override
    public String getSuffix()
    {
        return nick.getValue();
    }
}
