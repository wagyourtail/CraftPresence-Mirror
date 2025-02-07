/*
 * MIT License
 *
 * Copyright (c) 2018 - 2024 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.core.config.element;

import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import io.github.cdagaming.unicore.impl.HashMapBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class PresenceData extends Module implements Serializable {
    private static final long serialVersionUID = -7560029890988753870L;
    private static final PresenceData DEFAULT = new PresenceData();

    public boolean enabled = true;
    public boolean useAsMain = false;
    public String details = "";
    public String gameState = "";
    public String largeImageKey = "";
    public String largeImageText = "";
    public String smallImageKey = "";
    public String smallImageText = "";
    public String startTimestamp = "";
    public String endTimestamp = "";
    public Map<String, Button> buttons = new HashMapBuilder<String, Button>()
            .put("default", new Button(
                    Constants.TRANSLATOR.translate("craftpresence.defaults.display.button.label"),
                    Constants.TRANSLATOR.translate("craftpresence.defaults.display.button.url")
            ))
            .build();

    public PresenceData(final PresenceData other) {
        transferFrom(other);
    }

    public PresenceData() {
        // N/A
    }

    @Override
    public PresenceData getDefaults() {
        return new PresenceData(DEFAULT);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof PresenceData && !equals(target)) {
            final PresenceData data = (PresenceData) target;

            enabled = data.enabled;
            useAsMain = data.useAsMain;
            setDetails(data.details);
            setGameState(data.gameState);
            setLargeImage(data.largeImageKey, data.largeImageText);
            setSmallImage(data.smallImageKey, data.smallImageText);
            setTimes(data.startTimestamp, data.endTimestamp);
            buttons.clear();
            for (Map.Entry<String, Button> entry : data.buttons.entrySet()) {
                addButton(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public PresenceData copy() {
        return new PresenceData(this);
    }

    public PresenceData setDetails(final String details) {
        this.details = details;
        return this;
    }

    public PresenceData setGameState(final String gameState) {
        this.gameState = gameState;
        return this;
    }

    public PresenceData setLargeImage(final String imageKey, final String imageText) {
        this.largeImageKey = imageKey;
        this.largeImageText = imageText;
        return this;
    }

    public PresenceData setSmallImage(final String imageKey, final String imageText) {
        this.smallImageKey = imageKey;
        this.smallImageText = imageText;
        return this;
    }

    public PresenceData setTimes(final String startTimestamp, final String endTimestamp) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        return this;
    }

    public PresenceData setStartTime(final String timestamp) {
        return setTimes(timestamp, this.endTimestamp);
    }

    public PresenceData setEndTime(final String timestamp) {
        return setTimes(this.startTimestamp, timestamp);
    }

    public PresenceData addButton(final String name, final Button button) {
        this.buttons.put(name, new Button(button));
        return this;
    }

    public PresenceData removeButton(final String name) {
        this.buttons.remove(name);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof PresenceData)) {
            return false;
        }

        final PresenceData other = (PresenceData) obj;

        return Objects.equals(other.details, details) &&
                Objects.equals(other.gameState, gameState) &&
                Objects.equals(other.largeImageKey, largeImageKey) &&
                Objects.equals(other.largeImageText, largeImageText) &&
                Objects.equals(other.smallImageKey, smallImageKey) &&
                Objects.equals(other.startTimestamp, startTimestamp) &&
                Objects.equals(other.endTimestamp, endTimestamp) &&
                Objects.equals(other.buttons, buttons);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                details, gameState,
                largeImageKey, largeImageText,
                smallImageKey, smallImageText,
                startTimestamp, endTimestamp,
                buttons
        );
    }
}
