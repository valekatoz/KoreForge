package net.kore.utils.rotation;

import net.minecraft.util.*;

public class Rotation
{
    private float yaw;
    private float pitch;

    public Rotation(final float yaw, final float pitch) {
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Rotation setPitch(final float pitch) {
        this.pitch = pitch;
        return this;
    }

    public Rotation setYaw(final float yaw) {
        this.yaw = yaw;
        return this;
    }

    public Rotation wrap() {
        this.yaw = MathHelper.wrapAngleTo180_float(this.yaw);
        this.pitch = MathHelper.wrapAngleTo180_float(this.pitch);
        return this;
    }
}