package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.Constants;
import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.handler.StringHandler;
import com.gitlab.cdagaming.craftpresence.handler.discord.assets.DiscordAssetHandler;
import com.gitlab.cdagaming.craftpresence.handler.gui.controls.GUIExtendedButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ConfigGUI_ServerSettings extends GuiScreen {
    private final GuiScreen parentScreen, currentScreen;
    private GUIExtendedButton proceedButton, serverMessagesButton, defaultIconButton;
    private GuiTextField defaultMOTD, defaultName, defaultMSG;

    private String defaultServerMSG;

    ConfigGUI_ServerSettings(GuiScreen parentScreen) {
        mc = CraftPresence.instance;
        currentScreen = this;
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        defaultServerMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);

        defaultName = new GuiTextField(100, fontRenderer, (width / 2) + 3, CraftPresence.GUIS.getButtonY(1), 180, 20);
        defaultName.setText(CraftPresence.CONFIG.defaultServerName);
        defaultMOTD = new GuiTextField(110, fontRenderer, (width / 2) + 3, CraftPresence.GUIS.getButtonY(2), 180, 20);
        defaultMOTD.setText(CraftPresence.CONFIG.defaultServerMOTD);
        defaultMSG = new GuiTextField(120, fontRenderer, (width / 2) + 3, CraftPresence.GUIS.getButtonY(3), 180, 20);
        defaultMSG.setText(defaultServerMSG);

        serverMessagesButton = new GUIExtendedButton(130, (width / 2) - 90, CraftPresence.GUIS.getButtonY(4), 180, 20, Constants.TRANSLATOR.translate("gui.config.name.servermessages.servermessages"));
        defaultIconButton = new GUIExtendedButton(140, (width / 2) - 90, CraftPresence.GUIS.getButtonY(5), 180, 20, Constants.TRANSLATOR.translate("gui.config.name.servermessages.servericon"));
        proceedButton = new GUIExtendedButton(900, (width / 2) - 90, (height - 30), 180, 20, Constants.TRANSLATOR.translate("gui.config.buttonMessage.back"));

        buttonList.add(serverMessagesButton);
        buttonList.add(defaultIconButton);
        buttonList.add(proceedButton);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title");
        final String subTitle = Constants.TRANSLATOR.translate("gui.config.title.servermessages");
        final String serverNameText = Constants.TRANSLATOR.translate("gui.config.name.servermessages.servername");
        final String serverMOTDText = Constants.TRANSLATOR.translate("gui.config.name.servermessages.servermotd");
        final String defaultMessageText = Constants.TRANSLATOR.translate("gui.config.defaultMessage.server");

        drawString(fontRenderer, mainTitle, (width / 2) - (fontRenderer.getStringWidth(mainTitle) / 2), 10, 0xFFFFFF);
        drawString(fontRenderer, subTitle, (width / 2) - (fontRenderer.getStringWidth(subTitle) / 2), 20, 0xFFFFFF);
        drawString(fontRenderer, serverNameText, (width / 2) - 130, CraftPresence.GUIS.getButtonY(1) + 5, 0xFFFFFF);
        drawString(fontRenderer, serverMOTDText, (width / 2) - 130, CraftPresence.GUIS.getButtonY(2) + 5, 0xFFFFFF);
        drawString(fontRenderer, defaultMessageText, (width / 2) - 130, CraftPresence.GUIS.getButtonY(3) + 5, 0xFFFFFF);

        defaultName.drawTextBox();
        defaultMOTD.drawTextBox();
        defaultMSG.drawTextBox();

        proceedButton.enabled = !StringHandler.isNullOrEmpty(defaultMSG.getText()) || !StringHandler.isNullOrEmpty(defaultName.getText()) || !StringHandler.isNullOrEmpty(defaultMOTD.getText());
        serverMessagesButton.enabled = CraftPresence.CONFIG.showGameState;

        super.drawScreen(mouseX, mouseY, partialTicks);

        // Hovering over Default Server Name Label
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (width / 2f) - 130, CraftPresence.GUIS.getButtonY(1) + 5, fontRenderer.getStringWidth(serverNameText), fontRenderer.FONT_HEIGHT)) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.servermessages.servername")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
        // Hovering over Default Server MOTD Label
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (width / 2f) - 130, CraftPresence.GUIS.getButtonY(2) + 5, fontRenderer.getStringWidth(serverMOTDText), fontRenderer.FONT_HEIGHT)) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.servermessages.servermotd")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
        // Hovering over Default Server Message Label
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (width / 2f) - 130, CraftPresence.GUIS.getButtonY(3) + 5, fontRenderer.getStringWidth(defaultMessageText), fontRenderer.FONT_HEIGHT)) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.title.servermessages")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, serverMessagesButton)) {
            if (!serverMessagesButton.enabled) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.hoverMessage.access", Constants.TRANSLATOR.translate("gui.config.name.servermessages.servermessages"))), mouseX, mouseY, width, height, -1, fontRenderer, true);
            } else {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.servermessages.servermessages")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, defaultIconButton)) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.servermessages.servericon")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, proceedButton) && !proceedButton.enabled) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.hoverMessage.defaultempty")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == proceedButton.id) {
            if (!defaultName.getText().equals(CraftPresence.CONFIG.defaultServerName)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.defaultServerName = defaultName.getText();
            }
            if (!defaultMOTD.getText().equals(CraftPresence.CONFIG.defaultServerMOTD)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.defaultServerMOTD = defaultMOTD.getText();
            }
            if (!defaultMSG.getText().equals(defaultServerMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                StringHandler.setConfigPart(CraftPresence.CONFIG.serverMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG.getText());
            }
            mc.displayGuiScreen(parentScreen);
        } else if (button.id == serverMessagesButton.id) {
            mc.displayGuiScreen(new ConfigGUI_Selector(currentScreen, CraftPresence.CONFIG.NAME_serverMessages, Constants.TRANSLATOR.translate("gui.config.title.selector.server"), CraftPresence.SERVER.knownAddresses, null, null));
        } else if (button.id == defaultIconButton.id) {
            mc.displayGuiScreen(new ConfigGUI_Selector(currentScreen, CraftPresence.CONFIG.NAME_defaultServerIcon, Constants.TRANSLATOR.translate("gui.config.title.selector.icon"), DiscordAssetHandler.ICON_LIST, CraftPresence.CONFIG.defaultServerIcon, null));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parentScreen);
        }
        defaultName.textboxKeyTyped(typedChar, keyCode);
        defaultMOTD.textboxKeyTyped(typedChar, keyCode);
        defaultMSG.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        defaultName.mouseClicked(mouseX, mouseY, mouseButton);
        defaultMOTD.mouseClicked(mouseX, mouseY, mouseButton);
        defaultMSG.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        defaultName.updateCursorCounter();
        defaultMOTD.updateCursorCounter();
        defaultMSG.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
