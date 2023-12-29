package katoz.kore.command;

import katoz.kore.Kore;
import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;

@Command(value = Kore.MODID, description = "Access the " + Kore.NAME + " GUI.")
public class MainCommand {
    @Main
    private void handle() {
        Kore.INSTANCE.config.openGui();
    }
}