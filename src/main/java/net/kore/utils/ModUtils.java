package net.kore.utils;

import net.kore.Kore;
import net.minecraft.util.ChatComponentText;

public class ModUtils {
    public static void sendMessage(Object object) {
        String message = "null";
        if (object != null) {
            message = object.toString().replace("&", ""+Kore.fancy);
        }
        if (Kore.mc.thePlayer != null) {
            Kore.mc.thePlayer.addChatMessage(new ChatComponentText(Kore.fancy + "7[" + Kore.fancy + "c" + Kore.MOD_NAME + Kore.fancy + "7] " + Kore.fancy + "f" + message)); // §7[§cKORE§7] §f
        }
    }
}