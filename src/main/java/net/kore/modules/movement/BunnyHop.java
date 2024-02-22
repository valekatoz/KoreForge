package net.kore.modules.movement;

import net.kore.Kore;
import net.kore.events.MotionUpdateEvent;
import net.kore.modules.Module;
import net.kore.settings.BooleanSetting;
import net.kore.settings.NumberSetting;
import net.kore.utils.MovementUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BunnyHop extends Module {
    public NumberSetting speed;
    public BooleanSetting fastFall;

    public BunnyHop() {
        super("Bunny Hop", Category.MOVEMENT);
        this.speed = new NumberSetting("Speed", 2, 1, 10, 0.2);
        this.fastFall = new BooleanSetting("Fast Fall", false);
        this.addSettings(this.speed, this.fastFall);
        this.setFlagType(FlagType.DETECTED);
    }

    @Override
    public void assign()
    {
        Kore.bunnyHop = this;
    }

    @SubscribeEvent
    public void onMoveInput(MotionUpdateEvent e) {
        if(!this.isToggled()) return;

        if (MovementUtils.isMoving() && !Kore.mc.thePlayer.isInWater()) {
            if (Kore.mc.thePlayer.onGround) {
                Kore.mc.thePlayer.jump();
            }

            if (fastFall.isEnabled()) {
                if (Kore.mc.thePlayer.fallDistance < 2 && Kore.mc.thePlayer.fallDistance > 0) {
                    Kore.mc.thePlayer.motionY *= 1.5;
                }
            }

            Kore.mc.thePlayer.setSprinting(true);
            double spd = 0.01D * speed.getValue();
            double m = (float)(Math.sqrt(Kore.mc.thePlayer.motionX * Kore.mc.thePlayer.motionX + Kore.mc.thePlayer.motionZ * Kore.mc.thePlayer.motionZ) + spd);
            MovementUtils.bhop(m);
        }
    }
}