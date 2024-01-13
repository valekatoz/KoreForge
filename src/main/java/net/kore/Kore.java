package net.kore;

import net.kore.events.JoinGameEvent;
import net.kore.managers.*;
import net.kore.modules.ClientSettings;
import net.kore.modules.Module;
import net.kore.modules.combat.AimAssist;
import net.kore.modules.combat.AntiBot;
import net.kore.modules.misc.GhostBlocks;
import net.kore.modules.misc.MurderFinder;
import net.kore.modules.skyblock.PurseSpoofer;
import net.kore.modules.player.*;
import net.kore.modules.protection.*;
import net.kore.modules.render.*;
import net.kore.modules.skyblock.AutoExperiments;
import net.kore.modules.skyblock.AutoHarp;
import net.kore.utils.Notification;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.BlurUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod(modid = Kore.MOD_ID, name = Kore.MOD_NAME, version = Kore.VERSION)
public class Kore {
    public static final String MOD_ID = "@ID@";
    public static final String MOD_NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    // Managers
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static ThemeManager themeManager;
    public static NotificationManager notificationManager;

    // Variables
    public static Minecraft mc;
    public static List<String> changelog = new ArrayList<>();
    public static char fancy = (char) 167;

    // (Important) Modules
    public static Gui clickGui;
    public static ClientSettings clientSettings;

    // Modules
    public static PopupAnimation popupAnimation;
    public static Interfaces interfaces;
    public static InventoryDisplay inventoryDisplay;
    public static Animations animations;
    public static Giants giants;
    public static ChinaHat chinaHat;
    public static PlayerEsp playerEsp;
    public static PurseSpoofer purseSpoofer;
    public static Nametags nametags;
    public static ModHider modHider;
    public static NickHider nickHider;
    public static StaffAnalyser staffAnalyser;
    public static Proxy proxy;
    public static FreeCam freeCam;
    public static FastPlace fastPlace;
    public static FastBreak fastBreak;
    public static GuiMove guiMove;
    public static GhostBlocks ghostBlock;
    public static AutoExperiments autoExperiments;
    public static AutoHarp autoHarp;
    public static MurderFinder murderFinder;
    public static AntiBot antiBot;
    public static AimAssist aimAssist;

    public static void start()
    {
        Kore.mc = Minecraft.getMinecraft();

        moduleManager = new ModuleManager("net.kore.modules");

        moduleManager.initReflection();

        configManager = new ConfigManager();

        themeManager = new ThemeManager();

        notificationManager = new NotificationManager();

        CommandManager.init();

        loadChangelog();

        for (Module module : moduleManager.modules)
        {
            MinecraftForge.EVENT_BUS.register(module);
        }

        BlurUtils.registerListener();
    }

    public static void handleKey(int key)
    {
        for (Module module : moduleManager.modules)
        {
            if (module.getKeycode() == key)
            {
                module.toggle();
                if (!clickGui.disableNotifs.isEnabled() && !module.getName().equals("Ghost Blocks"))
                    notificationManager.showNotification((module.isToggled() ? "Enabled" : "Disabled") + " " + module.getName(), 2000, Notification.NotificationType.INFO);
            }
        }
    }

    public static void loadChangelog()
    {
        URL url2 = null;
        BufferedReader reader = null;

        // Tries fetching the home from server
        try {
            url2 = new URL("https://kore.valekatoz.com/home.txt");

            HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
            connection.setRequestProperty("User-Agent", "Kore/"+Kore.VERSION); // custom UserAgent to skip cloudflare managed challenge

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                changelog.add(line);
            }
        } catch (IOException e) {
            if(Kore.clientSettings.debug.isEnabled()) {
                e.printStackTrace();
            }

            // If fetching the home from server failed we fetch it from local file
            try {
                reader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("Kore", "home.txt")).getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    changelog.add(line);
                }

                reader.close();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    @Mod.EventHandler
    public void startForge(FMLPreInitializationEvent pre)
    {

    }

    @Mod.EventHandler
    public void startLate(FMLInitializationEvent event)
    {

    }

    public static void sendMessage(Object object) {
        if (Kore.mc.thePlayer != null)
        {
            mc.thePlayer.addChatMessage(new ChatComponentText(object.toString()));
        }
    }

    public static void sendMessageWithPrefix(Object object) {
        if (Kore.mc.thePlayer != null)
        {
            Kore.mc.thePlayer.addChatMessage(new ChatComponentText(Kore.fancy + "7[" + Kore.fancy + "q" + Kore.MOD_NAME + Kore.fancy + "7] " + Kore.fancy + "f" + object.toString().replaceAll("&", Kore.fancy + "")));
        }
    }
}
