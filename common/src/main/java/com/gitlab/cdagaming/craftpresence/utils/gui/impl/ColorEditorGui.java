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

package com.gitlab.cdagaming.craftpresence.utils.gui.impl;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Pair;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.ExtendedTextControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.controls.SliderControl;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.PaginatedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.TextWidget;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("DuplicatedCode")
public class ColorEditorGui extends PaginatedScreen {
    private final String configValueName;
    // Event Data
    private final BiConsumer<Integer, ColorEditorGui> onAdjustEntry;
    private final Consumer<ColorEditorGui> onInit;
    public String currentNormalHexValue, startingHexValue, currentTexturePath, startingTexturePath;
    public Tuple<Boolean, String, ResourceLocation> textureData = new Tuple<>(false, "", null);
    // Page 1 Variables
    private String currentConvertedHexValue;
    private int currentRed, currentGreen, currentBlue, currentAlpha;
    private ExtendedTextControl hexText;
    private SliderControl redText, greenText, blueText, alphaText;
    // Page 2 Variables
    private ExtendedTextControl textureText;
    private boolean isModified = false;
    private ResourceLocation currentTexture;

    public ColorEditorGui(GuiScreen parentScreen, String configValueName, BiConsumer<Integer, ColorEditorGui> onAdjustEntry, Consumer<ColorEditorGui> onInit) {
        super(parentScreen);
        this.configValueName = configValueName;
        this.onAdjustEntry = onAdjustEntry;
        this.onInit = onInit;
    }

    @Override
    public void initializeUi() {
        final int calc1 = (getScreenWidth() / 2) - 183;
        final int calc2 = (getScreenWidth() / 2) + 3;

        // Page 1 Items
        final String redTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.red");
        final String greenTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.green");
        final String blueTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.blue");
        final String alphaTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.color.value.alpha");

        hexText = addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        180, 20,
                        "gui.config.message.editor.hex_code"
                ), startPage
        );
        hexText.setControlMaxLength(10);

        redText = addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(2)),
                        new Pair<>(180, 20),
                        1.0f, 0.0f,
                        255.0f, 1.0f,
                        redTitle,
                        new Tuple<>(
                                this::syncValues,
                                () -> {
                                },
                                this::syncValues
                        )
                ), startPage
        );
        greenText = addControl(
                new SliderControl(
                        new Pair<>(calc2, getButtonY(2)),
                        new Pair<>(180, 20),
                        1.0f, 0.0f,
                        255.0f, 1.0f,
                        greenTitle,
                        new Tuple<>(
                                this::syncValues,
                                () -> {
                                },
                                this::syncValues
                        )
                ), startPage
        );
        blueText = addControl(
                new SliderControl(
                        new Pair<>(calc1, getButtonY(3)),
                        new Pair<>(180, 20),
                        1.0f, 0.0f,
                        255.0f, 1.0f,
                        blueTitle,
                        new Tuple<>(
                                this::syncValues,
                                () -> {
                                },
                                this::syncValues
                        )
                ), startPage
        );
        alphaText = addControl(
                new SliderControl(
                        new Pair<>(calc2, getButtonY(3)),
                        new Pair<>(180, 20),
                        1.0f, 0.0f,
                        255.0f, 1.0f,
                        alphaTitle,
                        new Tuple<>(
                                this::syncValues,
                                () -> {
                                },
                                this::syncValues
                        )
                ), startPage
        );

        // Page 2 Items
        textureText = addControl(
                new TextWidget(
                        getFontRenderer(),
                        getButtonY(1),
                        180, 20,
                        this::syncValues,
                        "gui.config.message.editor.texture_path"
                ), startPage + 1
        );
        textureText.setControlMaxLength(32767);

        initValues();
        syncValues();
        super.initializeUi();

        backButton.setOnClick(
                () -> {
                    syncValues();
                    if (isModified && onAdjustEntry != null) {
                        onAdjustEntry.accept(currentPage, this);
                    }
                    CraftPresence.GUIS.openScreen(parentScreen);
                }
        );
        setOnPageChange(
                () -> {
                    initValues();
                    syncValues();
                }
        );
    }

    @Override
    public void preRender() {
        final String mainTitle = ModUtils.TRANSLATOR.translate("gui.config.title.editor.color", configValueName);
        final String previewTitle = ModUtils.TRANSLATOR.translate("gui.config.message.editor.preview");

        renderCenteredString(mainTitle, getScreenWidth() / 2f, 10, 0xFFFFFF);
        renderString(previewTitle, getScreenWidth() - 90, getScreenHeight() - 29.5f, 0xFFFFFF);

        // Setup Data for Drawing
        double tooltipX = getScreenWidth() - 45;
        double tooltipY = getScreenHeight() - 45;
        double tooltipHeight = 40;
        double tooltipTextWidth = 40;

        String borderColor = "#000000";
        String borderColorEnd = "#000000";

        super.preRender();

        // Page 1 Items
        if (currentPage == startPage) {
            backButton.setControlEnabled(!StringUtils.isNullOrEmpty(hexText.getControlMessage()));

            // Draw Preview Box
            CraftPresence.GUIS.drawGradientRect(300, tooltipX - 3, tooltipY - 3, getScreenWidth() - 2, getScreenHeight() - 2, currentConvertedHexValue, currentConvertedHexValue);
        }

        // Page 2 Items
        if (currentPage == startPage + 1) {
            backButton.setControlEnabled(!StringUtils.isNullOrEmpty(textureText.getControlMessage()));

            if (currentTexture == null) {
                currentTexture = new ResourceLocation("");
            }

            // Ensure the Texture is refreshed consistently, if an external texture
            double widthDivider = 32.0D, heightDivider = 32.0D;

            if (textureData.getFirst()) {
                currentTexture = textureData.getThird();

                widthDivider = 44;
                heightDivider = 43;
            }

            // Draw Preview Box
            CraftPresence.GUIS.drawTextureRect(0.0D, getScreenWidth() - 47, getScreenHeight() - 47, 44, 44, 0, widthDivider, heightDivider, false, currentTexture);
        }

        // Draw Border around Preview Box
        CraftPresence.GUIS.drawGradientRect(300, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColor, borderColorEnd);
        CraftPresence.GUIS.drawGradientRect(300, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColor, borderColorEnd);
        CraftPresence.GUIS.drawGradientRect(300, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColor, borderColor);
        CraftPresence.GUIS.drawGradientRect(300, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        backButton.setControlMessage(isModified ? "gui.config.message.button.save" : "gui.config.message.button.back");
    }

    /**
     * Initialize Texture and Color Values for Initial Preview and Page
     */
    private void initValues() {
        if (onInit != null) {
            onInit.accept(this);

            if (StringUtils.isNullOrEmpty(hexText.getControlMessage()) && !StringUtils.isNullOrEmpty(startingHexValue)) {
                hexText.setControlMessage(startingHexValue);
                currentNormalHexValue = null;
                currentConvertedHexValue = null;
                currentTexturePath = null;
                textureData.put(false, "", new ResourceLocation(""));
                currentTexture = textureData.getThird();
                currentPage = startPage;
            } else if (StringUtils.isNullOrEmpty(textureText.getControlMessage()) && !StringUtils.isNullOrEmpty(startingTexturePath)) {
                textureText.setControlMessage(startingTexturePath);
                currentNormalHexValue = null;
                currentConvertedHexValue = null;
                currentTexturePath = null;
                textureData.put(false, "", new ResourceLocation(""));
                currentTexture = textureData.getThird();
                currentPage = startPage + 1;
            }
        }
    }

    /**
     * Synchronize RGBA, Hex, and Texture Settings for Preview
     */
    private void syncValues() {
        // Page 1 - RGBA / Hex Syncing
        if (currentPage == startPage) {
            Integer localValue = null;
            Color localColor;

            if (!StringUtils.isNullOrEmpty(hexText.getControlMessage())) {
                if (StringUtils.isValidColor(hexText.getControlMessage()).getFirst()) {
                    localValue = StringUtils.getColorFrom(hexText.getControlMessage()).getRGB();
                } else if (StringUtils.getValidInteger(hexText.getControlMessage()).getFirst()) {
                    localValue = Integer.decode(hexText.getControlMessage());
                }
            }

            if (localValue != null && !Integer.toString(localValue).equals(currentConvertedHexValue)) {
                currentAlpha = (localValue >> 24 & 255);
                currentRed = (localValue >> 16 & 255);
                currentGreen = (localValue >> 8 & 255);
                currentBlue = (localValue & 255);

                alphaText.setSliderValue(currentAlpha);
                redText.setSliderValue(currentRed);
                greenText.setSliderValue(currentGreen);
                blueText.setSliderValue(currentBlue);

                currentNormalHexValue = hexText.getControlMessage();
                currentConvertedHexValue = Integer.toString(localValue);
            } else {
                final boolean isRedDifferent = redText.isDragging() && redText.getSliderValue(false) != currentRed,
                        isGreenDifferent = greenText.isDragging() && greenText.getSliderValue(false) != currentGreen,
                        isBlueDifferent = blueText.isDragging() && blueText.getSliderValue(false) != currentBlue,
                        isAlphaDifferent = alphaText.isDragging() && alphaText.getSliderValue(false) != currentAlpha;

                // Determine if any Values DO need updates
                if (isRedDifferent || isGreenDifferent || isBlueDifferent || isAlphaDifferent) {
                    currentRed = (int) redText.getSliderValue(false) & 255;
                    currentGreen = (int) greenText.getSliderValue(false) & 255;
                    currentBlue = (int) blueText.getSliderValue(false) & 255;
                    currentAlpha = (int) alphaText.getSliderValue(false) & 255;

                    localColor = new Color(currentRed, currentGreen, currentBlue, currentAlpha);

                    currentNormalHexValue = StringUtils.getHexFrom(localColor);
                    hexText.setControlMessage(currentNormalHexValue);

                    currentConvertedHexValue = Long.toString(Long.decode(currentNormalHexValue).intValue());
                }
            }
            isModified = !hexText.getControlMessage().equals(startingHexValue);
        }

        // Page 2 - Texture Syncing
        if (currentPage == startPage + 1) {
            if (!StringUtils.isNullOrEmpty(textureText.getControlMessage())) {
                textureData = CraftPresence.GUIS.getTextureData(textureText.getControlMessage());
                currentTexture = textureData.getThird();
                currentTexturePath = textureData.getSecond();
            } else {
                currentTexture = new ResourceLocation("");
            }
            isModified = !StringUtils.isNullOrEmpty(startingTexturePath) && !textureText.getControlMessage().equals(startingTexturePath);
        }
    }
}
