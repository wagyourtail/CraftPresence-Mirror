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

package com.gitlab.cdagaming.craftpresence.config.category;

import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.core.Constants;
import com.gitlab.cdagaming.craftpresence.core.config.Module;
import com.gitlab.cdagaming.craftpresence.core.config.element.ColorData;
import com.gitlab.cdagaming.craftpresence.core.config.element.ColorSection;
import com.gitlab.cdagaming.craftpresence.utils.gui.RenderUtils;

import java.io.Serializable;
import java.util.Objects;

public class Accessibility extends Module implements Serializable {
    private static final long serialVersionUID = -6804925684173174749L;
    private static final Accessibility DEFAULT = new Accessibility();
    public ColorData tooltipBackground = RenderUtils.DEFAULT_TOOLTIP_BACKGROUND;
    public ColorData tooltipBorder = RenderUtils.DEFAULT_TOOLTIP_BORDER;
    public ColorData guiBackground = new ColorData(
            new ColorSection(64, 64, 64, 255),
            RenderUtils.DEFAULT_GUI_BACKGROUND
    );
    public ColorData altGuiBackground = new ColorData(
            new ColorSection(16, 16, 16, 192),
            new ColorSection(16, 16, 16, 208)
    );
    public String languageId = Constants.TRANSLATOR.getDefaultLanguage();
    public boolean stripTranslationColors = false;
    public boolean stripTranslationFormatting = ModUtils.IS_TEXT_FORMATTING_BLOCKED;
    public boolean stripExtraGuiElements = false;
    public boolean renderTooltips = true;
    public int configKeyCode = ModUtils.MCProtocolID > 340 ? 96 : 41;

    public Accessibility(final Accessibility other) {
        transferFrom(other);
    }

    public Accessibility() {
        // N/A
    }

    @Override
    public Accessibility getDefaults() {
        return new Accessibility(DEFAULT);
    }

    @Override
    public Accessibility copy() {
        return new Accessibility(this);
    }

    @Override
    public void transferFrom(Module target) {
        if (target instanceof Accessibility && !equals(target)) {
            final Accessibility data = (Accessibility) target;

            tooltipBackground = new ColorData(data.tooltipBackground);
            tooltipBorder = new ColorData(data.tooltipBorder);
            guiBackground = new ColorData(data.guiBackground);
            altGuiBackground = new ColorData(data.altGuiBackground);
            languageId = data.languageId;
            stripTranslationColors = data.stripTranslationColors;
            stripTranslationFormatting = data.stripTranslationFormatting;
            stripExtraGuiElements = data.stripExtraGuiElements;
            renderTooltips = data.renderTooltips;
            configKeyCode = data.configKeyCode;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Accessibility)) {
            return false;
        }

        final Accessibility other = (Accessibility) obj;

        return Objects.equals(other.tooltipBackground, tooltipBackground) &&
                Objects.equals(other.tooltipBorder, tooltipBorder) &&
                Objects.equals(other.guiBackground, guiBackground) &&
                Objects.equals(other.altGuiBackground, altGuiBackground) &&
                Objects.equals(other.languageId, languageId) &&
                Objects.equals(other.stripTranslationColors, stripTranslationColors) &&
                Objects.equals(other.stripTranslationFormatting, stripTranslationFormatting) &&
                Objects.equals(other.stripExtraGuiElements, stripExtraGuiElements) &&
                Objects.equals(other.renderTooltips, renderTooltips) &&
                Objects.equals(other.configKeyCode, configKeyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                tooltipBackground, tooltipBorder,
                guiBackground, altGuiBackground,
                languageId,
                stripTranslationColors, stripTranslationFormatting,
                stripExtraGuiElements, renderTooltips,
                configKeyCode
        );
    }
}
