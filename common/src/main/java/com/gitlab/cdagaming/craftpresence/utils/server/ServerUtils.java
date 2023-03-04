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

package com.gitlab.cdagaming.craftpresence.utils.server;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.impl.discord.DiscordStatus;
import com.gitlab.cdagaming.craftpresence.impl.discord.PartyPrivacy;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.TimeUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.gitlab.cdagaming.craftpresence.utils.entity.EntityUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.List;
import java.util.Map;

/**
 * Server Utilities used to Parse Server Data and handle related RPC Events
 *
 * @author CDAGaming
 */
public class ServerUtils implements Module {
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * The Current Player Map, if available
     */
    public List<NetworkPlayerInfo> currentPlayerList = Lists.newArrayList();
    /**
     * A List of the detected Server Addresses
     */
    public List<String> knownAddresses = Lists.newArrayList();
    /**
     * A List of the detected Server Data from NBT
     */
    public Map<String, ServerData> knownServerData = Maps.newHashMap();
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    private boolean hasScanned = false;
    /**
     * The IP Address of the Current Server the Player is in
     */
    private String currentServer_IP;
    /**
     * The Name of the Current Server the Player is in
     */
    private String currentServer_Name;
    /**
     * The Message of the Day of the Current Server the Player is in
     */
    private String currentServer_MOTD;
    /**
     * The Message of the Day, split by new lines, of the Current Server the Player is in
     */
    private List<String> currentServer_MOTD_Lines = Lists.newArrayList();
    /**
     * The Current Server RPC Message being used, with Arguments
     */
    private String currentServerMessage = "";
    /**
     * Whether the endpoint icon system can be used in this module
     */
    private boolean canUseEndpointIcon = false;
    /**
     * The Current Server RPC Icon being used, with Arguments
     */
    private String currentServerIcon = "";
    /**
     * The Current Formatted World Time (24-hour Format), as a String
     */
    private String timeString24;
    /**
     * The Current Formatted World Time (12-hour Format), as a String
     */
    private String timeString12;
    /**
     * The Current Formatted World Days, as a String
     */
    private String dayString;
    /**
     * The Current World's Difficulty
     */
    private String currentDifficulty;
    /**
     * The Current World's Name
     */
    private String currentWorldName;
    /**
     * The Current World's Weather Name
     */
    private String currentWeatherName;
    /**
     * The Current World's Weather Duration
     */
    private long currentWeatherDuration;
    /**
     * The Amount of Players in the Current Server the Player is in
     */
    private int currentPlayers;
    /**
     * The Maximum Amount of Players allowed in the Current Server the Player is in
     */
    private int maxPlayers;
    /**
     * The amount of Currently detected Server Addresses
     */
    private int serverIndex;
    /**
     * Mapping storing the Current X, Y and Z Position of the Player in a World
     * Format: Position (X, Y, Z)
     */
    private Tuple<Double, Double, Double> currentCoordinates = new Tuple<>(0.0D, 0.0D, 0.0D);
    /**
     * Mapping storing the Current and Maximum Health the Player currently has in a World
     */
    private Pair<Double, Double> currentHealth = new Pair<>(0.0D, 0.0D);
    /**
     * The Current Server Connection Data and Info
     */
    private ServerData currentServerData;
    /**
     * The Queued Server Connection Data and Info to Join, if any
     */
    private ServerData requestedServerData;
    /**
     * The Player's Current Connection Data
     */
    private NetHandlerPlayClient currentConnection;
    /**
     * If the RPC needs to be Updated or Re-Synchronized<p>
     * Needed here for Multiple-Condition RPC Triggers
     */
    private boolean queuedForUpdate = false;
    /**
     * If in Progress of Joining a World/Server from another World/Server
     */
    private boolean joinInProgress = false;
    /**
     * If the Current Server is on a LAN-Based Connection (A Local Network Game)
     */
    private boolean isOnLAN = false;

    @Override
    public void emptyData() {
        hasScanned = false;
        currentPlayerList.clear();
        knownAddresses.clear();
        knownServerData.clear();
        clearClientData();
    }

    @Override
    public void clearClientData() {
        currentServer_IP = null;
        currentServer_MOTD = null;
        currentServer_MOTD_Lines.clear();
        currentServer_Name = null;
        currentServerData = null;
        currentConnection = null;
        currentCoordinates = new Tuple<>(0.0D, 0.0D, 0.0D);
        currentHealth = new Pair<>(0.0D, 0.0D);
        currentDifficulty = null;
        currentWorldName = null;
        currentWeatherName = null;
        currentWeatherDuration = 0L;
        currentServerMessage = "";
        currentServerIcon = "";
        canUseEndpointIcon = false;
        timeString24 = null;
        timeString12 = null;
        dayString = null;
        currentPlayers = 0;
        maxPlayers = 0;

        queuedForUpdate = false;
        isOnLAN = false;
        setInUse(false);

        if (!joinInProgress) {
            requestedServerData = null;
        }

        CraftPresence.CLIENT.removeArguments("server", "world", "player");
        CraftPresence.CLIENT.clearOverride("server.message", "server.icon");
        CraftPresence.CLIENT.clearPartyData(true, false);
    }

    @Override
    public void onTick() {
        joinInProgress = CraftPresence.CLIENT.STATUS == DiscordStatus.JoinGame || CraftPresence.CLIENT.STATUS == DiscordStatus.SpectateGame;
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.generalSettings.detectWorldData : enabled;
        final boolean needsUpdate = enabled && !hasScanned;

        if (needsUpdate) {
            new Thread(this::getAllData, "CraftPresence-Server-Lookup").start();
            hasScanned = true;
        }

        if (enabled) {
            if (CraftPresence.player != null && !joinInProgress) {
                setInUse(true);
                updateData();
            } else if (isInUse()) {
                clearClientData();
            }
        } else if (isInUse()) {
            emptyData();
        }

        if (joinInProgress && requestedServerData != null) {
            CraftPresence.instance.addScheduledTask(() -> joinServer(requestedServerData));
        }
    }

    @Override
    public void updateData() {
        final ServerData newServerData = CraftPresence.instance.getCurrentServerData();
        final NetHandlerPlayClient newConnection = CraftPresence.instance.getConnection();

        if (!joinInProgress) {
            final List<NetworkPlayerInfo> newPlayerList = newConnection != null ? Lists.newArrayList(newConnection.getPlayerInfoMap()) : Lists.newArrayList();
            final int newCurrentPlayers = newConnection != null ? newConnection.getPlayerInfoMap().size() : 1;
            final int newMaxPlayers = newConnection != null && newConnection.currentServerMaxPlayers >= newCurrentPlayers ? newConnection.currentServerMaxPlayers : newCurrentPlayers + 1;
            final boolean newLANStatus = (CraftPresence.instance.isSingleplayer() && newCurrentPlayers > 1) || (newServerData != null && newServerData.isOnLAN());

            final String newServer_IP = newServerData != null && !StringUtils.isNullOrEmpty(newServerData.serverIP) ? newServerData.serverIP : "127.0.0.1";
            final String newServer_Name = newServerData != null && !StringUtils.isNullOrEmpty(newServerData.serverName) ? newServerData.serverName : CraftPresence.CONFIG.serverSettings.fallbackServerName;
            final String newServer_MOTD = !isOnLAN && !CraftPresence.instance.isSingleplayer() && (newServerData != null && !StringUtils.isNullOrEmpty(newServerData.serverMOTD)) &&
                    !(newServerData.serverMOTD.equalsIgnoreCase(ModUtils.TRANSLATOR.translate("craftpresence.multiplayer.status.cannot_connect")) ||
                            newServerData.serverMOTD.equalsIgnoreCase(ModUtils.TRANSLATOR.translate("craftpresence.multiplayer.status.cannot_resolve")) ||
                            newServerData.serverMOTD.equalsIgnoreCase(ModUtils.TRANSLATOR.translate("craftpresence.multiplayer.status.polling")) ||
                            newServerData.serverMOTD.equalsIgnoreCase(ModUtils.TRANSLATOR.translate("craftpresence.multiplayer.status.pinging"))) ? StringUtils.stripColors(newServerData.serverMOTD) : CraftPresence.CONFIG.serverSettings.fallbackServerMotd;

            if (newLANStatus != isOnLAN || ((newServerData != null && !newServerData.equals(currentServerData)) ||
                    (newServerData == null && currentServerData != null)) ||
                    (newConnection != null && !newConnection.equals(currentConnection)) || !newServer_IP.equals(currentServer_IP) ||
                    (!StringUtils.isNullOrEmpty(newServer_MOTD) && !newServer_MOTD.equals(currentServer_MOTD)) ||
                    (!StringUtils.isNullOrEmpty(newServer_Name) && !newServer_Name.equals(currentServer_Name))) {
                currentServer_IP = newServer_IP;

                if (!newServer_MOTD.equals(currentServer_MOTD)) {
                    currentServer_MOTD = newServer_MOTD;
                    currentServer_MOTD_Lines = StringUtils.splitTextByNewLine(newServer_MOTD);
                }
                currentServer_Name = newServer_Name;
                currentServerData = newServerData;
                currentConnection = newConnection;
                isOnLAN = newLANStatus;
                queuedForUpdate = true;

                if (!StringUtils.isNullOrEmpty(currentServer_IP)) {
                    final String formattedIP = currentServer_IP.contains(":") ? StringUtils.formatAddress(currentServer_IP, false) : currentServer_IP;
                    if (!knownAddresses.contains(formattedIP)) {
                        knownAddresses.add(formattedIP);
                    }
                }

                final ServerList serverList = new ServerList(CraftPresence.instance);
                serverList.loadServerList();
                if (serverList.countServers() != serverIndex || CraftPresence.CONFIG.serverSettings.serverData.size() != serverIndex) {
                    getAllData();
                }
            }

            // NOTE: Universal + Custom Events

            // `player` Sub-Arguments

            // `player.position` Argument = Current Coordinates of Player
            final double newX = StringUtils.roundDouble(CraftPresence.player != null ? CraftPresence.player.posX : 0.0D, CraftPresence.CONFIG.advancedSettings.roundSize);
            final double newY = StringUtils.roundDouble(CraftPresence.player != null ? CraftPresence.player.posY : 0.0D, CraftPresence.CONFIG.advancedSettings.roundSize);
            final double newZ = StringUtils.roundDouble(CraftPresence.player != null ? CraftPresence.player.posZ : 0.0D, CraftPresence.CONFIG.advancedSettings.roundSize);
            final Tuple<Double, Double, Double> newCoordinates = new Tuple<>(newX, newY, newZ);
            if (!newCoordinates.equals(currentCoordinates)) {
                currentCoordinates = newCoordinates;
                queuedForUpdate = true;
            }

            // 'player.health' Argument = Current and Maximum Health of Player
            final Pair<Double, Double> newHealth = CraftPresence.player != null ? new Pair<>(StringUtils.roundDouble(CraftPresence.player.getHealth(), 0), StringUtils.roundDouble(CraftPresence.player.getMaxHealth(), 0)) : new Pair<>(0.0D, 0.0D);
            if (!newHealth.equals(currentHealth)) {
                currentHealth = newHealth;
                queuedForUpdate = true;
            }

            // 'world' Sub-Arguments

            // 'world.difficulty' Argument = Current Difficulty of the World
            final String newDifficulty = CraftPresence.player != null ?
                    (CraftPresence.player.world.getWorldInfo().isHardcoreModeEnabled() ? ModUtils.TRANSLATOR.translate("craftpresence.defaults.mode.hardcore") : CraftPresence.player.world.getDifficulty().name()) :
                    "";
            if (!newDifficulty.equals(currentDifficulty)) {
                currentDifficulty = newDifficulty;
                queuedForUpdate = true;
            }

            // `world.weather.name`, `world.weather.duration` Argument = Current Weather Data of the World
            final Pair<String, Long> newWeatherData = EntityUtils.getWeather(CraftPresence.player);
            final String newWeatherName = ModUtils.TRANSLATOR.translate("craftpresence.defaults.weather." + newWeatherData.getFirst());
            final Long newWeatherDuration = newWeatherData.getSecond();
            if (!newWeatherName.equals(currentWeatherName)) {
                currentWeatherName = newWeatherName;
                queuedForUpdate = true;
            }
            if (!newWeatherDuration.equals(currentWeatherDuration)) {
                currentWeatherDuration = newWeatherDuration;
                queuedForUpdate = true;
            }

            // 'world.name' Argument = Current Name of the World
            final String primaryWorldName = CraftPresence.instance.getIntegratedServer() != null ? CraftPresence.instance.getIntegratedServer().getWorldName() : "";
            final String secondaryWorldName = CraftPresence.player != null ? CraftPresence.player.world.getWorldInfo().getWorldName() : ModUtils.TRANSLATOR.translate("craftpresence.defaults.world_name");
            final String newWorldName = StringUtils.getOrDefault(primaryWorldName, secondaryWorldName);
            if (!newWorldName.equals(currentWorldName)) {
                currentWorldName = newWorldName;
                queuedForUpdate = true;
            }

            // 'world.time' Argument = Current Time in World
            final String newGameTime = CraftPresence.player != null ? TimeUtils.convertWorldTime(CraftPresence.player.world.getWorldTime()) : null;
            if (!StringUtils.isNullOrEmpty(newGameTime) && !newGameTime.equals(timeString24)) {
                timeString24 = newGameTime;
                timeString12 = TimeUtils.convertTime(newGameTime, "HH:mm", "hh:mm a");
                queuedForUpdate = true;
            }

            // 'world.day' Argument = Current Amount of Days in World
            final String newGameDay = CraftPresence.player != null ? String.format("%d", CraftPresence.player.world.getWorldTime() / 24000L) : null;
            if (!StringUtils.isNullOrEmpty(newGameDay) && !newGameDay.equals(dayString)) {
                dayString = newGameDay;
                queuedForUpdate = true;
            }

            // 'server.players' Argument = Current and Maximum Allowed Players in Server/World
            if (newCurrentPlayers != currentPlayers || newMaxPlayers != maxPlayers) {
                currentPlayers = newCurrentPlayers;
                maxPlayers = newMaxPlayers;
                queuedForUpdate = true;
            }

            // Update Player List as needed, and Sync with Entity System if enabled
            if (!newPlayerList.equals(currentPlayerList)) {
                currentPlayerList = newPlayerList;

                if (CraftPresence.ENTITIES.enabled) {
                    CraftPresence.ENTITIES.ENTITY_NAMES.removeAll(CraftPresence.ENTITIES.PLAYER_BINDINGS.keySet());
                    CraftPresence.ENTITIES.getAllData();
                }
            }
        }

        if (queuedForUpdate) {
            updatePresence();
        }
    }

    /**
     * Creates a Secret Key to use in Sending Requested Server Data from Discord Join Requests
     *
     * @return The Parsable Secret Key
     */
    private String makeSecret() {
        String formattedKey = CraftPresence.CLIENT.CLIENT_ID + "";
        boolean containsServerName = false;
        boolean containsServerIP = false;

        if (!StringUtils.isNullOrEmpty(currentServer_Name)) {
            formattedKey += "-" + currentServer_Name.toLowerCase();
            containsServerName = true;
        }
        if (!StringUtils.isNullOrEmpty(currentServer_IP)) {
            formattedKey += "-" + currentServer_IP.toLowerCase();
            containsServerIP = true;
        }

        formattedKey += ";" + containsServerName + ";" + containsServerIP;
        return formattedKey;
    }

    /**
     * Verifies the Inputted secret Key, and upon match, Form Server Data to join a Server
     *
     * @param secret The secret key to test against for validity
     */
    public void verifyAndJoin(final String secret) {
        String[] boolParts = secret.split(";");
        String[] stringParts = boolParts[0].split("-");
        boolean containsValidClientID = StringUtils.elementExists(stringParts, 0) && (stringParts[0].length() >= 18 && StringUtils.getValidLong(stringParts[0]).getFirst());
        boolean containsServerName = StringUtils.elementExists(boolParts, 1) && StringUtils.elementExists(stringParts, 1) && Boolean.parseBoolean(boolParts[1]);
        boolean containsServerIP = StringUtils.elementExists(boolParts, 2) && StringUtils.elementExists(stringParts, 2) && Boolean.parseBoolean(boolParts[2]);
        String serverName = containsServerName ? stringParts[1] : CraftPresence.CONFIG.serverSettings.fallbackServerName;
        String serverIP = containsServerIP ? stringParts[2] : "";
        boolean isValidSecret = boolParts.length <= 4 && stringParts.length <= 3 && containsValidClientID;

        if (isValidSecret) {
            if (CraftPresence.CONFIG.generalSettings.enableJoinRequests) {
                requestedServerData = new ServerData(serverName, serverIP, false);
            } else {
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.warning.config.disabled.enable_join_request"));
            }
        } else {
            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate("craftpresence.logger.error.discord.join", secret));
        }
    }

    /**
     * Joins a Server/World based on Server Data requested
     *
     * @param serverData The Requested Server Data to Join
     */
    private void joinServer(final ServerData serverData) {
        try {
            if (CraftPresence.player != null) {
                CraftPresence.player.world.sendQuittingDisconnectingPacket();
                CraftPresence.instance.loadWorld(null);
            }
            CraftPresence.instance.displayGuiScreen(new GuiConnecting(CraftPresence.instance.currentScreen != null ? CraftPresence.instance.currentScreen : new GuiMainMenu(), CraftPresence.instance, serverData));
        } catch (Exception ex) {
            if (CommandUtils.isVerboseMode()) {
                ex.printStackTrace();
            }
        } finally {
            requestedServerData = null;
        }
    }

    @Override
    public void updatePresence() {
        // Form General Argument Lists & Sub Argument Lists
        canUseEndpointIcon = false;

        CraftPresence.CLIENT.syncArgument("player.position.x", currentCoordinates.getFirst());
        CraftPresence.CLIENT.syncArgument("player.position.y", currentCoordinates.getSecond());
        CraftPresence.CLIENT.syncArgument("player.position.z", currentCoordinates.getThird());

        CraftPresence.CLIENT.syncArgument("player.health.current", currentHealth.getFirst());
        CraftPresence.CLIENT.syncArgument("player.health.max", currentHealth.getSecond());

        // World Data Arguments
        CraftPresence.CLIENT.syncArgument("world.difficulty", StringUtils.getOrDefault(currentDifficulty));
        CraftPresence.CLIENT.syncArgument("world.weather.name", StringUtils.getOrDefault(currentWeatherName));
        CraftPresence.CLIENT.syncArgument("world.weather.duration", currentWeatherDuration);
        CraftPresence.CLIENT.syncArgument("world.name", StringUtils.getOrDefault(currentWorldName));
        CraftPresence.CLIENT.syncArgument("world.time24", StringUtils.getOrDefault(timeString24));
        CraftPresence.CLIENT.syncArgument("world.time12", StringUtils.getOrDefault(timeString12));
        CraftPresence.CLIENT.syncArgument("world.day", StringUtils.getOrDefault(dayString));

        CraftPresence.CLIENT.syncArgument("server.default.icon", CraftPresence.CONFIG.serverSettings.fallbackServerIcon);

        ModuleData resultData = new ModuleData();
        String formattedIcon;
        if (!CraftPresence.instance.isSingleplayer() && currentServerData != null) {
            // Player Amount Arguments
            CraftPresence.CLIENT.syncArgument("server.players.current", currentPlayers);
            CraftPresence.CLIENT.syncArgument("server.players.max", maxPlayers);

            // Server Data Arguments (Multiplayer)
            final String formattedIP = currentServer_IP.contains(":") ? StringUtils.formatAddress(currentServer_IP, false) : currentServer_IP;
            CraftPresence.CLIENT.syncArgument("server.address.full", currentServer_IP);
            CraftPresence.CLIENT.syncArgument("server.address.short", formattedIP);
            CraftPresence.CLIENT.syncArgument("server.name", currentServer_Name);
            CraftPresence.CLIENT.syncArgument("server.motd.raw", currentServer_MOTD);
            if (!currentServer_MOTD_Lines.isEmpty()) {
                int index = 1;
                for (String motdPart : currentServer_MOTD_Lines) {
                    CraftPresence.CLIENT.syncArgument("data.server.motd.line." + index, motdPart);
                    index++;
                }
            }

            final ModuleData defaultData = CraftPresence.CONFIG.serverSettings.serverData.get("default");
            final ModuleData alternateData = CraftPresence.CONFIG.serverSettings.serverData.get(currentServer_Name);
            final ModuleData primaryData = CraftPresence.CONFIG.serverSettings.serverData.get(formattedIP);

            canUseEndpointIcon = CraftPresence.CONFIG.advancedSettings.allowEndpointIcons && !StringUtils.isNullOrEmpty(CraftPresence.CONFIG.advancedSettings.serverIconEndpoint);

            final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : "";
            final String alternateIcon = Config.isValidProperty(alternateData, "iconOverride") ? alternateData.getIconOverride() : defaultIcon;
            final String currentIcon = Config.isValidProperty(primaryData, "iconOverride") ? primaryData.getIconOverride() : alternateIcon;

            resultData = primaryData != null ? primaryData : (alternateData != null ? alternateData : defaultData);
            currentServerIcon = currentIcon;

            // Attempt to find alternative icons, if no overrides are present
            if (StringUtils.isNullOrEmpty(currentServerIcon)) {
                if (canUseEndpointIcon) {
                    if (!CraftPresence.CONFIG.displaySettings.dynamicIcons.containsKey(formattedIP)) {
                        CraftPresence.CONFIG.displaySettings.dynamicIcons.put(formattedIP,
                                CraftPresence.CLIENT.compileData(String.format(
                                        CraftPresence.CONFIG.advancedSettings.serverIconEndpoint,
                                        formattedIP
                                )).get().toString()
                        );
                        DiscordAssetUtils.syncCustomAssets();
                        CraftPresence.CONFIG.save();
                    }
                    currentServerIcon = formattedIP;
                } else {
                    currentServerIcon = currentServer_Name;
                }
            }

            if (isOnLAN) {
                // NOTE: LAN-Only Presence Updates
                resultData = CraftPresence.CONFIG.statusMessages.lanData;
                currentServerMessage = Config.isValidProperty(resultData, "textOverride") ? resultData.getTextOverride() : "";
                currentServerIcon = Config.isValidProperty(resultData, "iconOverride") ? resultData.getIconOverride() : "";
            } else {
                // NOTE: Server-Only Presence Updates
                final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
                final String alternateMessage = alternateData != null && Config.isValidProperty(alternateData, "textOverride") ? alternateData.getTextOverride() : defaultMessage;
                currentServerMessage = primaryData != null && Config.isValidProperty(primaryData, "textOverride") ? primaryData.getTextOverride() : alternateMessage;

                // If join requests are enabled, parse the appropriate data
                // to form party information.
                //
                // Note: The party privacy level is appended by modulus division to prevent
                // it being anything other than valid privacy levels
                if (CraftPresence.CONFIG.generalSettings.enableJoinRequests) {
                    if (!StringUtils.isNullOrEmpty(currentServer_Name) && !currentServer_Name.equalsIgnoreCase(CraftPresence.CONFIG.serverSettings.fallbackServerName)) {
                        CraftPresence.CLIENT.PARTY_ID = "Join Server: " + currentServer_Name;
                    } else {
                        CraftPresence.CLIENT.PARTY_ID = "Join Server: " + currentServer_IP;
                    }
                    CraftPresence.CLIENT.JOIN_SECRET = makeSecret();
                    CraftPresence.CLIENT.PARTY_SIZE = currentPlayers;
                    CraftPresence.CLIENT.PARTY_MAX = maxPlayers;
                    CraftPresence.CLIENT.PARTY_PRIVACY = PartyPrivacy.from(CraftPresence.CONFIG.generalSettings.partyPrivacyLevel % 2);
                }
            }
        } else if (CraftPresence.instance.isSingleplayer()) {
            // NOTE: SinglePlayer-Only Presence Updates
            resultData = CraftPresence.CONFIG.statusMessages.singleplayerData;
            currentServerMessage = Config.isValidProperty(resultData, "textOverride") ? resultData.getTextOverride() : "";
            currentServerIcon = Config.isValidProperty(resultData, "iconOverride") ? resultData.getIconOverride() : "";
        }
        formattedIcon = CraftPresence.CLIENT.imageOf("server.icon", true, currentServerIcon, CraftPresence.CONFIG.serverSettings.fallbackServerIcon);

        CraftPresence.CLIENT.syncOverride(resultData, "server.message", "server.icon");
        CraftPresence.CLIENT.syncArgument("server.message", currentServerMessage);
        CraftPresence.CLIENT.syncArgument("server.icon", formattedIcon);
        queuedForUpdate = false;
    }

    @Override
    public void getAllData() {
        try {
            final ServerList serverList = new ServerList(CraftPresence.instance);
            serverList.loadServerList();
            serverIndex = serverList.countServers();

            for (int currentIndex = 0; currentIndex < serverIndex; currentIndex++) {
                final ServerData data = serverList.getServerData(currentIndex);
                if (!StringUtils.isNullOrEmpty(data.serverIP)) {
                    final String formattedIP = data.serverIP.contains(":") ? StringUtils.formatAddress(data.serverIP, false) : data.serverIP;
                    if (!knownAddresses.contains(formattedIP)) {
                        knownAddresses.add(formattedIP);
                    }
                    if (!knownServerData.containsKey(data.serverIP)) {
                        knownServerData.put(data.serverIP, data);
                    }
                }
            }
        } catch (Exception ex) {
            if (CommandUtils.isVerboseMode()) {
                ex.printStackTrace();
            }
        }

        for (String serverEntry : CraftPresence.CONFIG.serverSettings.serverData.keySet()) {
            if (!StringUtils.isNullOrEmpty(serverEntry) && !knownAddresses.contains(serverEntry)) {
                knownAddresses.add(serverEntry);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    @Override
    public boolean isInUse() {
        return isInUse;
    }

    @Override
    public void setInUse(boolean state) {
        if (state && !this.isInUse) {
            CraftPresence.CLIENT.syncTimestamp("data.server.time");
        }
        this.isInUse = state;
    }

    /**
     * Retrieves server data for the specified address, if available
     *
     * @param serverAddress The Server's identifying address
     * @return Server data for the specified address, if available
     */
    public ServerData getDataFromName(final String serverAddress) {
        return knownServerData.getOrDefault(serverAddress, null);
    }
}
