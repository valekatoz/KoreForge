package net.kore.events;

import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.entity.*;

@Cancelable
public class RenderEntityEvent extends Event
{
    public EntityLivingBase entity;
    public float limbSwing;
    public float limbSwingAmount;
    public float rotationFloat;
    public float rotationYaw;
    public float rotationPitch;

    public RenderEntityEvent(final EntityLivingBase entity, final float limbSwing, final float limbSwingAmount, final float rotationFloat, final float rotationYaw, final float rotationPitch) {
        this.entity = entity;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.rotationFloat = rotationFloat;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
    }
}
