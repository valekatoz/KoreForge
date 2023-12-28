package katoz.kore.config;

import cc.polyfrost.oneconfig.config.annotations.*;
import katoz.kore.Kore;
import katoz.kore.modules.render.Watermark;
import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.ModType;

import java.util.Random;

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See <a href="https://docs.polyfrost.cc/oneconfig/config/adding-options">this link</a> for more config Options
 */

public class KoreConfig extends Config {

    // CATEGORIES
    private transient static final String RENDER = "Render";
    private transient static final String PLAYER = "Player";
    private transient static final String OTHER = "Other";

    // SUB-CATEGORIES
    private transient static final String WATERMARK = "Watermark";
    private transient static final String AUTO_HARP = "Auto Harp";
    private transient static final String AUTO_EXPERIMENTS = "Auto Experiments";
    private transient static final String PROTECTIONS = "Protections";
    private transient static final String DEVELOPMENT = "Developer Mode";

    @HUD(
            name = "Enabled",
            category = RENDER,
            subcategory = WATERMARK
    )
    public Watermark hud = new Watermark();

    @Switch(
            name = "Enabled",
            category = PLAYER,
            subcategory = AUTO_HARP,
            size = 2
    )
    public static boolean autoHarp = false;

    @Slider(
            name = "Click delay (Milliseconds)",
            description = "Change this slider based on song speed and ping",
            category = PLAYER,
            subcategory = AUTO_HARP,
            min = 0, max = 1000
    )
    public static int autoHarpDelay = 150;

    @Switch(
            name = "Enabled",
            category = PLAYER,
            subcategory = AUTO_EXPERIMENTS
    )
    public static boolean autoExperiments = false;

    @Button(
            name = "Randomize click delay within the suggested range",
            text = "Randomize",
            category = PLAYER,
            subcategory = AUTO_EXPERIMENTS
    )
    Runnable runnable = () -> {
        Random rand = new Random(System.currentTimeMillis());
        autoExperimentsDelay = rand.nextInt(18)+8; // 8 -> 25
    };


    @Slider(
            name = "Click delay (Ticks)",
            description = "Each click is delay by ticks, 20 ticks = 1 second",
            category = PLAYER,
            subcategory = AUTO_EXPERIMENTS,
            min = 0, max = 30
    )
    public static int autoExperimentsDelay = 8;

    @Info(
            text = "If you don't disable external mods solvers the ui might bug out",
            category = PLAYER,
            subcategory = AUTO_EXPERIMENTS,
            type = InfoType.WARNING,
            size = 2
    )
    public static boolean enchantingSolversInfoIgnored;
    @Switch(
            name = "Chronomatron Solver",
            description = "Automatically solves the Chronomatron challenge",
            category = PLAYER,
            subcategory = AUTO_EXPERIMENTS
    )
    public static boolean chronomatronSolver = false;

    @Switch(
            name = "Ultrasequencer Solver",
            description = "Automatically solves the Ultrasequencer challenge",
            category = PLAYER,
            subcategory = AUTO_EXPERIMENTS
    )
    public static boolean ultrasequencerSolver = false;

    @Switch(
            name = "Enable",
            description = "Enable or disable developer mode (useful for debugging)",
            category = OTHER,
            subcategory = DEVELOPMENT
    )
    public static boolean devMode = false;

    @Switch(
            name = "Mod Hider",
            description = "Hides the mod id from the mods list",
            category = OTHER,
            subcategory = PROTECTIONS
    )
    public static boolean modHider = true;

    public KoreConfig() {
        super(new Mod(Kore.NAME, ModType.SKYBLOCK, "/assets/logo.png",84,84), Kore.MODID + ".json");
        initialize();
    }
}

