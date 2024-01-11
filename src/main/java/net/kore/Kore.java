package net.kore;

import net.kore.managers.*;
import net.kore.modules.Module;
import net.kore.modules.combat.AimAssist;
import net.kore.modules.combat.AntiBot;
import net.kore.modules.misc.MurderFinder;
import net.kore.modules.player.*;
import net.kore.modules.protection.*;
import net.kore.modules.render.*;
import net.kore.modules.misc.AutoExperiments;
import net.kore.modules.misc.AutoHarp;
import net.kore.modules.dev.Debug;
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

import java.io.*;
import java.util.Collections;
import java.util.List;

@Mod(modid = Kore.MOD_ID, name = Kore.MOD_NAME, version = Kore.VERSION)
public class Kore {
    public static final String MOD_ID = "@ID@";
    public static final String MOD_NAME = "@NAME@";
    public static final String VERSION = "@VER@";

    //Managers
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static ThemeManager themeManager;
    public static NotificationManager notificationManager;
    public static Minecraft mc;
    public static List<String> changelog;
    public static char fancy = (char) 167;


    // Modules
    public static Gui clickGui;
    public static PopupAnimation popupAnimation;
    public static Interfaces interfaces;
    public static InventoryDisplay inventoryDisplay;
    public static Animations animations;
    public static Giants giants;
    public static ChinaHat chinaHat;
    public static PlayerEsp playerEsp;
    public static Nametags nametags;
    public static ModHider modHider;
    public static NickHider nickHider;
    public static AntiNicker antiNicker;
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
    public static Debug Debug;

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
        changelog = Lists.newArrayList("This is a development build of Kore", "Use it and be aware we are not liable", "if you get banned.","");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("Kore", "changelog.txt")).getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                changelog.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        Fonts.bootstrap();

        start();
    }

    public static void sendMessage(String line)
    {
        mc.thePlayer.addChatMessage(new ChatComponentText(line));
    }
}
