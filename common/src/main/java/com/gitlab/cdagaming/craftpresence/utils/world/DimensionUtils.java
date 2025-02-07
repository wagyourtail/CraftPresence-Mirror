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

package com.gitlab.cdagaming.craftpresence.utils.world;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.config.Config;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.element.ModuleData;
import com.gitlab.cdagaming.craftpresence.core.impl.Module;
import io.github.cdagaming.unicore.utils.FileUtils;
import io.github.cdagaming.unicore.utils.MappingUtils;
import io.github.cdagaming.unicore.utils.StringUtils;
import io.github.classgraph.ClassInfo;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;

import java.util.List;
import java.util.Map;

/**
 * Dimension Utilities used to Parse Dimension Data and handle related RPC Events
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class DimensionUtils implements Module {
    /**
     * Whether this module is allowed to start and enabled
     */
    public boolean enabled = false;
    /**
     * A List of the detected Dimension Names
     */
    public List<String> DIMENSION_NAMES = StringUtils.newArrayList();
    /**
     * A List of the default detected Dimension Names
     */
    public List<String> DEFAULT_NAMES = StringUtils.newArrayList();
    /**
     * Whether this module is active and currently in use
     */
    private boolean isInUse = false;
    /**
     * Whether this module has performed an initial retrieval of config items
     */
    private boolean hasScannedConfig = false;
    /**
     * Whether this module has performed an initial retrieval of internal items
     */
    private boolean hasScannedInternals = false;
    /**
     * The Name of the Current Dimension the Player is in
     */
    private String CURRENT_DIMENSION_NAME;
    /**
     * The alternative name for the Current Dimension the Player is in, if any
     */
    private String CURRENT_DIMENSION_IDENTIFIER;
    /**
     * The Player's Current Dimension, if any
     */
    private WorldProvider CURRENT_DIMENSION;

    @Override
    public void emptyData() {
        queueConfigScan();
        queueInternalScan();
        DEFAULT_NAMES.clear();
        DIMENSION_NAMES.clear();
        clearClientData();
    }

    @Override
    public void clearClientData() {
        CURRENT_DIMENSION = null;
        CURRENT_DIMENSION_NAME = null;
        CURRENT_DIMENSION_IDENTIFIER = null;

        setInUse(false);
        CraftPresence.CLIENT.removeArguments("dimension", "data.dimension");
        CraftPresence.CLIENT.clearOverride("dimension.message", "dimension.icon");
    }

    @Override
    public void onTick() {
        enabled = !CraftPresence.CONFIG.hasChanged ? CraftPresence.CONFIG.generalSettings.detectDimensionData : enabled;
        final boolean needsConfigUpdate = enabled && !hasScannedConfig() && canFetchConfig();
        final boolean needsInternalUpdate = enabled && !hasScannedInternals() && canFetchInternals();

        if (needsConfigUpdate) {
            scanConfigData();
            hasScannedConfig = true;
        }
        if (needsInternalUpdate) {
            scanInternalData();
            hasScannedInternals = true;
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
        final WorldProvider newProvider = CraftPresence.player.world.provider;
        final DimensionType newDimensionType = newProvider.getDimensionType();
        final String newDimensionName = StringUtils.formatIdentifier(newDimensionType.getName(), false, !CraftPresence.CONFIG.advancedSettings.formatWords);

        final String newDimension_primaryIdentifier = StringUtils.formatIdentifier(newDimensionType.getName(), true, !CraftPresence.CONFIG.advancedSettings.formatWords);
        final String newDimension_alternativeIdentifier = StringUtils.formatIdentifier(MappingUtils.getClassName(newProvider), true, !CraftPresence.CONFIG.advancedSettings.formatWords);
        final String newDimension_Identifier = StringUtils.getOrDefault(newDimension_primaryIdentifier, newDimension_alternativeIdentifier);

        if (!newDimensionName.equals(CURRENT_DIMENSION_NAME) || !newDimension_Identifier.equals(CURRENT_DIMENSION_IDENTIFIER)) {
            CURRENT_DIMENSION = newProvider;
            CURRENT_DIMENSION_NAME = StringUtils.getOrDefault(newDimensionName, newDimension_Identifier);
            CURRENT_DIMENSION_IDENTIFIER = newDimension_Identifier;

            if (!DEFAULT_NAMES.contains(newDimension_Identifier)) {
                DEFAULT_NAMES.add(newDimension_Identifier);
            }
            if (!DIMENSION_NAMES.contains(newDimension_Identifier)) {
                DIMENSION_NAMES.add(newDimension_Identifier);
            }

            updatePresence();
        }
    }

    @Override
    public void updatePresence() {
        // Form Dimension Argument List
        final ModuleData defaultData = CraftPresence.CONFIG.dimensionSettings.dimensionData.get("default");
        final ModuleData currentData = CraftPresence.CONFIG.dimensionSettings.dimensionData.get(CURRENT_DIMENSION_IDENTIFIER);

        final String defaultMessage = Config.isValidProperty(defaultData, "textOverride") ? defaultData.getTextOverride() : "";
        final String currentMessage = Config.isValidProperty(currentData, "textOverride") ? currentData.getTextOverride() : defaultMessage;
        final String defaultIcon = Config.isValidProperty(defaultData, "iconOverride") ? defaultData.getIconOverride() : CURRENT_DIMENSION_IDENTIFIER;
        final String currentIcon = Config.isValidProperty(currentData, "iconOverride") ? currentData.getIconOverride() : defaultIcon;
        final String formattedIcon = CraftPresence.CLIENT.imageOf("dimension.icon", true, currentIcon, CraftPresence.CONFIG.dimensionSettings.fallbackDimensionIcon);

        CraftPresence.CLIENT.syncArgument("dimension.default.icon", CraftPresence.CONFIG.dimensionSettings.fallbackDimensionIcon);

        CraftPresence.CLIENT.syncArgument("data.dimension.instance", CURRENT_DIMENSION);
        CraftPresence.CLIENT.syncArgument("data.dimension.class", CURRENT_DIMENSION.getClass());
        CraftPresence.CLIENT.syncArgument("dimension.name", CURRENT_DIMENSION_NAME);

        CraftPresence.CLIENT.syncOverride(currentData != null ? currentData : defaultData, "dimension.message", "dimension.icon");
        CraftPresence.CLIENT.syncArgument("dimension.message", currentMessage);
        CraftPresence.CLIENT.syncArgument("dimension.icon", formattedIcon);
        CraftPresence.CLIENT.syncTimestamp("data.dimension.time");
    }

    /**
     * Retrieves a List of detected Dimension Types
     *
     * @return The detected Dimension Types found
     */
    private List<DimensionType> getDimensionTypes() {
        List<DimensionType> dimensionTypes = StringUtils.newArrayList();

        StringUtils.addEntriesNotPresent(dimensionTypes, DimensionType.values());

        if (dimensionTypes.isEmpty()) {
            // Fallback 1: Use Reflected Dimension Types
            Map<?, ?> reflectedDimensionTypes = (Map<?, ?>) StringUtils.getField(DimensionType.class, null, "dimensionTypes");
            if (reflectedDimensionTypes != null) {
                for (Object objectType : reflectedDimensionTypes.values()) {
                    DimensionType type = (objectType instanceof DimensionType) ? (DimensionType) objectType : null;

                    if (type != null && !dimensionTypes.contains(type)) {
                        dimensionTypes.add(type);
                    }
                }
            } else if (FileUtils.isClassGraphEnabled()) {
                // Fallback 2: Use Manual Class Lookup
                for (ClassInfo classInfo : FileUtils.getClassNamesMatchingSuperType(WorldProvider.class).values()) {
                    if (classInfo != null) {
                        try {
                            Class<?> classObj = FileUtils.loadClass(classInfo.getName());
                            if (classObj != null) {
                                WorldProvider providerObj = (WorldProvider) classObj.getDeclaredConstructor().newInstance();
                                if (!dimensionTypes.contains(providerObj.getDimensionType())) {
                                    dimensionTypes.add(providerObj.getDimensionType());
                                }
                            }
                        } catch (Throwable ex) {
                            Constants.LOG.debugError(ex);
                        }
                    }
                }
            }
        }

        return dimensionTypes;
    }

    @Override
    public void getInternalData() {
        for (DimensionType TYPE : getDimensionTypes()) {
            if (TYPE != null) {
                String dimensionName = StringUtils.getOrDefault(TYPE.getName(), MappingUtils.getClassName(TYPE));
                String name = StringUtils.formatIdentifier(dimensionName, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!DEFAULT_NAMES.contains(name)) {
                    DEFAULT_NAMES.add(name);
                }
                if (!DIMENSION_NAMES.contains(name)) {
                    DIMENSION_NAMES.add(name);
                }
            }
        }
    }

    @Override
    public void getConfigData() {
        for (String dimensionEntry : CraftPresence.CONFIG.dimensionSettings.dimensionData.keySet()) {
            if (!StringUtils.isNullOrEmpty(dimensionEntry)) {
                String name = StringUtils.formatIdentifier(dimensionEntry, true, !CraftPresence.CONFIG.advancedSettings.formatWords);
                if (!DIMENSION_NAMES.contains(name)) {
                    DIMENSION_NAMES.add(name);
                }
            }
        }
    }

    @Override
    public boolean canFetchInternals() {
        return MappingUtils.areMappingsLoaded() && (!FileUtils.isClassGraphEnabled() || FileUtils.canScanClasses());
    }

    @Override
    public boolean hasScannedInternals() {
        return hasScannedInternals;
    }

    @Override
    public void queueInternalScan() {
        hasScannedInternals = false;
    }

    @Override
    public boolean canFetchConfig() {
        return CraftPresence.CONFIG != null;
    }

    @Override
    public boolean hasScannedConfig() {
        return hasScannedConfig;
    }

    @Override
    public void queueConfigScan() {
        hasScannedConfig = false;
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
