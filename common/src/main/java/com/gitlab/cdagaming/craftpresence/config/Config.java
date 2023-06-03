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

package com.gitlab.cdagaming.craftpresence.config;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.config.category.*;
import com.gitlab.cdagaming.craftpresence.config.element.*;
import com.gitlab.cdagaming.craftpresence.config.migration.HypherConverter;
import com.gitlab.cdagaming.craftpresence.config.migration.Legacy2Modern;
import com.gitlab.cdagaming.craftpresence.config.migration.TextReplacer;
import com.gitlab.cdagaming.craftpresence.impl.HashMapBuilder;
import com.gitlab.cdagaming.craftpresence.impl.KeyConverter;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Config extends Module implements Serializable {
    // Constants
    private static final long serialVersionUID = -4853238501768086595L;
    private static final int MC_VERSION = ModUtils.MCProtocolID;
    private static final int VERSION = 5;
    private static final List<String> keyCodeTriggers = StringUtils.newArrayList("keycode", "keybinding");
    private static final List<String> languageTriggers = StringUtils.newArrayList("language", "lang", "langId", "languageId");
    private static final Config DEFAULT = new Config().applyDefaults();
    private static final Config INSTANCE = loadOrCreate();
    public transient boolean hasChanged = false, isNewFile = false;
    // Global Settings
    public String _README = "https://gitlab.com/CDAGaming/CraftPresence/-/wikis/home";
    public String _SOURCE = "https://gitlab.com/CDAGaming/CraftPresence";
    public int _schemaVersion = 0;
    public int _lastMCVersionId = 0;
    // Other Settings
    public General generalSettings = new General();
    public Biome biomeSettings = new Biome();
    public Dimension dimensionSettings = new Dimension();
    public Server serverSettings = new Server();
    public Status statusMessages = new Status();
    public Advanced advancedSettings = new Advanced();
    public Accessibility accessibilitySettings = new Accessibility();
    public Display displaySettings = new Display();

    public Config(final Config other) {
        transferFrom(other);
    }

    public Config() {
        // N/A
    }

    public static Config getInstance() {
        return new Config(INSTANCE);
    }

    public static String getConfigPath() {
        return ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".json";
    }

    public static File getConfigFile() {
        return new File(getConfigPath());
    }

    public static Pair<Config, JsonElement> read() {
        Config config = null;
        JsonElement rawJson = null;

        try {
            config = FileUtils.getJsonData(getConfigFile(), Config.class,
                    FileUtils.Modifiers.DISABLE_ESCAPES, FileUtils.Modifiers.PRETTY_PRINT);
            rawJson = FileUtils.getJsonData(getConfigFile());
        } catch (Exception ex) {
            if (ex.getClass() != FileNotFoundException.class && ex.getClass() != NoSuchFileException.class) {
                ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.save"));
                if (CommandUtils.isVerboseMode()) {
                    ex.printStackTrace();
                }

                if (!getConfigFile().renameTo(new File(getConfigPath() + ".bak"))) {
                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.backup"));
                }
            }
        }
        return new Pair<>(config, rawJson);
    }

    public static Config loadOrCreate(final boolean forceCreate) {
        final Pair<Config, JsonElement> data = read();
        Config config = data.getFirst();
        JsonElement rawJson = data.getSecond();

        final boolean hasNoData = config == null;
        final boolean isInvalidData = !hasNoData && (forceCreate || (config._schemaVersion <= 0 || config._lastMCVersionId <= 0));
        if (hasNoData || isInvalidData) {
            config = hasNoData ? DEFAULT : config.getDefaults();
            config.isNewFile = true;
            config.hasChanged = isInvalidData;
        }

        final boolean wasNewFile = config.isNewFile;
        config.handleSync(rawJson);
        if (!forceCreate) {
            config.save();
        }
        if (wasNewFile) {
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.config.new"));
        } else {
            ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.config.save"));
        }
        return config;
    }

    public static Config loadOrCreate() {
        return loadOrCreate(false);
    }

    public static Object getProperty(final Config instance, final String... path) {
        if (instance == null) {
            return null;
        }
        return instance.getProperty(path);
    }

    public static Object getProperty(final Module instance, final String name) {
        if (instance == null) {
            return null;
        }
        return instance.getProperty(name);
    }

    public static boolean isValidProperty(final Config instance, final String... path) {
        final Object property = getProperty(instance, path);
        return property != null && !StringUtils.isNullOrEmpty(property.toString());
    }

    public static boolean isValidProperty(final Config instance, final String name) {
        return isValidProperty(instance, name.split("\\."));
    }

    public static boolean isValidProperty(final Module instance, final String name) {
        final Object property = getProperty(instance, name);
        return property != null && !StringUtils.isNullOrEmpty(property.toString());
    }

    public static int getGameVersion() {
        return MC_VERSION;
    }

    public static int getSchemaVersion() {
        return VERSION;
    }

    public Config applyDefaults(final Config config) {
        config._schemaVersion = getSchemaVersion();
        config._lastMCVersionId = getGameVersion();
        return config;
    }

    public Config applyDefaults() {
        return applyDefaults(this);
    }

    @Override
    public Config getDefaults() {
        return new Config(DEFAULT);
    }

    @Override
    public Config copy() {
        return new Config(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Config && !equals(target)) {
            final Config data = (Config) target;
            hasChanged = data.hasChanged;
            isNewFile = data.isNewFile;

            _README = data._README;
            _SOURCE = data._SOURCE;
            _schemaVersion = data._schemaVersion;
            _lastMCVersionId = data._lastMCVersionId;

            generalSettings = new General(data.generalSettings);
            biomeSettings = new Biome(data.biomeSettings);
            dimensionSettings = new Dimension(data.dimensionSettings);
            serverSettings = new Server(data.serverSettings);
            statusMessages = new Status(data.statusMessages);
            advancedSettings = new Advanced(data.advancedSettings);
            accessibilitySettings = new Accessibility(data.accessibilitySettings);
            displaySettings = new Display(data.displaySettings);
        }
    }

    public void applySettings() {
        if (hasChanged) {
            CommandUtils.reloadData(true);
            hasChanged = false;
        }
        isNewFile = false;
    }

    public void applyFrom(final Config old) {
        boolean needsReboot = false;
        if (!generalSettings.clientId.equals(old.generalSettings.clientId)) {
            needsReboot = true; // Client ID changed
        } else if (generalSettings.preferredClientLevel != old.generalSettings.preferredClientLevel) {
            needsReboot = true; // Preferred Client Level changed
        } else if (generalSettings.resetTimeOnInit != old.generalSettings.resetTimeOnInit) {
            needsReboot = true; // Reset Time On Init changed
        } else if (generalSettings.autoRegister != old.generalSettings.autoRegister) {
            needsReboot = true; // Auto Register changed
        } else if (!accessibilitySettings.languageId.equals(old.accessibilitySettings.languageId)) { // Fallback Language ID Changed
            ModUtils.TRANSLATOR.syncTranslations();
        }

        if (needsReboot) {
            CommandUtils.rebootRPC();
        }
    }

    public JsonElement handleMigrations(JsonElement rawJson, final int oldVer, final int newVer) {
        if (isNewFile) {
            final File legacyFile = new File(ModUtils.configDir + File.separator + ModUtils.MOD_ID + ".properties");
            if (legacyFile.exists()) {
                new Legacy2Modern(legacyFile, "UTF-8").apply(this, rawJson);
            } else {
                // fileVersion, configDirectories[main,server-entries]
                final Map<Integer, String> hypherionFiles = new HashMapBuilder<Integer, String>()
                        .put(0, ModUtils.configDir + File.separator)
                        .put(31, SystemUtils.USER_DIR + File.separator + "simple-rpc" + File.separator)
                        .put(32, ModUtils.configDir + File.separator + "simple-rpc" + File.separator)
                        .build();
                for (Map.Entry<Integer, String> entry : hypherionFiles.entrySet()) {
                    final File hypherionFile = new File(entry.getValue() + "simple-rpc.toml");
                    if (hypherionFile.exists()) {
                        new HypherConverter(entry).apply(this, rawJson);
                        break;
                    }
                }
            }
        }

        // Config Layers for prior existing files (Or recently made ones)
        if (!isNewFile) {
            int currentVer = oldVer;
            if (currentVer < newVer) {
                if (CommandUtils.isVerboseMode()) {
                    ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.config.outdated", currentVer, newVer));
                }

                if (MathUtils.isWithinValue(currentVer, 1, 2, true, false)) {
                    // Schema Changes (v1 -> v2)
                    //  - Property: `generalSettings.showTime` -> `displaySettings.presenceData.startTimestamp`
                    final boolean showTime = rawJson.getAsJsonObject()
                            .getAsJsonObject("generalSettings")
                            .getAsJsonPrimitive("showTime").getAsBoolean();
                    displaySettings.presenceData.startTimestamp = showTime ? "{data.general.time}" : "";
                    currentVer = 2;
                }
                if (MathUtils.isWithinValue(currentVer, 2, 3, true, false)) {
                    // Schema Changes (v2 -> v3)
                    //  - Placeholder: `world.time24` -> `world.time.format_24`
                    //  - Placeholder: `world.time12` -> `world.time.format_12`
                    //  - Placeholder: `world.day` -> `world.time.day`
                    new TextReplacer(
                            new HashMapBuilder<String, String>()
                                    .put("world.time24", "world.time.format_24")
                                    .put("world.time12", "world.time.format_12")
                                    .put("world.day", "world.time.day")
                                    .build(),
                            true,
                            true, false, true
                    ).apply(this, rawJson);
                    currentVer = 3;
                }
                if (MathUtils.isWithinValue(currentVer, 3, 4, true, false)) {
                    // Schema Changes (v3 -> v4)
                    //  - Migrate Color-Related Settings to new System
                    final JsonObject oldData = rawJson.getAsJsonObject()
                            .getAsJsonObject("accessibilitySettings");
                    final boolean showBackgroundAsDark = oldData
                            .getAsJsonPrimitive("showBackgroundAsDark").getAsBoolean();
                    final Map<String, String> propsToChange = new HashMapBuilder<String, String>()
                            .put("tooltipBackgroundColor", "tooltipBackground")
                            .put("tooltipBorderColor", "tooltipBorder")
                            .put("guiBackgroundColor", "guiBackground")
                            .put("buttonBackgroundColor", "buttonBackground")
                            .build();

                    for (Map.Entry<String, String> entry : propsToChange.entrySet()) {
                        final String oldValue = oldData.getAsJsonPrimitive(entry.getKey()).getAsString();
                        final ColorData newValue = new ColorData();

                        if (!StringUtils.isNullOrEmpty(oldValue)) {
                            if (StringUtils.isValidColorCode(oldValue)) {
                                final ColorSection startColor = new ColorSection(
                                        StringUtils.findColor(oldValue)
                                );
                                newValue.setStartColor(startColor);

                                if (entry.getKey().equalsIgnoreCase("tooltipBorderColor")) {
                                    final int borderColorCode = startColor.getColor().getRGB();
                                    final String borderColorEnd = Integer.toString((borderColorCode & 0xFEFEFE) >> 1 | borderColorCode & 0xFF000000);
                                    newValue.setEndColor(new ColorSection(
                                            StringUtils.findColor(borderColorEnd)
                                    ));
                                }
                            } else {
                                final boolean applyTint = showBackgroundAsDark && entry.getKey().equalsIgnoreCase("guiBackgroundColor");
                                if (applyTint) {
                                    newValue.setStartColor(
                                            new ColorSection(64, 64, 64, 255)
                                    );
                                }
                                newValue.setTexLocation(oldValue);
                            }
                        }

                        accessibilitySettings.setProperty(entry.getValue(), newValue);
                    }
                    currentVer = 4;
                }
                if (MathUtils.isWithinValue(currentVer, 4, 5, true, false)) {
                    // Schema Changes (v2 -> v3)
                    //  - Placeholder: `data.screen.class` -> `getClass(data.screen.instance)`
                    new TextReplacer(
                            new HashMapBuilder<String, String>()
                                    .put("data.screen.class", "getClass(data.screen.instance)")
                                    .build(),
                            true,
                            true, false, true
                    ).apply(this, rawJson);
                    currentVer = 5;
                }

                save();
            }

            // Refresh the raw json contents, in case of any changes
            rawJson = read().getSecond();
        }
        return rawJson;
    }

    public JsonElement handleVerification(final JsonElement rawJson, final KeyConverter.ConversionMode keyCodeMigrationId, final TranslationUtils.ConversionMode languageMigrationId, final String... path) {
        // Verify Type Safety, reset value if anything is null or invalid for it's type
        String pathPrefix = StringUtils.join(".", Arrays.asList(path));
        if (!StringUtils.isNullOrEmpty(pathPrefix)) {
            pathPrefix += ".";
        }

        if (rawJson != null) {
            final Object parentValue = getProperty(path);
            for (Map.Entry<String, JsonElement> entry : rawJson.getAsJsonObject().entrySet()) {
                final String rawName = pathPrefix + entry.getKey();
                final List<String> configPath = StringUtils.newArrayList(path);
                configPath.add(entry.getKey());
                final String[] pathData = configPath.toArray(new String[0]);

                final JsonElement rawValue = entry.getValue();
                Object defaultValue = getDefaults().getProperty(pathData);
                Object currentValue = getProperty(pathData);
                boolean shouldReset = false, shouldContinue = true;

                if (defaultValue == null) {
                    if (currentValue == null || !(parentValue instanceof ColorData || parentValue instanceof ColorSection || parentValue instanceof PresenceData || parentValue instanceof ModuleData || parentValue instanceof Button)) {
                        ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.invalid", rawName));
                        shouldContinue = false;
                    } else {
                        defaultValue = currentValue;
                    }
                }

                if (shouldContinue) {
                    if (Module.class.isAssignableFrom(defaultValue.getClass())) {
                        final List<String> paths = StringUtils.newArrayList(path);
                        paths.add(entry.getKey());
                        handleVerification(entry.getValue(), keyCodeMigrationId, languageMigrationId, paths.toArray(new String[0]));
                    } else if (!rawName.contains("presence")) { // Avoidance Filter
                        if (!StringUtils.isNullOrEmpty(defaultValue.toString()) && StringUtils.isNullOrEmpty(currentValue.toString())) {
                            shouldReset = true;
                        } else {
                            final Class<?> expectedClass = currentValue.getClass();
                            if (expectedClass == Boolean.class &&
                                    !StringUtils.isValidBoolean(rawValue.getAsString())) {
                                shouldReset = true;
                            } else if (expectedClass == Integer.class) {
                                final Pair<Boolean, Integer> boolData = StringUtils.getValidInteger(rawValue.getAsString());
                                if (boolData.getFirst()) {
                                    // This check will trigger if the Field Name contains KeyCode Triggers
                                    // If the Property Name contains these values, move onwards
                                    for (String keyTrigger : keyCodeTriggers) {
                                        if (rawName.toLowerCase().contains(keyTrigger.toLowerCase())) {
                                            if (!KeyUtils.isValidKeyCode(boolData.getSecond())) {
                                                shouldReset = true;
                                            } else if (keyCodeMigrationId != KeyConverter.ConversionMode.Unknown) {
                                                final int migratedKeyCode = KeyConverter.convertKey(boolData.getSecond(), keyCodeMigrationId);
                                                if (migratedKeyCode != boolData.getSecond()) {
                                                    ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.apply", "KEYCODE", keyCodeMigrationId.name(), rawName, boolData.getSecond(), migratedKeyCode));
                                                    setProperty(migratedKeyCode, pathData);
                                                }
                                            }
                                            break;
                                        }
                                    }
                                } else {
                                    shouldReset = true;
                                }
                            } else if (currentValue instanceof Map<?, ?>) {
                                final Map<Object, Object> newData = StringUtils.newHashMap((Map<?, ?>) currentValue);
                                final Map<Object, Object> defaultData = StringUtils.newHashMap((Map<?, ?>) defaultValue);
                                if (!newData.containsKey("default")) {
                                    ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.missing.default", rawName));
                                    newData.putAll(defaultData);
                                    setProperty(newData, pathData);
                                } else if (entry.getValue().isJsonObject()) {
                                    for (Object dataEntry : newData.keySet()) {
                                        final List<String> paths = StringUtils.newArrayList(path);
                                        paths.add(entry.getKey());
                                        paths.add(dataEntry.toString());
                                        final JsonElement dataValue = entry.getValue().getAsJsonObject().get(dataEntry.toString());
                                        if (dataValue.isJsonObject()) {
                                            handleVerification(dataValue, keyCodeMigrationId, languageMigrationId, paths.toArray(new String[0]));
                                        }
                                    }
                                }
                            } else if (rawValue.isJsonPrimitive()) {
                                final String rawStringValue = rawValue.getAsString();
                                // This check will trigger if the Field Name contains Language Identifier Triggers
                                // If the Property Name contains these values, move onwards
                                for (String langTrigger : languageTriggers) {
                                    if (rawName.toLowerCase().contains(langTrigger.toLowerCase())) {
                                        if (languageMigrationId != TranslationUtils.ConversionMode.Unknown) {
                                            final String migratedLanguageId = TranslationUtils.convertId(rawStringValue, languageMigrationId);
                                            if (!migratedLanguageId.equals(rawStringValue)) {
                                                ModUtils.LOG.info(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.apply", "LANGUAGE", languageMigrationId.name(), rawName, rawStringValue, migratedLanguageId));
                                                setProperty((Object) migratedLanguageId, pathData);
                                            }
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        if (shouldReset) {
                            ModUtils.LOG.error(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.error.config.prop.empty", rawName));
                            resetProperty(pathData);
                        }
                    }
                }
            }
        }
        return rawJson;
    }

    public JsonElement handleSync(JsonElement rawJson) {
        final int newSchemaVer = getSchemaVersion();
        if (isNewFile || _schemaVersion != newSchemaVer) {
            int oldVer = _schemaVersion;
            rawJson = handleMigrations(rawJson, oldVer, newSchemaVer);
            _schemaVersion = newSchemaVer;
        }
        final int oldMCVer = _lastMCVersionId;
        final int newMCVer = getGameVersion();
        if (oldMCVer != newMCVer) {
            _lastMCVersionId = newMCVer;
        }

        // Sync Flag Data
        if (ModUtils.IS_TEXT_COLORS_BLOCKED) {
            accessibilitySettings.stripTranslationColors = true;
        }

        // Sync Migration Data for later usage
        final KeyConverter.ConversionMode keyCodeMigrationId;
        final TranslationUtils.ConversionMode languageMigrationId;

        // Case 1 Notes (KeyCode):
        // In this situation, if the currently parsed protocol version differs and
        // is a newer version then or exactly 17w43a (1.13, 341), then
        // we need to ensure any keycode assignments are in an LWJGL 3 format.
        // Otherwise, if our current protocol version is anything less then 17w43a (1.13, 341),
        // we need to ensure any keycode assignments are in an LWJGL 2 format.
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (oldMCVer < 341 && newMCVer >= 341) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl3;
        } else if (oldMCVer >= 341 && newMCVer < 341) {
            keyCodeMigrationId = KeyConverter.ConversionMode.Lwjgl2;
        } else if (oldMCVer >= 0 && newMCVer >= 0) {
            keyCodeMigrationId = KeyConverter.ConversionMode.None;
        } else {
            keyCodeMigrationId = KeyConverter.ConversionMode.Unknown;
        }

        // Case 2 Notes (Language ID):
        // In this situation, if the currently parsed protocol version differs and
        // is a newer version then or exactly 16w32a (1.11, 301), then
        // we need to ensure any Language Locale's are complying with Pack Format 3 and above.
        // Otherwise, if our current protocol version is anything less then 16w32a (1.11, 301),
        // we need to ensure any Language Locale's are complying with Pack Format 2 and below.
        // If neither is true, then we mark the migration data as None, and it will be verified
        if (oldMCVer < 301 && newMCVer >= 301) {
            languageMigrationId = TranslationUtils.ConversionMode.PackFormat3;
        } else if (oldMCVer >= 301 && newMCVer < 301) {
            languageMigrationId = TranslationUtils.ConversionMode.PackFormat2;
        } else if (oldMCVer >= 0 && newMCVer >= 0) {
            languageMigrationId = TranslationUtils.ConversionMode.None;
        } else {
            languageMigrationId = TranslationUtils.ConversionMode.Unknown;
        }

        ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.add", keyCodeTriggers.toString(), keyCodeMigrationId, keyCodeMigrationId.equals(KeyConverter.ConversionMode.None) ? "Verification" : "Setting Change"));
        ModUtils.LOG.debugInfo(ModUtils.TRANSLATOR.translate(true, "craftpresence.logger.info.migration.add", languageTriggers.toString(), languageMigrationId, languageMigrationId.equals(TranslationUtils.ConversionMode.None) ? "Verification" : "Setting Change"));
        return !isNewFile ? handleVerification(rawJson, keyCodeMigrationId, languageMigrationId) : rawJson;
    }

    public void save(final boolean shouldApply) {
        FileUtils.writeJsonData(this, getConfigFile(), "UTF-8",
                FileUtils.Modifiers.DISABLE_ESCAPES, FileUtils.Modifiers.PRETTY_PRINT);
        if (shouldApply) {
            applySettings();
        }
    }

    public void save() {
        save(true);
    }

    public Pair<Object, Tuple<Class<?>, Object, String>> lookupProperty(final String... path) {
        Class<?> classObj = Config.class;
        Object instance = this;
        Object result = null;

        String name = null;
        for (int i = 0; i < path.length; i++) {
            if (!StringUtils.isNullOrEmpty(path[i])) {
                name = path[i];
                if (instance instanceof Map<?, ?>) {
                    result = StringUtils.newHashMap((Map<?, ?>) instance).get(name);
                } else {
                    result = StringUtils.getField(classObj, instance, name);
                }
                if (result != null) {
                    if (i < path.length - 1) {
                        classObj = result.getClass();
                        instance = result;
                    }
                } else {
                    break;
                }
            }
        }
        return new Pair<>(result, new Tuple<>(classObj, instance, name));
    }

    public Object getProperty(final String... path) {
        return lookupProperty(path).getFirst();
    }

    @Override
    public Object getProperty(final String name) {
        return getProperty(name.split("\\."));
    }

    public void setProperty(final Object value, final String... path) {
        final Pair<Object, Tuple<Class<?>, Object, String>> propertyData = lookupProperty(path);
        if (propertyData.getFirst() != null) {
            final Tuple<Class<?>, Object, String> fieldData = propertyData.getSecond();
            if (fieldData.getSecond() instanceof Map<?, ?>) {
                final String[] parentPath = Arrays.copyOf(path, path.length - 1);
                final Tuple<Class<?>, Object, String> parentData = lookupProperty(parentPath).getSecond();

                Map<Object, Object> data = StringUtils.newHashMap((Map<?, ?>) fieldData.getSecond());
                data.put(fieldData.getThird(), value);

                StringUtils.updateField(parentData.getFirst(), parentData.getSecond(), data, parentData.getThird());
            } else {
                StringUtils.updateField(fieldData.getFirst(), fieldData.getSecond(), value, fieldData.getThird());
            }
        }
    }

    @Override
    public void setProperty(final String name, final Object value) {
        setProperty(value, name.split("\\."));
    }

    public void resetProperty(final String... path) {
        setProperty(getDefaults().getProperty(path), path);
    }

    @Override
    public void resetProperty(final String name) {
        resetProperty(name.split("\\."));
    }
}
