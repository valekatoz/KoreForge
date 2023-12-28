package katoz.kore.modules.render;

import katoz.kore.Kore;
import katoz.kore.utils.hud.WatermarkHud;
import katoz.kore.config.KoreConfig;

/**
 * An example OneConfig HUD that is started in the config and displays text.
 *
 * @see KoreConfig#hud
 */
public class Watermark extends WatermarkHud {
    public Watermark() {
        super("Kore", true);
    }

    @Override
    public String getText(boolean example) {
        return "(" + Kore.VERSION +  ")";
    }
}
