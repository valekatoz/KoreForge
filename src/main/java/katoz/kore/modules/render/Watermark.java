package katoz.kore.modules.render;

import katoz.kore.Kore;
import katoz.kore.utils.hud.WatermarkHud;

public class Watermark extends WatermarkHud {
    public Watermark() {
        super(Kore.NAME, true);
    }
    @Override
    public String getText(boolean example) {
        return "(" + Kore.VERSION +  ")";
    }
}
