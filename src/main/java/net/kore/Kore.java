package net.kore;

import net.kore.managers.*;
import net.kore.modules.ClientSettings;
import net.kore.modules.Module;
import net.kore.modules.combat.*;
import net.kore.modules.misc.*;
import net.kore.modules.skyblock.*;
import net.kore.modules.player.*;
import net.kore.modules.protection.*;
import net.kore.modules.render.*;
import net.kore.utils.Notification;
import net.kore.utils.api.ServerUtils;
import net.kore.utils.font.Fonts;
import net.kore.utils.render.shader.BlurUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = Kore.MOD_ID, name = Kore.MOD_NAME, version = Kore.VERSION)
public class Kore {
    public static final String MOD_ID = "@ID@";
    public static final String MOD_NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final String VERSION_NUMBER = "@VER_NUM@";

    // Managers
    public static LicenseManager licenseManager;
    public static ModuleManager moduleManager;
    public static ConfigManager configManager;
    public static ThemeManager themeManager;
    public static NotificationManager notificationManager;

    // Variables
    public static Minecraft mc;
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

        licenseManager = new LicenseManager();

        moduleManager = new ModuleManager("net.kore.modules");

        moduleManager.initReflection();

        configManager = new ConfigManager();

        themeManager = new ThemeManager();

        notificationManager = new NotificationManager();

        CommandManager.init();

        ServerUtils.loadChangelog();

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
                if (!clickGui.disableNotifs.isEnabled())
                    notificationManager.showNotification((module.isToggled() ? "Enabled" : "Disabled") + " " + module.getName(), 2000, Notification.NotificationType.INFO);
            }
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent pre) {
        MinecraftForge.EVENT_BUS.register(this);
        Fonts.bootstrap();
        Kore.start();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!licenseManager.hasConnected() && event.entity instanceof net.minecraft.client.entity.EntityPlayerSP) {
            licenseManager.setConnected(true);

            if(licenseManager.isPremium()) {
                sendMessageWithPrefix("You successfully authenticated to Kore (Premium)");
            } else {
                sendMessageWithPrefix("Looks like you are not premium. You should consider upgrading to premium for the best features.");
            }
        }
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
