/*
 * MIT License
 *
 * Copyright (c) 2018 - 2022 CDAGaming (cstack2011@yahoo.com)
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

package com.gitlab.cdagaming.craftpresence.utils.world;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.impl.Module;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.MappingUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.google.common.collect.Lists;
import net.minecraft.world.biome.Biome;

import java.util.List;

/**
 * Biome Utilities used to Parse Biome Data and handle related RPC Events
 *
 * @author CDAGaming
 */
public class BiomeUtils implements Module {
    /**
     * A List of the detected Biome Type's
     */
    private final List<Biome> BIOME_TYPES = Lists.newArrayList();
    /**
     * Whether this module is active and currently in use
     */
    public boolean isInUse = false;
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * Whether this module has performed an initial retrieval of items
     */
    public boolean hasScanned = false;
    /**
     * A List of the detected Biome Names
     */
    public List<String> BIOME_NAMES = Lists.newArrayList();
    /**
     * The Name of the Current Biome the Player is in
     */
    private String CURRENT_BIOME_NAME;
    /**
     * The alternative name for the Current Biome the Player is in, if any
     */
    private String CURRENT_BIOME_IDENTIFIER;

    @Override
    public void emptyData() {
        hasScanned = false;
        BIOME_NAMES.clear();
        BIOME_TYPES.clear();
        clearClientData();
    }

    @Override
    public void clearClientData() {
        CURRENT_BIOME_NAME = null;
        CURRENT_BIOME_IDENTIFIER = null;

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("biome");
        CraftPresence.CLIENT.clearOverride("biome.message", "biome.icon");
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.generalSettings.detectBiomeData : enabled;
        final boolean needsUpdate = enabled && !hasScanned;

        if (needsUpdate) {
            new Thread(this::getAllData, "CraftPresence-Biome-Lookup").start();
            hasScanned = true;
        }

        if (enabled) {
            if (CraftPresence.player != null) {
                setInUse(true);
                updateData();
            } else if (isInUse()) {
                clearClientData();
            }
        } else if (isInUse()) {
            emptyData();
        }
    }

    @Override
    public void updateData() {
        final Biome newBiome = CraftPresence.player.world.getBiome(CraftPresence.player.getPosition());
        final String newBiomeName = StringUtils.formatIdentifier(newBiome.getBiomeName(), false, !CraftPresence.CONFIG.advancedSettings.formatWords);

        final String newBiome_primaryIdentifier = StringUtils.formatIdentifier(newBiome.getBiomeName(), true, !CraftPresence.CONFIG.advancedSettings.formatWords);
        final String newBiome_alternativeIdentifier = StringUtils.formatIdentifier(MappingUtils.getClassName(newBiome), true, !CraftPresence.CONFIG.advancedSettings.formatWords);
        final String newBiome_Identifier = StringUtils.getOrDefault(newBiome_primaryIdentifier, newBiome_alternativeIdentifier);

        if (!newBiomeName.equals(CURRENT_BIOME_NAME) || !newBiome_Identifier.equals(CURRENT_BIOME_IDENTIFIER)) {
            CURRENT_BIOME_NAME = StringUtils.getOrDefault(newBiomeName, newBiome_Identifier);
            CURRENT_BIOME_IDENTIFIER = newBiome_Identifier;

            if (!BIOME_NAMES.contains(newBiome_Identifier)) {
                BIOME_NAMES.add(newBiome_Identifier);
            }
            if (!BIOME_TYPES.contains(newBiome)) {
                BIOME_TYPES.add(newBiome);
            }

            updatePresence();
        }
    }

    @Override
    public void updatePresence() {
        final ModuleData defaultData = CraftPresence.CONFIG.biomeSettings.biomeData.get("default");
        final ModuleData currentData = CraftPresence.CONFIG.biomeSettings.biomeData.get(CURRENT_BIOME_IDENTIFIER);

        final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage;
        final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_BIOME_IDENTIFIER;
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
        final String formattedIcon = StringUtils.formatAsIcon(currentIcon.replace(" ", "_"));

        CraftPresence.CLIENT.syncArgument("biome.default.icon", CraftPresence.CONFIG.biomeSettings.fallbackBiomeIcon);

        CraftPresence.CLIENT.syncArgument("biome.name", CURRENT_BIOME_NAME);

        CraftPresence.CLIENT.syncOverride(currentData != null ? currentData : defaultData, "biome.message", "biome.icon");
        CraftPresence.CLIENT.syncArgument("biome.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("biome.icon", CraftPresence.CLIENT.imageOf("biome.icon", true, formattedIcon, CraftPresence.CONFIG.biomeSettings.fallbackBiomeIcon));
    }

    /**
     * Retrieves a List of detected Biome Types
     *
     * @return The detected Biome Types found
     */
    private List<Biome> getBiomeTypes() {
        List<Biome> biomeTypes = Lists.newArrayList();

        if (Biome.REGISTRY != null) {
            for (Biome biome : Biome.REGISTRY) {
                if (biome != null && !biomeTypes.contains(biome)) {
                    biomeTypes.add(biome);
                }
            }
        }

        if (biomeTypes.isEmpty()) {
            // Fallback: Use Manual Class Lookup
            for (Class<?> classObj : FileUtils.getClassNamesMatchingSuperType(Biome.class, CraftPresence.CONFIG.advancedSettings.includeExtraGuiClasses)) {
                if (classObj != null) {
                    try {
                        Biome biomeObj = (Biome) classObj.getDeclaredConstructor().newInstance();
                        if (!biomeTypes.contains(biomeObj)) {
                            biomeTypes.add(biomeObj);
                        }
                    } catch (Throwable ex) {
                        if (ModUtils.IS_VERBOSE) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        return biomeTypes;
    }

    @Override
    public void getAllData() {
        for (Biome biome : getBiomeTypes()) {
            if (biome != null) {
                String biomeName = StringUtils.getOrDefault(biome.getBiomeName(), MappingUtils.getClassName(biome));
                String name = StringUtils.formatIdentifier(biomeName, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!BIOME_NAMES.contains(name)) {
                    BIOME_NAMES.add(name);
                }
                if (!BIOME_TYPES.contains(biome)) {
                    BIOME_TYPES.add(biome);
                }
            }
        }

        for (String biomeEntry : CraftPresence.CONFIG.biomeSettings.biomeData.keySet()) {
            if (!StringUtils.isNullOrEmpty(biomeEntry)) {
                String name = StringUtils.formatIdentifier(biomeEntry, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!BIOME_NAMES.contains(name)) {
                    BIOME_NAMES.add(name);
                }
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
        this.isInUse = state;
    }
}
