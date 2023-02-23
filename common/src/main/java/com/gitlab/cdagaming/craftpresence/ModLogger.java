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

package com.gitlab.cdagaming.craftpresence;

import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logging Manager for either Sending Info to Chat or in Logs
 *
 * @author CDAGaming
 */
public class ModLogger {
    /**
     * Name of the Logger, primarily used for Chat Formatting
     */
    private final String loggerName;

    /**
     * The Instance of the Root Logging Manager, for sending messages to logs
     */
    private final Logger logInstance;

    ModLogger(final String loggerName) {
        this.loggerName = loggerName;
        this.logInstance = LogManager.getLogger(loggerName);
    }

    /**
     * Get the instance of the root logging manager
     *
     * @return An instance of the root logging manager
     */
    public Logger getLogInstance() {
        return logInstance;
    }

    /**
     * Sends a Message with an ERROR Level to either Chat or Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void error(final String logMessage, Object... logArguments) {
        final String message = String.format(logMessage, logArguments);
        if (canShowAsChat()) {
            StringUtils.sendMessageToPlayer(CraftPresence.player, "§6§l[§f§l" + loggerName + "§6]§r§c " + message);
        } else {
            getLogInstance().error(message);
        }
    }

    /**
     * Sends a Message with an WARNING Level to either Chat or Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void warn(final String logMessage, Object... logArguments) {
        final String message = String.format(logMessage, logArguments);
        if (canShowAsChat()) {
            StringUtils.sendMessageToPlayer(CraftPresence.player, "§6§l[§f§l" + loggerName + "§6]§r§e " + message);
        } else {
            getLogInstance().warn(message);
        }
    }

    /**
     * Sends a Message with an INFO Level to either Chat or Logs
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void info(final String logMessage, Object... logArguments) {
        final String message = String.format(logMessage, logArguments);
        if (canShowAsChat()) {
            StringUtils.sendMessageToPlayer(CraftPresence.player, "§6§l[§f§l" + loggerName + "§6]§r " + message);
        } else {
            getLogInstance().info(message);
        }
    }

    /**
     * Sends a Message with an INFO Level to either Chat or Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugInfo(final String logMessage, Object... logArguments) {
        if (CommandUtils.isDebugMode()) {
            info("[DEBUG] " + logMessage, logArguments);
        }
    }

    /**
     * Sends a Message with an WARNING Level to either Chat or Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugWarn(final String logMessage, Object... logArguments) {
        if (CommandUtils.isDebugMode()) {
            warn("[DEBUG] " + logMessage, logArguments);
        }
    }

    /**
     * Sends a Message with an ERROR Level to either Chat or Logs, if in Debug Mode
     *
     * @param logMessage   The Log Message to Send
     * @param logArguments Additional Formatting Arguments
     */
    public void debugError(final String logMessage, Object... logArguments) {
        if (CommandUtils.isDebugMode()) {
            error("[DEBUG] " + logMessage, logArguments);
        }
    }

    /**
     * Whether the logger can show output as chat messages
     *
     * @return {@link Boolean#TRUE} if condition is satisfied
     */
    public boolean canShowAsChat() {
        return CraftPresence.player != null &&
                !CraftPresence.CONFIG.hasChanged &&
                !CraftPresence.SYSTEM.IS_GAME_CLOSING &&
                CraftPresence.CONFIG.accessibilitySettings.showLoggingInChat;
    }
}
