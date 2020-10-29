package me.dreamhopping.mediamod;

import me.dreamhopping.mediamod.gui.GuiMediaPlayerManager;
import me.dreamhopping.mediamod.gui.PlayerOverlay;
import me.dreamhopping.mediamod.keybinds.KeybindInputHandler;
import me.dreamhopping.mediamod.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import me.dreamhopping.mediamod.command.MediaModCommand;
import me.dreamhopping.mediamod.command.MediaModUpdateCommand;
import me.dreamhopping.mediamod.config.Settings;
import me.dreamhopping.mediamod.event.MediaInfoUpdateEvent;
import me.dreamhopping.mediamod.keybinds.KeybindManager;
import me.dreamhopping.mediamod.media.MediaHandler;
import me.dreamhopping.mediamod.media.core.api.MediaInfo;

import java.io.File;

/**
 * The main class for MediaMod
 *
 * @author ConorTheDev
 * @see net.minecraftforge.fml.common.Mod
 */
@SuppressWarnings("unused")
@Mod(name = Metadata.NAME, modid = Metadata.MODID, version = Metadata.VERSION)
public class MediaMod {
    /**
     * The API Endpoint for MediaMod requests
     */
    public static final String ENDPOINT = "https://mediamod-serverless.dreamhopping.vercel.app/";

    /**
     * An instance of this class to access non-static methods from other classes
     */
    @Mod.Instance(Metadata.MODID)
    public static MediaMod INSTANCE;

    /**
     * Logger used to log info messages, debug messages, error messages & more
     *
     * @see org.apache.logging.log4j.Logger
     */
    public final Logger logger = LogManager.getLogger("MediaMod");
    /**
     * A File which points to the MediaMod Data directory
     */
    public File mediamodDirectory;
    /**
     * A file that points to the MediaMod Themes Data directory
     */

    public File mediamodThemeDirectory;
    /**
     * If this is the first load of MediaMod
     */
    private boolean firstLoad = true;

    /**
     * Fired before Minecraft starts
     *
     * @param event - FMLPreInitializationEvent
     * @see FMLPreInitializationEvent
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        KeybindManager.INSTANCE.register();

        MinecraftForge.EVENT_BUS.register(new KeybindInputHandler());
        MinecraftForge.EVENT_BUS.register(GuiMediaPlayerManager.instance);
    }

    /**
     * Fired when Minecraft is starting
     *
     * @param event - FMLInitializationEvent
     * @see FMLInitializationEvent
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("MediaMod starting...");

        // Register event subscribers and commands
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PlayerOverlay.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PlayerMessenger.INSTANCE);

        ClientCommandHandler.instance.registerCommand(new MediaModCommand());
        ClientCommandHandler.instance.registerCommand(new MediaModUpdateCommand());

        mediamodDirectory = new File(FMLClientHandler.instance().getClient().mcDataDir, "mediamod");
        mediamodThemeDirectory = new File(mediamodDirectory, "themes");

        if (!mediamodDirectory.exists()) {
            logger.info("Creating mediamod directory...");
            boolean mkdir = mediamodDirectory.mkdir();

            if (mkdir) {
                logger.info("Created directory!");
            } else {
                logger.fatal("Failed to create mediamod directory");
            }
        }

        // Remove theming for this beta build because it's nowhere near ready for the betas
        /*if (!mediamodThemeDirectory.exists()) {
            boolean createdThemeDirectory = mediamodThemeDirectory.mkdir();
            if (createdThemeDirectory) {
                logger.info("Created theme directory!");
                File defaultThemeFile = new File(mediamodThemeDirectory, "default.toml");
                try {
                    if (!defaultThemeFile.exists()) {
                        if (defaultThemeFile.createNewFile()) {
                            logger.info("Created default theme file");

                            String defaultFile =
                                    "[metadata]\n" +
                                            "name = \"Default\"\n" +
                                            "version = 1.0\n" +
                                            "\n" +
                                            "[colours]\n" +
                                            "textRed = 255\n" +
                                            "textGreen = 255\n" +
                                            "textBlue = 255\n" +
                                            "playerRed = 0\n" +
                                            "playerGreen = 0\n" +
                                            "playerBlue = 0\n";

                            FileWriter writer = new FileWriter(defaultThemeFile);
                            writer.append(defaultFile);
                            writer.close();
                        }
                    } else {
                        logger.error("Failed to create default theme file");
                    }
                } catch (IOException e) {
                    logger.error("Failed to initialise themes! Error: ", e);
                }
            } else {
                logger.error("Failed to create theme directory");
            }
        }*/

        logger.info("Checking if MediaMod is up-to-date...");
        VersionChecker.checkVersion();

        logger.info("Loading Configuration...");
        Settings.loadConfig();

        Multithreading.runAsync(() -> {
            // Load services
            MediaHandler mediaHandler = MediaHandler.instance;
            mediaHandler.loadAll();
        });

        Runtime.getRuntime().addShutdownHook(new Thread("MediaMod Shutdown Thread") {
            public void run() {
                logger.info("Shutting down MediaMod");
                new UpdaterUtility().performUpdate();
            }
        });
    }

    /**
     * Fired when the world fires a tick
     *
     * @param event WorldTickEvent
     * @see net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (firstLoad && Minecraft.getMinecraft().player != null) {
            if (!VersionChecker.INSTANCE.isLatestVersion && !Settings.ALWAYS_AUTOUPDATE) {
                PlayerMessenger.sendMessage(ChatColor.RED + "MediaMod is out of date!", true);
                PlayerMessenger.sendMessage(ChatColor.GRAY + "Latest Version: " + ChatColor.WHITE + VersionChecker.INSTANCE.latestVersionInformation.name);
                PlayerMessenger.sendMessage(ChatColor.GRAY + "Your Version: " + ChatColor.WHITE + Metadata.VERSION);
                PlayerMessenger.sendMessage(ChatColor.GRAY + "Changelog: " + ChatColor.WHITE + VersionChecker.INSTANCE.latestVersionInformation.changelog);

                TextComponentString urlComponent = new TextComponentString(ChatColor.GRAY + "Click this to automatically update now!");
                urlComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "mediamodupdate"));
                urlComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(ChatColor.translateAlternateColorCodes('&',
                        "&7Runs /mmupdate"))));
                PlayerMessenger.sendMessage(urlComponent);
            } else if (Settings.ALWAYS_AUTOUPDATE && !VersionChecker.INSTANCE.isLatestVersion) {
                Multithreading.runAsync(() -> {
                    UpdaterUtility utility = new UpdaterUtility();
                    utility.scheduleUpdate();
                });
            }

            firstLoad = false;
        }
    }


    /**
     * Fired when the current song information changes
     *
     * @see MediaInfoUpdateEvent
     */
    @SubscribeEvent
    public void onMediaInfoChange(MediaInfoUpdateEvent event) {
        MediaInfo info = event.mediaInfo;
        if (info == null) return;

        if (Settings.ANNOUNCE_TRACKS) {
            PlayerMessenger.sendMessage(ChatColor.GRAY + "Current track: " + info.track.name + " by " + info.track.artists[0].name, true);
        }
    }

}