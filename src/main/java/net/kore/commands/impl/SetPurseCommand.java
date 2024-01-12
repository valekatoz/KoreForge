package net.kore.commands.impl;

import net.kore.Kore;
import net.kore.commands.Command;

public class SetPurseCommand extends Command {
    public SetPurseCommand()
    {
        super("purse", "setpurse","setcoins");
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 2)
        {
            Kore.sendMessage("Invalid command!");
            return;
        }
        double value = Long.parseLong(args[1]);

        Kore.purseSpoofer.additionalCoins.setRawValue(value);
    }

    @Override
    public String getDescription() {
        return null;
    }
}