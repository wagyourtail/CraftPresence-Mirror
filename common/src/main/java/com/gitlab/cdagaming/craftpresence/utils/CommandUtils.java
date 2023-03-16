/*
 * MIT License
 *
 * Copyright (c) 2018 - 2023 CDAGaming (cstack2011@yahoo.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gitlab.cdagaming.craftpresence.utils;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.impl.TreeMapBuilder;
import com.gitlab.cdagaming.craftpresence.integrations.pack.Pack;
import com.gitlab.cdagaming.craftpresence.integrations.pack.curse.CurseUtils;
import com.gitlab.cdagaming.craftpresence.integrations.pack.mcupdater.MCUpdaterUtils;
import com.gitlab.cdagaming.craftpresence.integrations.pack.multimc.MultiMCUtils;
import com.gitlab.cdagaming.craftpresence.integrations.pack.technic.TechnicUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.jagrosh.discordipc.entities.DiscordBuild;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Command Utilities for Synchronizing and Initializing Data
 *
 * @author CDAGaming
 */
@SuppressFBWarnings("MS_CANNOT_BE_FINAL")
public class CommandUtils {
    /**
     * Timer Instance for this Class, used for Scheduling Events
     */
    private static final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    /**
     * A mapping of the currently loaded Rich Presence Modules
     */
    private static final Map<String, Module> modules = new TreeMapBuilder<String, Module>()
            .put("_biome", CraftPresence.BIOMES)
            .put("_dimension", CraftPresence.DIMENSIONS)
            .put("_item", CraftPresence.TILE_ENTITIES)
            .put("_entity", CraftPresence.ENTITIES)
            .put("_server", CraftPresence.SERVER)
            .put("_screen", CraftPresence.GUIS)
            .build();
    /**
     * A mapping of the currently loaded Pack Extension Modules
     */
    private static final Map<String, Pack> packModules = new TreeMapBuilder<String, Pack>()
            .put("curse", new CurseUtils())
            .put("multimc", new MultiMCUtils())
            .put("mcupdater", new MCUpdaterUtils())
            .put("technic", new TechnicUtils())
            .build();
    /**
     * Whether you are on the Main Menu in Minecraft
     */
    public static boolean isInMainMenu = false;
    /**
     * Whether you are on the Loading Stage in Minecraft
     */
    public static boolean isLoadingGame = false;

    /**
     * Retrieve the Timer Instance for this Class, used for Scheduling Events
     *
     * @return the Timer Instance for this Class
     */
    public static ScheduledExecutorService getThreadPool() {
        return exec;
    }

    /**
     * Determines if this Application is running in a Developer or Debug State
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isDebugMode() {
        return ModUtils.IS_DEV_FLAG ||
                isVerboseMode() || (CraftPresence.CONFIG != null && CraftPresence.CONFIG.advancedSettings.debugMode);
    }

    /**
     * Determines if this Application is running in a de-obfuscated or Developer environment
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public static boolean isVerboseMode() {
        return ModUtils.IS_VERBOSE_FLAG ||
                (CraftPresence.CONFIG != null && CraftPresence.CONFIG.advancedSettings.verboseMode);
    }

    /**
     * Synchronizes Module Placeholder Data, meant for RPC usage
     */
    public static void syncModuleArguments() {
        for (Map.Entry<String, Module> module : modules.entrySet()) {
            String name = module.getKey();
            name = (name.startsWith("_") ? "" : "_") + name;
            CraftPresence.CLIENT.syncArgument(name + ".instance", module.getValue());
        }
    }

    /**
     * Synchronizes the `pack` Arguments, based on any found Launcher Pack/Instance Data
     */
    public static void syncPackArguments() {
        boolean foundPack = false;
        for (Map.Entry<String, Pack> pack : packModules.entrySet()) {
            final String type = pack.getKey();
            final Pack data = pack.getValue();

            if (data.hasPackName()) {
                CraftPresence.CLIENT.syncArgument("pack.type", type);
                CraftPresence.CLIENT.syncArgument("pack.name", data.getPackName());
                CraftPresence.CLIENT.syncArgument("pack.icon",
                        CraftPresence.CLIENT.imageOf("pack.icon", true, data.getPackIcon())
                );

                foundPack = true;
                break;
            }
        }

        if (!foundPack) {
            CraftPresence.CLIENT.removeArguments("pack");
        }
    }

    /**
     * Clears Runtime Client Data from all active Modules (PARTIAL Clear)
     */
    public static void clearModuleData() {
        for (Module module : modules.values()) {
            module.clearClientData();
        }
    }

    /**
     * Adds a module for ticking and RPC Syncronization
     *
     * @param moduleId The name of the module
     * @param instance The instance of the module
     */
    public static void addModule(final String moduleId, final Module instance) {
        modules.put(moduleId, instance);
    }

    /**
     * Adds a module for ticking and RPC Syncronization
     *
     * @param moduleId The name of the module
     * @param instance The instance of the module
     */
    public static void addModule(final String moduleId, final Pack instance) {
        packModules.put(moduleId, instance);
    }

    /**
     * Reloads and Synchronizes Data, as needed, and performs onTick Events
     *
     * @param forceUpdateRPC Whether to Force an Update to the RPC Data
     */
    public static void reloadData(final boolean forceUpdateRPC) {
        ModUtils.TRANSLATOR.onTick();
        if (forceUpdateRPC) {
            ModUtils.TRANSLATOR.syncTranslations();
        }
        CraftPresence.SYSTEM.onTick();
        CraftPresence.instance.addScheduledTask(CraftPresence.KEYBINDINGS::onTick);

        CraftPresence.SYSTEM.TICK_LOCK.lock();
        try {
            for (Module module : modules.values()) {
                if (module.canBeLoaded()) {
                    module.onTick();
                    if (forceUpdateRPC && module.isInUse()) {
                        module.updatePresence();
                    }
                }
            }
            CraftPresence.CLIENT.onTick();
        } finally {
            CraftPresence.SYSTEM.TICK_LOCK.unlock();
            CraftPresence.SYSTEM.postTick();
        }
    }

    /**
     * Restarts and Initializes the RPC Data
     *
     * @param flushOverride Whether to purge existing RPC assets, to be later refreshed
     */
    public static void rebootRPC(boolean flushOverride) {
        final String clientId = CraftPresence.CONFIG.generalSettings.clientId;
        flushOverride = flushOverride || !CraftPresence.CLIENT.CLIENT_ID.equals(
                clientId
        );
        CraftPresence.CLIENT.shutDown();

        if (flushOverride) {
            DiscordAssetUtils.emptyData();
            CraftPresence.CLIENT.CLIENT_ID = clientId;
        }

        final DiscordBuild preferredBuild = DiscordBuild.from(CraftPresence.CONFIG.generalSettings.preferredClientLevel);
        if (!CraftPresence.CLIENT.PREFERRED_CLIENT.equals(preferredBuild)) {
            CraftPresence.CLIENT.PREFERRED_CLIENT = preferredBuild;
        }
        DiscordAssetUtils.loadAssets(clientId, true);
        CraftPresence.CLIENT.init(CraftPresence.CONFIG.generalSettings.resetTimeOnInit);
    }

    /**
     * Restarts and Initializes the RPC Data
     */
    public static void rebootRPC() {
        rebootRPC(false);
    }

    /**
     * Initializes Essential Data<p>
     * (In this case, Pack Data and Available RPC Icons)
     */
    public static void init() {
        for (Map.Entry<String, Pack> pack : packModules.entrySet()) {
            final String type = pack.getKey();
            final Pack data = pack.getValue();
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.pack.init", type));
            if (data.load()) {
                ModUtils.LOG.info(ModUtils.TRANSLATOR.translate("craftpresence.logger.info.pack.loaded", type, data.getPackName(), data.getPackIcon()));
                break; // Only iterate until the first pack is found
            } else {
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.pack", type));
            }
        }
        DiscordAssetUtils.loadAssets(CraftPresence.CONFIG.generalSettings.clientId, true);
        CraftPresence.KEYBINDINGS.register();
    }

    /**
     * Synchronizes RPC Data related to the current Menu Module that's Active
     *
     * @param currentData the current Menu {@link ModuleData}
     */
    public static void syncMenuData(final ModuleData currentData) {
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : "";
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : CraftPresence.CONFIG.generalSettings.defaultIcon;
        final String formattedIcon = CraftPresence.CLIENT.imageOf("menu.icon", true, currentIcon);

        CraftPresence.CLIENT.clearPartyData(true, false);
        CraftPresence.CLIENT.syncOverride(currentData, "menu.message", "menu.icon");
        CraftPresence.CLIENT.syncArgument("menu.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("menu.icon", formattedIcon);
    }

    /**
     * Synchronizes RPC Data towards that of being in a Loading State
     */
    public static void setLoadingPresence() {
        syncMenuData(CraftPresence.CONFIG.statusMessages.loadingData);
        isLoadingGame = true;
    }

    /**
     * Synchronizes RPC Data towards that of being in the Main Menu
     */
    public static void setMainMenuPresence() {
        // Clear Loading Game State, if applicable
        if (isLoadingGame) {
            clearMenuPresence();
            isLoadingGame = false;
        }

        syncMenuData(CraftPresence.CONFIG.statusMessages.mainMenuData);
        isInMainMenu = true;
    }

    /**
     * Clear the Initial Presence Data set from the Loading and Main Menu Events
     */
    public static void clearInitialPresence() {
        isInMainMenu = false;
        isLoadingGame = false;
        clearMenuPresence();
    }

    /**
     * Clear the Menu Presence Data, derived from the Loading and Main Menu Events
     */
    public static void clearMenuPresence() {
        CraftPresence.CLIENT.clearOverride("menu.message", "menu.icon");
        CraftPresence.CLIENT.removeArguments("menu");
    }
}
