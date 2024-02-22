package net.kore.modules.combat;

import net.kore.Kore;
import net.kore.modules.Module;
import net.kore.settings.ModeSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.MathUtils;
import net.kore.utils.MilliTimer;
import net.kore.utils.PlayerUtils;
import net.kore.utils.SkyblockUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class AutoClicker extends Module
{
    public static final NumberSetting maxCps;
    public static final NumberSetting minCps;
    public static final ModeSetting mode;
    private MilliTimer timer;
    private double nextDelay;

    public AutoClicker() {
        super("Auto Clicker", Category.COMBAT);
        this.timer = new MilliTimer();
        this.nextDelay = 10.0;
        this.setFlagType(FlagType.DETECTED);
        this.addSettings(AutoClicker.minCps, AutoClicker.maxCps, AutoClicker.mode);
    }

    @Override
    public void assign()
    {
        Kore.autoClicker = this;
    }

    @SubscribeEvent
    public void onTick(final RenderWorldLastEvent event) {
        if (this.isToggled() && Kore.mc.thePlayer != null && this.isPressed() && !Kore.mc.thePlayer.isUsingItem() && Kore.mc.currentScreen == null && this.timer.hasTimePassed((long)(1000.0 / this.nextDelay))) {
            this.timer.reset();
            this.nextDelay = MathUtils.getRandomInRange(AutoClicker.maxCps.getValue(), AutoClicker.minCps.getValue());
            PlayerUtils.click();
        }
    }

    @Override
    public boolean isPressed() {
        final String selected = AutoClicker.mode.getSelected();
        switch (selected) {
            case "Toggle": {
                return this.isToggled();
            }
            default: {
                return Kore.mc.gameSettings.keyBindAttack.isKeyDown();
            }
        }
    }

    static {
        maxCps = new NumberSetting("Max CPS", 12.0, 1.0, 20.0, 1.0) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                AutoClicker.minCps.setMax(AutoClicker.maxCps.getValue());
                if (AutoClicker.minCps.getValue() > AutoClicker.minCps.getMax()) {
                    AutoClicker.minCps.setValue(AutoClicker.minCps.getMin());
                }
            }
        };
        minCps = new NumberSetting("Min CPS", 10.0, 1.0, 20.0, 1.0) {
            @Override
            public void setValue(final double value) {
                super.setValue(value);
                AutoClicker.maxCps.setMin(AutoClicker.minCps.getValue());
                if (AutoClicker.maxCps.getValue() < AutoClicker.maxCps.getMin()) {
                    AutoClicker.maxCps.setValue(AutoClicker.maxCps.getMin());
                }
            }
        };

        mode = new ModeSetting("Mode", "Attack held", new String[] { "Toggle", "Attack held" });
    }
}