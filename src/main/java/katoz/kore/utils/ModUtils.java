package katoz.kore.utils;

import katoz.kore.Kore;
import net.minecraft.util.ChatComponentText;

public class ModUtils {
    public static void sendMessage(Object object) {
        String message = "null";
        if (object != null) {
            message = object.toString().replace("&", "§");
        }
        if (Kore.mc.thePlayer != null) {
            Kore.mc.thePlayer.addChatMessage(new ChatComponentText("§7[§c" + Kore.NAME + "§7] §f" + message));
        }
    }
}
