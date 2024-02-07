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
            Kore.sendMessageWithPrefix(".kore <help/check/disconnect>");
            return;
        }

        if(args.length > 1 && args[1].equals("help")) {
            CommandManager.printHelp();

        } else if(args.length > 1 && args[1].equals("check")) {
            if(Kore.updateManager.checkUpdate()) {
                Kore.sendMessageWithPrefix("(&cUpdater&f) You are not running the latest version");
            } else {
                Kore.sendMessageWithPrefix("(&cUpdater&f) You are running the latest version");
            }

        } else if(args.length > 1 && args[1].equals("disconnect")) {
            if(Kore.licenseManager.disconnect()) {
                Kore.sendMessageWithPrefix("(&cLicense&f) You successfully disconnected from Kore");
            } else {
                Kore.sendMessageWithPrefix("(&cLicense&f) You are in the unlicensed version.");
            }

        } else {
            Kore.sendMessageWithPrefix("(&cDiscord&f) -> https://discord.com/invite/H4x6eFp9KR");
        }
    }

    @Override
    public String getDescription() {
        return ".kore <help/update/disconnect>";
    }
}