package net.kore.commands;

import net.kore.Kore;
import net.minecraft.util.ChatComponentText;

public abstract class Command {
    public String[] names;
    public Command(String... name)
    {
        names = name;
    }
    public String[] getNames()
    {
        return names;
    }

    public abstract void execute(String[] args) throws Exception;

    public String getName()
    {
        return names[0];
    }

    public abstract String getDescription();

    public static void sendMessage(String message)
    {
        Kore.mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
