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

package com.gitlab.cdagaming.craftpresence.utils.gui.controls;

import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.ModUtils;
import com.gitlab.cdagaming.craftpresence.impl.Tuple;
import com.gitlab.cdagaming.craftpresence.utils.CommandUtils;
import com.gitlab.cdagaming.craftpresence.utils.StringUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.GuiUtils;
import com.gitlab.cdagaming.craftpresence.utils.gui.integrations.ExtendedScreen;
import com.gitlab.cdagaming.craftpresence.utils.gui.widgets.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Extended Gui Widget for a Clickable Button
 *
 * @author CDAGaming
 */
@SuppressWarnings("DuplicatedCode")
public class ExtendedButtonControl extends GuiButton implements Widget {
    /**
     * Optional Arguments used for functions within the Mod, if any
     */
    private String[] optionalArgs;
    /**
     * Event to Deploy when this Control is Clicked, if any
     */
    private Runnable onPushEvent = null;
    /**
     * Event to Deploy when this Control is Hovered Over, if any
     */
    private Runnable onHoverEvent = null;
    /**
     * The current running Font Render Instance for this control
     */
    private FontRenderer currentFontRender = null;
    /**
     * Whether the mouse is currently within screen bounds
     */
    private boolean isOverScreen = false;

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param buttonId     The ID for the control to Identify as
     * @param x            The Starting X Position for this Control
     * @param y            The Starting Y Position for this Control
     * @param widthIn      The Width for this Control
     * @param heightIn     The Height for this Control
     * @param buttonText   The display text, to display within this control
     * @param optionalArgs The optional Arguments, if any, to associate with this control
     */
    public ExtendedButtonControl(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, String... optionalArgs) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);

        this.optionalArgs = optionalArgs;
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param buttonId     The ID for the control to Identify as
     * @param x            The Starting X Position for this Control
     * @param y            The Starting Y Position for this Control
     * @param widthIn      The Width for this Control
     * @param heightIn     The Height for this Control
     * @param buttonText   The display text, to display within this control
     * @param onPushEvent  The Click Event to Occur when this control is clicked
     * @param optionalArgs The optional Arguments, if any, to associate with this control
     */
    public ExtendedButtonControl(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Runnable onPushEvent, String... optionalArgs) {
        this(buttonId, x, y, widthIn, heightIn, buttonText, optionalArgs);
        this.onPushEvent = onPushEvent;
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param buttonId     The ID for the control to Identify as
     * @param x            The Starting X Position for this Control
     * @param y            The Starting Y Position for this Control
     * @param widthIn      The Width for this Control
     * @param heightIn     The Height for this Control
     * @param buttonText   The display text, to display within this control
     * @param onPushEvent  The Click Event to Occur when this control is clicked
     * @param onHoverEvent The Hover Event to Occur when this control is clicked
     * @param optionalArgs The optional Arguments, if any, to associate with this control
     */
    public ExtendedButtonControl(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, Runnable onPushEvent, Runnable onHoverEvent, String... optionalArgs) {
        this(buttonId, x, y, widthIn, heightIn, buttonText, onPushEvent, optionalArgs);
        this.onHoverEvent = onHoverEvent;
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param x            The Starting X Position for this Control
     * @param y            The Starting Y Position for this Control
     * @param widthIn      The Width for this Control
     * @param heightIn     The Height for this Control
     * @param buttonText   The display text, to display within this control
     * @param optionalArgs The optional Arguments, if any, to associate with this control
     */
    public ExtendedButtonControl(int x, int y, int widthIn, int heightIn, String buttonText, String... optionalArgs) {
        super(CraftPresence.GUIS.getNextIndex(), x, y, widthIn, heightIn, buttonText);
        this.optionalArgs = optionalArgs;
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param x            The Starting X Position for this Control
     * @param y            The Starting Y Position for this Control
     * @param widthIn      The Width for this Control
     * @param heightIn     The Height for this Control
     * @param buttonText   The display text, to display within this control
     * @param onPushEvent  The Click Event to Occur when this control is clicked
     * @param optionalArgs The optional Arguments, if any, to associate with this control
     */
    public ExtendedButtonControl(int x, int y, int widthIn, int heightIn, String buttonText, Runnable onPushEvent, String... optionalArgs) {
        this(x, y, widthIn, heightIn, buttonText, optionalArgs);
        setOnClick(onPushEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param x            The Starting X Position for this Control
     * @param y            The Starting Y Position for this Control
     * @param widthIn      The Width for this Control
     * @param heightIn     The Height for this Control
     * @param buttonText   The display text, to display within this control
     * @param onPushEvent  The Click Event to Occur when this control is clicked
     * @param onHoverEvent The Hover Event to Occur when this control is clicked
     * @param optionalArgs The optional Arguments, if any, to associate with this control
     */
    public ExtendedButtonControl(int x, int y, int widthIn, int heightIn, String buttonText, Runnable onPushEvent, Runnable onHoverEvent, String... optionalArgs) {
        this(x, y, widthIn, heightIn, buttonText, onPushEvent, optionalArgs);
        setOnHover(onHoverEvent);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param id            The ID for the Control to Identify as
     * @param xPos          The Starting X Position for this Control
     * @param yPos          The Starting Y Position for this Control
     * @param displayString The display text, to display within this Control
     */
    public ExtendedButtonControl(int id, int xPos, int yPos, String displayString) {
        super(id, xPos, yPos, displayString);
    }

    /**
     * Initialization Event for this Control, assigning defined arguments
     *
     * @param xPos          The Starting X Position for this Control
     * @param yPos          The Starting Y Position for this Control
     * @param displayString The display text, to display within this Control
     */
    public ExtendedButtonControl(int xPos, int yPos, String displayString) {
        this(CraftPresence.GUIS.getNextIndex(), xPos, yPos, displayString);
    }

    @Override
    public void draw(ExtendedScreen screen) {
        isOverScreen = CraftPresence.GUIS.isMouseOver(screen);
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        setCurrentFontRender(mc.fontRenderer);
        if (visible) {
            hovered = isOverScreen() && CraftPresence.GUIS.isMouseOver(mouseX, mouseY, this);
            final int hoverState = getHoverState(hovered);

            String backgroundCode = CraftPresence.CONFIG.accessibilitySettings.buttonBackgroundColor;

            if (StringUtils.isValidColorCode(backgroundCode)) {
                CraftPresence.GUIS.drawGradientRect(zLevel, getX(), getY(), getWidth(), getHeight(), backgroundCode, backgroundCode);
            } else {
                final Tuple<Boolean, String, ResourceLocation> textureData = CraftPresence.GUIS.getTextureData(backgroundCode);
                final ResourceLocation texLocation = textureData.getThird();

                CraftPresence.GUIS.renderButton(getX(), getY(), getWidth(), getHeight(), hoverState, zLevel, texLocation);
            }

            if (isOverScreen()) {
                mouseDragged(mc, mouseX, mouseY);
            }
            final int color;

            if (!enabled) {
                color = 10526880;
            } else if (hovered) {
                color = 16777120;
            } else {
                color = 14737632;
            }

            drawCenteredString(getFontRenderer(), getDisplayMessage(), getX() + getWidth() / 2, getY() + (getHeight() - 8) / 2, color);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public void setX(int posX) {
        this.x = posX;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public void setY(int posY) {
        this.y = posY;
    }

    /**
     * Get whether the mouse is currently within screen bounds
     * @return {@link Boolean#TRUE} is condition is satisfied
     */
    public boolean isOverScreen() {
        return isOverScreen;
    }

    /**
     * Get the Current Font Renderer for this Control
     *
     * @return The Current Font Renderer for this Control
     */
    public FontRenderer getFontRenderer() {
        return currentFontRender != null ? currentFontRender : GuiUtils.getDefaultFontRenderer();
    }

    /**
     * Set the Current Font Renderer for this Control
     *
     * @param currentFontRender The new current font renderer
     */
    public void setCurrentFontRender(final FontRenderer currentFontRender) {
        this.currentFontRender = currentFontRender;
    }

    /**
     * Get the Current Font Height for this Control
     *
     * @return The Current Font Height for this Control
     */
    public int getFontHeight() {
        return getFontRenderer().FONT_HEIGHT;
    }

    /**
     * Retrieves, if any, the Optional Arguments assigned within this Control
     *
     * @return The Optional Arguments assigned within this Control, if any
     */
    public String[] getOptionalArgs() {
        return optionalArgs.clone();
    }

    /**
     * Set the Event to occur upon Mouse Click
     *
     * @param event The event to occur
     */
    public void setOnClick(Runnable event) {
        onPushEvent = event;
    }

    /**
     * Triggers the onClick event to occur
     */
    public void onClick() {
        if (onPushEvent != null) {
            setControlEnabled(false);
            CommandUtils.getThreadPool().execute(onPushEvent);
            setControlEnabled(true);
        }
    }

    /**
     * Sets the Event to occur upon Mouse Over
     *
     * @param event The event to occur
     */
    public void setOnHover(Runnable event) {
        onHoverEvent = event;
    }

    /**
     * Triggers the onHover event to occur
     */
    public void onHover() {
        if (onHoverEvent != null) {
            onHoverEvent.run();
        }
    }

    /**
     * Gets the control's current display message
     *
     * @return The control's current display message
     */
    public String getDisplayMessage() {
        String result = getControlMessage().trim();
        if (result.contains(" ")) {
            String adjusted = result;
            for (String dataPart : result.split(" ")) {
                if (ModUtils.TRANSLATOR.hasTranslation(dataPart)) {
                    adjusted = adjusted.replace(dataPart, ModUtils.TRANSLATOR.translate(dataPart));
                }
            }
            result = adjusted;
        } else if (ModUtils.TRANSLATOR.hasTranslation(getControlMessage())) {
            result = ModUtils.TRANSLATOR.translate(result);
        }
        return result;
    }

    /**
     * Gets the control's current text contents
     *
     * @return The control's current text contents
     */
    public String getControlMessage() {
        return this.displayString;
    }

    /**
     * Sets the control's display message to the specified value
     *
     * @param newMessage The new display message for this control
     */
    public void setControlMessage(final String newMessage) {
        this.displayString = newMessage;
    }

    /**
     * Gets whether the control is currently active or enabled
     *
     * @return Whether the control is currently active or enabled
     */
    public boolean isControlEnabled() {
        return this.enabled;
    }

    /**
     * Sets the control's current enabled state
     *
     * @param isEnabled The new enable state for this control
     */
    public void setControlEnabled(final boolean isEnabled) {
        this.enabled = isEnabled;
    }

    /**
     * Gets whether the control is currently visible
     *
     * @return Whether the control is currently visible
     */
    public boolean isControlVisible() {
        return this.visible;
    }

    /**
     * Sets the control's current visibility state
     *
     * @param isVisible The new visibility state for this control
     */
    public void setControlVisible(final boolean isVisible) {
        this.visible = isVisible;
    }
}
