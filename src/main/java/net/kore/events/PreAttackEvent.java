package net.kore.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PreAttackEvent extends Event
{
    public Entity entity;

    public PreAttackEvent(final Entity entity) {
        this.entity = entity;
    }
}