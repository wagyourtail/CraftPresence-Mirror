package com.gitlab.cdagaming.craftpresence.handler;

import com.gitlab.cdagaming.craftpresence.Constants;
import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.google.common.collect.Maps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class TranslationHandler {
    public static TranslationHandler instance;
    public boolean isUnicode = false;
    private String languageID = "en_US", modID;
    private Map<String, String> translationMap = Maps.newHashMap();
    private Map<String, Boolean> requestMap = Maps.newHashMap();
    private boolean usingJSON = false;

    public TranslationHandler() {
        setLanguage(CraftPresence.CONFIG != null ? CraftPresence.CONFIG.languageID : languageID);
        setUsingJSON(false);
        getTranslationMap();
        checkUnicode();
    }

    public TranslationHandler(final boolean useJSON) {
        setLanguage(CraftPresence.CONFIG != null ? CraftPresence.CONFIG.languageID : languageID);
        setUsingJSON(useJSON);
        getTranslationMap();
        checkUnicode();
    }

    public TranslationHandler(final String modID) {
        setLanguage(CraftPresence.CONFIG != null ? CraftPresence.CONFIG.languageID : languageID);
        setModID(modID);
        setUsingJSON(false);
        getTranslationMap();
        checkUnicode();
    }

    public TranslationHandler(final String modID, final boolean useJSON) {
        setLanguage(CraftPresence.CONFIG != null ? CraftPresence.CONFIG.languageID : languageID);
        setModID(modID);
        setUsingJSON(useJSON);
        getTranslationMap();
        checkUnicode();
    }

    void tick() {
        if (CraftPresence.CONFIG != null && !languageID.equals(CraftPresence.CONFIG.languageID) &&
                (!requestMap.containsKey(CraftPresence.CONFIG.languageID) || requestMap.get(CraftPresence.CONFIG.languageID))) {
            setLanguage(CraftPresence.CONFIG.languageID);
            getTranslationMap();
            checkUnicode();
        }

        if (CraftPresence.instance.gameSettings != null && isUnicode != CraftPresence.instance.gameSettings.forceUnicodeFont) {
            checkUnicode();
        }
    }

    private void checkUnicode() {
        isUnicode = false;
        int i = 0;
        int totalLength = 0;

        for (String currentString : translationMap.values()) {
            int currentLength = currentString.length();
            totalLength += currentLength;

            for (int index = 0; index < currentLength; ++index) {
                if (currentString.charAt(index) >= 256) {
                    ++i;
                }
            }
        }

        float f = (float) i / (float) totalLength;
        isUnicode = (double) f > 0.1D || (CraftPresence.instance.gameSettings != null && CraftPresence.instance.gameSettings.forceUnicodeFont);
    }

    private void setUsingJSON(final boolean usingJSON) {
        this.usingJSON = usingJSON;
    }

    private void setLanguage(final String languageID) {
        if (!StringHandler.isNullOrEmpty(languageID)) {
            this.languageID = languageID;
        } else {
            this.languageID = "en_US";
        }
    }

    private void setModID(final String modID) {
        if (!StringHandler.isNullOrEmpty(modID)) {
            this.modID = modID;
        } else {
            this.modID = null;
        }
    }

    private void getTranslationMap() {
        translationMap = Maps.newHashMap();

        InputStream in = StringHandler.getResourceAsStream(TranslationHandler.class, "/assets/"
                + (!StringHandler.isNullOrEmpty(modID) ? modID + "/" : "") +
                "lang/" + languageID + (usingJSON ? ".json" : ".lang"));
        InputStream fallbackIn = StringHandler.getResourceAsStream(TranslationHandler.class, "/assets/"
                + (!StringHandler.isNullOrEmpty(modID) ? modID + "/" : "") +
                "lang/" + languageID.toLowerCase() + (usingJSON ? ".json" : ".lang"));

        if (in != null || fallbackIn != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in != null ? in : fallbackIn));
            try {
                String currentString;
                while ((currentString = reader.readLine()) != null) {
                    currentString = currentString.trim();
                    if (!currentString.startsWith("#") && !currentString.startsWith("[{}]") && (usingJSON ? currentString.contains(":") : currentString.contains("="))) {
                        String[] splitTranslation = usingJSON ? currentString.split(":", 2) : currentString.split("=", 2);
                        if (usingJSON) {
                            String str1 = splitTranslation[0].substring(1, splitTranslation[0].length() - 1).replace("\\n", "\n").replace("\\", "").trim();
                            String str2 = splitTranslation[1].substring(2, splitTranslation[1].length() - 2).replace("\\n", "\n").replace("\\", "").trim();
                            translationMap.put(str1.replace("Â", ""), str2.replace("Â", ""));
                        } else {
                            translationMap.put(splitTranslation[0].trim().replace("Â", ""), splitTranslation[1].trim().replace("Â", ""));
                        }
                    }
                }

                if (in != null) {
                    in.close();
                }
                if (fallbackIn != null) {
                    fallbackIn.close();
                }
            } catch (Exception ex) {
                Constants.LOG.error("An Exception has Occurred while Loading Translation Mappings, Things may not work well...");
                ex.printStackTrace();
            }
        } else {
            Constants.LOG.error("Translations for " + modID + " do not exist for " + languageID);
            requestMap.put(languageID, false);
            setLanguage("en_US");
        }
    }

    public String translate(String translationKey, Object... parameters) {
        boolean hasErrored = false;
        String translatedString = translationKey;
        try {
            if (translationMap.containsKey(translationKey)) {
                translatedString = String.format(translationMap.get(translationKey), parameters);
            } else {
                hasErrored = true;
            }
        } catch (Exception ex) {
            Constants.LOG.error("Exception Parsing " + translationKey);
            ex.printStackTrace();
            hasErrored = true;
        }

        if (hasErrored) {
            Constants.LOG.error("Unable to retrieve a Translation for " + translationKey);
        }
        return translatedString;
    }
}
