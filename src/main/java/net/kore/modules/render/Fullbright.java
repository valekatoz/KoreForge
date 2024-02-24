package net.kore.modules.render;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.ModeSetting;

public class Fullbright extends Module {

    private float originalGamma;

    public Fullbright() {
        super("Fullbright", Category.RENDER);
    }

    @Override
    public void assign()
    {
        Kore.fullbright = this;
    }

    @Override
    public void onEnable() {
        originalGamma = Kore.mc.gameSettings.gammaSetting;
        Kore.mc.gameSettings.gammaSetting = 100;
    }

    @Override
    public void onDisable() {
        Kore.mc.gameSettings.gammaSetting = originalGamma > 10 ? 1 : originalGamma;
        if(Kore.clientSettings.debug.isEnabled()) {
            Kore.sendMessageWithPrefix("" + Kore.mc.gameSettings.gammaSetting);
        }
    }
}