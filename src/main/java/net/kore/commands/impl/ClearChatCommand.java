package net.kore.commands.impl;

import net.kore.Kore;
import net.kore.commands.Command;

public class ClearChatCommand extends Command {
    public ClearChatCommand()
    {
        super("clear");
    }
    @Override
    public void execute(String[] args) throws Exception {
        if (args.length < 2) {
            Kore.sendMessageWithPrefix("Invalid command!");
            return;
        }

        for(int i = 0; i < Integer.parseInt(args[1]); i++) {
            Kore.sendMessage("");
        }
    }

    @Override
    public String getDescription() {
        return ".clear <lines>";
    }
}