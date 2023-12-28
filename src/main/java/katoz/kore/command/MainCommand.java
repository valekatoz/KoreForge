package katoz.kore.command;

import katoz.kore.Kore;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

/**
 * An example command implementing the Command api of OneConfig.
 * Registered in Kore.java with `CommandManager.INSTANCE.registerCommand(new MainCommand());`
 *
 * @see Command
 * @see Main
 * @see Kore
 */
@Command(value = Kore.MODID, description = "Access the " + Kore.NAME + " GUI.")
public class MainCommand {
    @Main
    private void handle() {
        Kore.INSTANCE.config.openGui();
    }
}