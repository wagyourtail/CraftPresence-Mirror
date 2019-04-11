package com.gitlab.cdagaming.craftpresence;

import com.gitlab.cdagaming.craftpresence.config.ConfigHandler;
import com.gitlab.cdagaming.craftpresence.handler.CommandHandler;
import com.gitlab.cdagaming.craftpresence.handler.KeyHandler;
import com.gitlab.cdagaming.craftpresence.handler.StringHandler;
import com.gitlab.cdagaming.craftpresence.handler.SystemHandler;
import com.gitlab.cdagaming.craftpresence.handler.discord.DiscordHandler;
import com.gitlab.cdagaming.craftpresence.handler.discord.rpc.DiscordRPC;
import com.gitlab.cdagaming.craftpresence.handler.entity.EntityHandler;
import com.gitlab.cdagaming.craftpresence.handler.gui.GUIHandler;
import com.gitlab.cdagaming.craftpresence.handler.server.ServerHandler;
import com.gitlab.cdagaming.craftpresence.handler.world.BiomeHandler;
import com.gitlab.cdagaming.craftpresence.handler.world.DimensionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.File;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION_ID, clientSideOnly = true, guiFactory = Constants.GUI_FACTORY, canBeDeactivated = true, updateJSON = Constants.UPDATE_JSON, certificateFingerprint = Constants.FINGERPRINT, acceptedMinecraftVersions = "*")
public class CraftPresence {
    public static boolean packFound = false, awaitingReply = false;
    public static Minecraft instance = Minecraft.getMinecraft();
    public static EntityPlayer player = instance.player;
    public static int TIMER = 0;

    public static ConfigHandler CONFIG;
    public static SystemHandler SYSTEM = new SystemHandler();
    public static KeyHandler KEYBINDINGS = new KeyHandler();
    public static DiscordHandler CLIENT = new DiscordHandler();
    public static ServerHandler SERVER = new ServerHandler();
    public static BiomeHandler BIOMES = new BiomeHandler();
    public static DimensionHandler DIMENSIONS = new DimensionHandler();
    public static EntityHandler ENTITIES = new EntityHandler();
    public static GUIHandler GUIS = new GUIHandler();

    private boolean initialized = false;

    public CraftPresence() {
        MinecraftForge.EVENT_BUS.register(this);
        KEYBINDINGS.register();
    }

    @Mod.EventHandler
    public void disableMod(final FMLModDisabledEvent event) {
        CLIENT.shutDown();
    }

    @Mod.EventHandler
    public void onFingerprintViolation(final FMLFingerprintViolationEvent event) {
        if (!Constants.IS_DEV)
            Constants.LOG.warn(Constants.TRANSLATOR.translate("craftpresence.logger.warning.fingerprintviolation"));
    }

    public void init() {
        if (Constants.IS_DEV) {
            Constants.LOG.warn(Constants.TRANSLATOR.translate("craftpresence.logger.warning.debugmode"));
        }
        SYSTEM = new SystemHandler();
        CONFIG = new ConfigHandler(Constants.configDir + File.separator + Constants.MODID + ".properties");
        CONFIG.initialize();

        final File CP_DIR = new File(Constants.MODID);
        Constants.loadDLL(!CP_DIR.exists() || CP_DIR.listFiles() == null);

        CommandHandler.init();
        StringHandler.init();

        try {
            CLIENT.CLIENT_ID = CONFIG.clientID;
            CLIENT.setup();
            CLIENT.init();
            CLIENT.updateTimestamp();
        } catch (Exception ex) {
            Constants.LOG.error(Constants.TRANSLATOR.translate("craftpresence.logger.error.load"));
            ex.printStackTrace();
        } finally {
            initialized = true;
        }
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (!initialized && instance.currentScreen != null && (
                instance.currentScreen instanceof GuiMainMenu ||
                        instance.currentScreen.getClass().getSimpleName().equals("GuiCustom") ||
                        instance.currentScreen.getClass().getSimpleName().contains("MainMenu") || (
                        instance.currentScreen.getClass().getSuperclass() != null &&
                                instance.currentScreen.getClass().getSuperclass().getSimpleName().equals(GuiMainMenu.class.getSimpleName())))) {
            init();
        } else if (initialized) {
            CommandHandler.reloadData(false);

            if (TIMER > 0) {
                if (!SYSTEM.isTiming) {
                    SYSTEM.startTimer();
                } else {
                    SYSTEM.checkTimer();
                }
            }

            if (CONFIG.showCurrentDimension && DIMENSIONS.DIMENSION_NAMES.isEmpty()) {
                DIMENSIONS.getDimensions();
            }
            if (CONFIG.showCurrentBiome && BIOMES.BIOME_NAMES.isEmpty()) {
                BIOMES.getBiomes();
            }
            if (CONFIG.enablePERGUI && GUIS.GUI_NAMES.isEmpty()) {
                GUIS.getGUIs();
            }
            if (CONFIG.enablePERItem && ENTITIES.ENTITY_NAMES.isEmpty()) {
                ENTITIES.getEntities();
            }
            if (CONFIG.showGameState && SERVER.knownAddresses.isEmpty()) {
                SERVER.getServerAddresses();
            }

            if (!CONFIG.hasChanged) {
                if ((!CommandHandler.isOnMainMenuPresence() && player == null) && (!DIMENSIONS.isInUse && !BIOMES.isInUse && !ENTITIES.isInUse && !SERVER.isInUse)) {
                    CommandHandler.setMainMenuPresence();
                }

                if (awaitingReply && TIMER == 0) {
                    StringHandler.sendMessageToPlayer(player, Constants.TRANSLATOR.translate("craftpresence.command.request.ignored", CLIENT.REQUESTER_USER.username));
                    DiscordRPC.INSTANCE.Discord_Respond(CLIENT.REQUESTER_USER.userId, DiscordRPC.DISCORD_REPLY_IGNORE);
                    awaitingReply = false;
                    CLIENT.STATUS = "ready";
                } else if (!awaitingReply && CLIENT.REQUESTER_USER != null) {
                    CLIENT.REQUESTER_USER = null;
                    CLIENT.STATUS = "ready";
                }
            }
        }
    }
}
