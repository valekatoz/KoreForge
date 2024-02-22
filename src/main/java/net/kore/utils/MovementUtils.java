package net.kore.utils;

import net.kore.Kore;
import net.kore.events.MoveEvent;
import net.kore.modules.combat.KillAura;
import net.kore.utils.rotation.RotationUtils;
import net.minecraft.entity.Entity;

public class MovementUtils
{
    public static MilliTimer strafeTimer;

    public static float getSpeed() {
        return (float)Math.sqrt(Kore.mc.thePlayer.motionX * Kore.mc.thePlayer.motionX + Kore.mc.thePlayer.motionZ * Kore.mc.thePlayer.motionZ);
    }

    public static float getSpeed(final double x, final double z) {
        return (float)Math.sqrt(x * x + z * z);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    public static boolean isMoving() {
        return Kore.mc.thePlayer.moveForward != 0.0f || Kore.mc.thePlayer.moveStrafing != 0.0f;
    }

    public static boolean hasMotion() {
        return Kore.mc.thePlayer.motionX != 0.0 && Kore.mc.thePlayer.motionZ != 0.0 && Kore.mc.thePlayer.motionY != 0.0;
    }

    public static boolean isOnGround(final double height) {
        return !Kore.mc.theWorld.getCollidingBoundingBoxes((Entity)Kore.mc.thePlayer, Kore.mc.thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }

    public static void strafe(final double speed) {
        if (!isMoving()) {
            return;
        }
        final double yaw = getDirection();
        Kore.mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        Kore.mc.thePlayer.motionZ = Math.cos(yaw) * speed;
        MovementUtils.strafeTimer.reset();
    }

    public static void bhop(double s) {
        double forward = Kore.mc.thePlayer.movementInput.moveForward;
        double strafe = Kore.mc.thePlayer.movementInput.moveStrafe;
        float yaw = Kore.mc.thePlayer.rotationYaw;

        if ((forward == 0.0D) && (strafe == 0.0D)) {
            Kore.mc.thePlayer.motionX = 0.0D;
            Kore.mc.thePlayer.motionZ = 0.0D;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D)
                    yaw += (float) (forward > 0.0D ? -45 : 45);
                else if (strafe < 0.0D)
                    yaw += (float) (forward > 0.0D ? 45 : -45);

                strafe = 0.0D;
                if (forward > 0.0D)
                    forward = 1.0D;
                else if (forward < 0.0D)
                    forward = -1.0D;
            }

            double rad = Math.toRadians(yaw + 90.0F);
            double sin = Math.sin(rad);
            double cos = Math.cos(rad);
            Kore.mc.thePlayer.motionX = (forward * s * cos) + (strafe * s * sin);
            Kore.mc.thePlayer.motionZ = (forward * s * sin) - (strafe * s * cos);
        }

    }

    public static void strafe(final float speed, final float yaw) {
        if (!isMoving() || !MovementUtils.strafeTimer.hasTimePassed(150L)) {
            return;
        }
        Kore.mc.thePlayer.motionX = -Math.sin(Math.toRadians(yaw)) * speed;
        Kore.mc.thePlayer.motionZ = Math.cos(Math.toRadians(yaw)) * speed;
        MovementUtils.strafeTimer.reset();
    }

    public static void forward(final double length) {
        final double yaw = Math.toRadians(Kore.mc.thePlayer.rotationYaw);
        Kore.mc.thePlayer.setPosition(Kore.mc.thePlayer.posX + -Math.sin(yaw) * length, Kore.mc.thePlayer.posY, Kore.mc.thePlayer.posZ + Math.cos(yaw) * length);
    }

    public static double getDirection() {
        return Math.toRadians(getYaw());
    }

    public static void setMotion(final MoveEvent em, final double speed) {
        double forward = Kore.mc.thePlayer.movementInput.moveForward;
        double strafe = Kore.mc.thePlayer.movementInput.moveStrafe;
        float yaw = ((KillAura.target != null && Kore.killAura.movementFix.isEnabled())) ? RotationUtils.getRotations(KillAura.target).getYaw() : Kore.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            Kore.mc.thePlayer.motionX = 0.0;
            Kore.mc.thePlayer.motionZ = 0.0;
            if (em != null) {
                em.setX(0.0);
                em.setZ(0.0);
            }
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
            final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
            Kore.mc.thePlayer.motionX = forward * speed * cos + strafe * speed * sin;
            Kore.mc.thePlayer.motionZ = forward * speed * sin - strafe * speed * cos;
            if (em != null) {
                em.setX(Kore.mc.thePlayer.motionX);
                em.setZ(Kore.mc.thePlayer.motionZ);
            }
        }
    }

    public static float getYaw() {
        float yaw = Kore.mc.thePlayer.rotationYaw;
        if (Kore.mc.thePlayer.moveForward < 0.0f) {
            yaw += 180.0f;
        }
        float forward = 1.0f;
        if (Kore.mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        }
        else if (Kore.mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }
        if (Kore.mc.thePlayer.moveStrafing > 0.0f) {
            yaw -= 90.0f * forward;
        }
        if (Kore.mc.thePlayer.moveStrafing < 0.0f) {
            yaw += 90.0f * forward;
        }
        return yaw;
    }

    static {
        MovementUtils.strafeTimer = new MilliTimer();
    }
}
