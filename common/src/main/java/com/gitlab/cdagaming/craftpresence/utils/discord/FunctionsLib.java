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

package com.gitlab.cdagaming.craftpresence.utils.discord;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.utils.FileUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.discord.assets.DiscordAssetUtils;
import com.google.common.collect.Lists;
import org.meteordev.starscript.StandardLib;
import org.meteordev.starscript.Starscript;
import org.meteordev.starscript.value.Value;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Standard library with some default functions and variables.
 *
 * @author CDAGaming
 */
public class FunctionsLib {
    public static void init(Starscript ss) {
        StandardLib.init(ss);

        // General Functions
        ss.set("randomAsset", FunctionsLib::randomAsset);
        ss.set("randomString", FunctionsLib::randomString);

        // DiscordUtils
        ss.set("getResult", FunctionsLib::getResult);

        // StringUtils
        ss.set("rgbaToHex", FunctionsLib::rgbaToHex);
        ss.set("getOrDefault", FunctionsLib::getOrDefault);
        ss.set("replaceAnyCase", FunctionsLib::replaceAnyCase);
        ss.set("length", FunctionsLib::length);
        ss.set("minify", FunctionsLib::minify);
        ss.set("nullOrEmpty", FunctionsLib::nullOrEmpty);
        ss.set("formatAddress", FunctionsLib::formatAddress);
        ss.set("hasWhitespace", FunctionsLib::hasWhitespace);
        ss.set("hasAlphaNumeric", FunctionsLib::hasAlphaNumeric);
        ss.set("isUuid", FunctionsLib::isUuid);
        ss.set("isColor", FunctionsLib::isColor);
        ss.set("toCamelCase", FunctionsLib::toCamelCase);
        ss.set("asIcon", FunctionsLib::asIcon);
        ss.set("asProperWord", FunctionsLib::asProperWord);
        ss.set("removeRepeatWords", FunctionsLib::removeRepeatWords);
        ss.set("asIdentifier", FunctionsLib::asIdentifier);
        ss.set("convertTime", FunctionsLib::convertTime);
        ss.set("capitalizeWords", FunctionsLib::capitalizeWords);
        ss.set("getField", FunctionsLib::getField);
        ss.set("getClass", FunctionsLib::getClass);
        ss.set("hasField", FunctionsLib::hasField);
        ss.set("stripColors", FunctionsLib::stripColors);
    }

    public static Value getResult(Starscript ss, int argCount) {
        if (argCount != 1)
            ss.error("randomString() can only be used with one argument, got %d.", argCount);
        return Value.string(CraftPresence.CLIENT.getResult(ss.pop().toString()));
    }

    public static Value randomAsset(Starscript ss, int argCount) {
        return Value.string(DiscordAssetUtils.getRandomAssetName());
    }

    public static Value randomString(Starscript ss, int argCount) {
        final List<String> args = Lists.newArrayList();
        if (argCount < 1)
            ss.error("randomString() requires one or more arguments, got %d.", argCount);
        for (int i = 0; i < argCount; i++) {
            args.add(ss.pop().toString());
        }
        return Value.string(args.get(new Random().nextInt(argCount)));
    }

    public static Value rgbaToHex(Starscript ss, int argCount) {
        if (argCount < 3 || argCount > 4)
            ss.error("rgbaToHex() can only be used with 3-4 arguments, got %d.", argCount);
        int a = 255;
        if (argCount == 4) {
            a = (int) ss.popNumber("Fourth argument to rgbaToHex() needs to be a number.");
            if (a < 0 || a > 255) {
                ss.error("Fourth argument to rgbaToHex() is not a valid color index, can only be 0-255.");
            }
        }
        int b = (int) ss.popNumber("Third argument to rgbaToHex() needs to be a number.");
        if (b < 0 || b > 255) {
            ss.error("Third argument to rgbaToHex() is not a valid color index, can only be 0-255.");
        }
        int g = (int) ss.popNumber("Second argument to rgbaToHex() needs to be a number.");
        if (g < 0 || g > 255) {
            ss.error("Second argument to rgbaToHex() is not a valid color index, can only be 0-255.");
        }
        int r = (int) ss.popNumber("First argument to rgbaToHex() needs to be a number.");
        if (r < 0 || r > 255) {
            ss.error("First argument to rgbaToHex() is not a valid color index, can only be 0-255.");
        }
        return Value.string(StringUtils.getHexFromColor(new Color(r, g, b, a)));
    }

    public static Value getOrDefault(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("getOrDefault() can only be used with 1-2 arguments, got %d.", argCount);
        String alternative = "";
        if (argCount == 2) {
            alternative = ss.popString("Second argument to getOrDefault() needs to be a string.");
        }
        String target = ss.popString("First argument to getOrDefault() needs to be a string.");
        return Value.string(StringUtils.getOrDefault(target, alternative));
    }

    public static Value replaceAnyCase(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("replaceAnyCase() requires 3 arguments, got %d.", argCount);
        String to = ss.popString("Third argument to replaceAnyCase() needs to be a string.");
        String from = ss.popString("Second argument to replaceAnyCase() needs to be a string.");
        String source = ss.popString("First argument to replaceAnyCase() needs to be a string.");
        return Value.string(StringUtils.replaceAnyCase(source, from, to));
    }

    public static Value length(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("length() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to length() needs to be a string.");
        return Value.number(source.length());
    }

    public static Value minify(Starscript ss, int argCount) {
        if (argCount != 2) ss.error("minify() requires 2 arguments, got %d.", argCount);
        int length = (int) ss.popNumber("Second argument to minify() needs to be a number.");
        String source = ss.popString("First argument to minify() needs to be a string.");
        return Value.string(StringUtils.minifyString(source, length));
    }

    /**
     * Determines whether a String classifies as NULL or EMPTY
     *
     * @param ss       The {@link Starscript} instance
     * @param argCount The argument count
     * @return {@code true} if Entry is classified as NULL or EMPTY
     */
    public static Value nullOrEmpty(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("nullOrEmpty() can only be used with 1-2 arguments, got %d.", argCount);
        boolean allowWhitespace = false;
        if (argCount == 2) {
            allowWhitespace = ss.popBool("Second argument to nullOrEmpty() needs to be a boolean.");
        }
        String target = ss.popString("First argument to nullOrEmpty() needs to be a string.");
        return Value.bool(StringUtils.isNullOrEmpty(target, allowWhitespace));
    }

    public static Value formatAddress(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("formatAddress() can only be used with 1-2 arguments, got %d.", argCount);
        boolean returnPort = false;
        if (argCount == 2) {
            returnPort = ss.popBool("Second argument to formatAddress() needs to be a boolean.");
        }
        String target = ss.popString("First argument to formatAddress() needs to be a string.");
        return Value.string(StringUtils.formatAddress(target, returnPort));
    }

    public static Value hasWhitespace(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("hasWhitespace() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to hasWhitespace() needs to be a string.");
        return Value.bool(StringUtils.containsWhitespace(source));
    }

    public static Value hasAlphaNumeric(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("hasAlphaNumeric() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to hasAlphaNumeric() needs to be a string.");
        return Value.bool(StringUtils.containsAlphaNumeric(source));
    }

    public static Value isUuid(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("isUuid() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to isUuid() needs to be a string.");
        return Value.bool(StringUtils.isValidUuid(source));
    }

    public static Value isColor(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("isColor() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to isColor() needs to be a string.");
        return Value.bool(StringUtils.isValidColorCode(source));
    }

    public static Value toCamelCase(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("toCamelCase() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to toCamelCase() needs to be a string.");
        return Value.string(StringUtils.formatToCamel(source));
    }

    public static Value asIcon(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("asIcon() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to asIcon() needs to be a string.");
        return Value.string(StringUtils.formatAsIcon(source));
    }

    public static Value asProperWord(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 4)
            ss.error("asProperWord() can only be used with 1-4 arguments, got %d.", argCount);
        int caseCheckTimes = -1;
        if (argCount == 4) {
            caseCheckTimes = (int) ss.popNumber("Fourth argument to asProperWord() needs to be a number.");
        }
        boolean skipSymbolReplacement = false;
        if (argCount >= 3) {
            skipSymbolReplacement = ss.popBool("Third argument to asProperWord() needs to be a boolean.");
        }
        boolean avoid = false;
        if (argCount >= 2) {
            avoid = ss.popBool("Second argument to asProperWord() needs to be a boolean.");
        }
        String target = ss.popString("First argument to asProperWord() needs to be a string.");
        return Value.string(StringUtils.formatWord(target, avoid, skipSymbolReplacement, caseCheckTimes));
    }

    public static Value removeRepeatWords(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("removeRepeatWords() requires 1 argument, got %d.", argCount);
        String source = ss.popString("First argument to removeRepeatWords() needs to be a string.");
        return Value.string(StringUtils.removeRepeatWords(source));
    }

    public static Value asIdentifier(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 3)
            ss.error("asIdentifier() can only be used with 1-3 arguments, got %d.", argCount);
        boolean avoid = false;
        if (argCount == 3) {
            avoid = ss.popBool("Third argument to asIdentifier() needs to be a boolean.");
        }
        boolean formatToId = false;
        if (argCount >= 2) {
            formatToId = ss.popBool("Second argument to asIdentifier() needs to be a boolean.");
        }
        String target = ss.popString("First argument to asIdentifier() needs to be a string.");
        return Value.string(StringUtils.formatIdentifier(target, formatToId, avoid));
    }

    public static Value convertTime(Starscript ss, int argCount) {
        if (argCount != 3) ss.error("convertTime() requires 3 arguments, got %d.", argCount);
        String newPattern = ss.popString("Third argument to convertTime() needs to be a string.");
        String originalPattern = ss.popString("Second argument to convertTime() needs to be a string.");
        String target = ss.popString("First argument to convertTime() needs to be a string.");
        return Value.string(StringUtils.convertTime(target, originalPattern, newPattern));
    }

    public static Value capitalizeWords(Starscript ss, int argCount) {
        if (argCount < 1 || argCount > 2)
            ss.error("capitalizeWords() can only be used with 1-2 arguments, got %d.", argCount);
        int timesToCheck = -1;
        if (argCount == 2) {
            timesToCheck = (int) ss.popNumber("Second argument to capitalizeWords() needs to be a number.");
        }
        String target = ss.popString("First argument to capitalizeWords() needs to be a string.");
        return Value.string(StringUtils.capitalizeWord(target, timesToCheck));
    }

    public static Value getField(Starscript ss, int argCount) {
        if (argCount < 2 || argCount > 3) ss.error("getField() can only be used with 2-3 arguments, got %d.", argCount);
        String fieldName, className;
        Object instance, result;
        if (argCount == 2) {
            fieldName = ss.popString("Second argument to getField() needs to be a string.");
            instance = ss.popObject("First argument to getField() needs to be an object.");
            result = StringUtils.lookupObject(instance.getClass(), instance, fieldName);
        } else {
            fieldName = ss.popString("Third argument to getField() needs to be a string.");
            instance = ss.popObject("Second argument to getField() needs to be an object.");
            className = ss.popString("First argument to getField() needs to be a string.");
            result = StringUtils.lookupObject(className, instance, fieldName);
        }

        return result != null ? Value.object(result) : Value.null_();
    }

    public static Value getClass(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("getClass() requires 1 argument, got %d.", argCount);
        Value value = ss.pop();
        Class<?> result = null;
        if (value.isObject()) {
            result = value.getObject().getClass();
        } else if (value.isString()) {
            result = FileUtils.findValidClass(value.getString());
        } else {
            ss.error("First argument to getClass() needs to be an object or string.");
        }
        return result != null ? Value.object(result) : Value.null_();
    }

    /**
     * Retrieves whether the specified class contains the specified field name
     *
     * @param ss       The {@link Starscript} instance
     * @param argCount The argument count
     * @return whether the specified class contains the specified field name
     */
    public static Value hasField(Starscript ss, int argCount) {
        if (argCount != 2) ss.error("hasField() requires 2 arguments, got %d.", argCount);
        String b = ss.popString("Second argument to hasField() needs to be a string.");
        String a = ss.popString("First argument to hasField() needs to be a string.");
        return Value.bool(StringUtils.doesClassContainField(a, b));
    }

    /**
     * Strips Color and Formatting Codes from the inputted String
     *
     * @param ss       The {@link Starscript} instance
     * @param argCount The argument count
     * @return whether the specified class contains the specified field name
     */
    public static Value stripColors(Starscript ss, int argCount) {
        if (argCount != 1) ss.error("stripColors() requires 1 argument, got %d.", argCount);
        String a = ss.popString("Argument to stripColors() needs to be a string.");
        return Value.string(StringUtils.stripColors(a));
    }
}
