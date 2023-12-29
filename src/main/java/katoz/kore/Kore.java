package katoz.kore;

import cc.polyfrost.oneconfig.events.EventManager;
import katoz.kore.command.MainCommand;
import katoz.kore.config.KoreConfig;
import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import katoz.kore.events.MillisecondEvent;
import katoz.kore.events.SecondEvent;
import katoz.kore.modules.player.AutoExperiments;
import katoz.kore.modules.player.AutoHarp;
import katoz.kore.utils.LocationUtils;
import katoz.kore.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The entrypoint of Kore that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */

@Mod(modid = Kore.MODID, name = Kore.NAME, version = Kore.VERSION)
public class Kore {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    @Mod.Instance(MODID)
    public static Kore INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static KoreConfig config;
    public static Minecraft mc = Minecraft.getMinecraft();
    private final ArrayList<Object> modules = new ArrayList<>();
    private boolean login = false;
    private boolean failedCreatingConfig = false;

    public Kore() {
        Collections.addAll(modules,
                new LocationUtils(),
                new AutoExperiments(),
                new AutoHarp()
        );
    }

    @Mod.EventHandler
    public void preFMLInitialization(FMLPreInitializationEvent event) {
        File configDirectory = new File(event.getModConfigurationDirectory(), MODID);
        if (!configDirectory.exists() && !configDirectory.mkdir()) {
            failedCreatingConfig = true;
        }
    }

    // Register the config and commands.
    @Mod.EventHandler
    public void onFMLInitialization(FMLInitializationEvent event) {
        config = new KoreConfig();
        registerCommand(new MainCommand());
        registerModule(this);
        modules.forEach(this::registerModule);
    }

    @Mod.EventHandler
    public void postFMLInitialization(FMLPostInitializationEvent event) {
        LocalDateTime now = LocalDateTime.now();
        Duration initialDelay = Duration.between(now, now);
        long initialDelaySeconds = initialDelay.getSeconds();

        ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(1);
        threadPool.scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new SecondEvent()), initialDelaySeconds, 1, TimeUnit.SECONDS);
        threadPool.scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new MillisecondEvent()), initialDelaySeconds, 1, TimeUnit.MILLISECONDS);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || login) return;
        login = true;
        initialize();
    }

    private void initialize() {
        if (failedCreatingConfig) {
            ModUtils.sendMessage("Failed creating a config directory, some configuration options will not persist!");
        }
    }

    private void registerModule(Object obj) {
        MinecraftForge.EVENT_BUS.register(obj);
        EventManager.INSTANCE.register(obj);
    }

    private void registerCommand(Object obj) {
        CommandManager.INSTANCE.registerCommand(obj);
    }
}
