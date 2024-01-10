package net.kore.events;

import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PostGuiOpenEvent extends Event
{
    public Gui gui;

    public PostGuiOpenEvent(final Gui gui) {
        this.gui = gui;
    }
}
