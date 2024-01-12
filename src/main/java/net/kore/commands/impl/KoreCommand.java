package net.kore.commands.impl;

import net.kore.Kore;
import net.kore.commands.Command;
import net.kore.managers.CommandManager;
import net.kore.utils.Notification;

public class KoreCommand extends Command {
    public KoreCommand()
    {
        super("kore");
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length > 2)
        {
            Kore.sendMessage(".kore <help/dev>");
            return;
        }

        if(args.length > 1 && args[1].equals("help")) {
            CommandManager.printHelp();
        } else if(args.length > 1 && args[1].equals("dev")) {
            // Dev test command
            Kore.notificationManager.showNotification("This is a notification", 2000, Notification.NotificationType.INFO);
            Kore.sendMessage("(Dev) Notification executed");
        } else {
            Kore.sendMessage("(Dev) Gui should have opened");
            Kore.clickGui.toggle();
            Kore.clickGui.onEnable();
        }
    }

    @Override
    public String getDescription() {
        return "koreclient main command -> .kore <help/dev>";
    }
}