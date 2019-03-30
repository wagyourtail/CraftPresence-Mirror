package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.Constants;
import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.handler.StringHandler;
import com.gitlab.cdagaming.craftpresence.handler.gui.controls.GUIExtendedButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ConfigGUI_StatusMessages extends GuiScreen {
    private final GuiScreen parentScreen;
    private int pageNumber;
    private GUIExtendedButton proceedButton, nextPageButton, previousPageButton;
    private GuiTextField mainMenuMSG, lanMSG, singleplayerMSG, packMSG,
            playerMSG, playerAmountMSG, gameTimeMSG, modsMSG, viveCraftMSG;

    ConfigGUI_StatusMessages(GuiScreen parentScreen) {
        mc = CraftPresence.instance;
        pageNumber = 0;
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        ScaledResolution sr = new ScaledResolution(mc);

        // Page 1 Items
        mainMenuMSG = new GuiTextField(110, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1), 180, 20);
        lanMSG = new GuiTextField(120, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(2), 180, 20);
        singleplayerMSG = new GuiTextField(130, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(3), 180, 20);

        // Page 2 Items
        packMSG = new GuiTextField(150, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1), 180, 20);
        playerMSG = new GuiTextField(160, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(2), 180, 20);
        playerAmountMSG = new GuiTextField(170, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(3), 180, 20);
        gameTimeMSG = new GuiTextField(180, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(4), 180, 20);
        modsMSG = new GuiTextField(190, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(5), 180, 20);
        viveCraftMSG = new GuiTextField(200, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(6), 180, 20);

        mainMenuMSG.setText(CraftPresence.CONFIG.mainmenuMSG);
        lanMSG.setText(CraftPresence.CONFIG.lanMSG);
        singleplayerMSG.setText(CraftPresence.CONFIG.singleplayerMSG);
        packMSG.setText(CraftPresence.CONFIG.packPlaceholderMSG);
        playerMSG.setText(CraftPresence.CONFIG.playerPlaceholderMSG);
        playerAmountMSG.setText(CraftPresence.CONFIG.playerAmountPlaceholderMSG);
        gameTimeMSG.setText(CraftPresence.CONFIG.gameTimePlaceholderMSG);
        modsMSG.setText(CraftPresence.CONFIG.modsPlaceholderMSG);
        viveCraftMSG.setText(CraftPresence.CONFIG.vivecraftMessage);

        proceedButton = new GUIExtendedButton(700, (sr.getScaledWidth() / 2) - 90, (sr.getScaledHeight() - 30), 180, 20, Constants.TRANSLATOR.translate("gui.config.buttonMessage.back"));

        previousPageButton = new GUIExtendedButton(800, proceedButton.x - 23, (sr.getScaledHeight() - 30), 20, 20, "<");
        nextPageButton = new GUIExtendedButton(900, (proceedButton.x + proceedButton.getWidth()) + 3, (sr.getScaledHeight() - 30), 20, 20, ">");

        buttonList.add(previousPageButton);
        buttonList.add(nextPageButton);
        buttonList.add(proceedButton);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        drawDefaultBackground();

        final String mainTitle = Constants.TRANSLATOR.translate("gui.config.title");
        final String subTitle = Constants.TRANSLATOR.translate("gui.config.title.statusmessages");
        final String mainMenuText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.mainmenumsg");
        final String lanText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.lanmsg");
        final String singlePlayerText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.singleplayermsg");
        final String packText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.placeholder.packmsg");
        final String playerText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.placeholder.playermsg");
        final String playerAmountText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.placeholder.playeramountmsg");
        final String gameTimeText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.placeholder.gametimemsg");
        final String modsText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.placeholder.modsmsg");
        final String viveCraftText = Constants.TRANSLATOR.translate("gui.config.name.statusmessages.special.vivecraftmsg");

        drawString(fontRenderer, mainTitle, (sr.getScaledWidth() / 2) - (fontRenderer.getStringWidth(mainTitle) / 2), 10, 0xFFFFFF);
        drawString(fontRenderer, subTitle, (sr.getScaledWidth() / 2) - (fontRenderer.getStringWidth(subTitle) / 2), 20, 0xFFFFFF);

        if (pageNumber == 0) {
            drawString(fontRenderer, mainMenuText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(1) + 5, 0xFFFFFF);
            drawString(fontRenderer, lanText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(2) + 5, 0xFFFFFF);
            drawString(fontRenderer, singlePlayerText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(3) + 5, 0xFFFFFF);

            mainMenuMSG.drawTextBox();
            lanMSG.drawTextBox();
            singleplayerMSG.drawTextBox();
        }

        if (pageNumber == 1) {
            drawString(fontRenderer, packText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(1) + 5, 0xFFFFFF);
            drawString(fontRenderer, playerText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(2) + 5, 0xFFFFFF);
            drawString(fontRenderer, playerAmountText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(3) + 5, 0xFFFFFF);
            drawString(fontRenderer, gameTimeText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(4) + 5, 0xFFFFFF);
            drawString(fontRenderer, modsText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(5) + 5, 0xFFFFFF);
            drawString(fontRenderer, viveCraftText, (sr.getScaledWidth() / 2) - 145, CraftPresence.GUIS.getButtonY(6) + 5, 0xFFFFFF);

            packMSG.drawTextBox();
            playerMSG.drawTextBox();
            playerAmountMSG.drawTextBox();
            gameTimeMSG.drawTextBox();
            modsMSG.drawTextBox();
            viveCraftMSG.drawTextBox();
        }

        previousPageButton.enabled = pageNumber != 0;
        nextPageButton.enabled = pageNumber != 1;
        proceedButton.enabled = !StringHandler.isNullOrEmpty(mainMenuMSG.getText()) && !StringHandler.isNullOrEmpty(singleplayerMSG.getText()) && !StringHandler.isNullOrEmpty(packMSG.getText()) && !StringHandler.isNullOrEmpty(playerMSG.getText()) && !StringHandler.isNullOrEmpty(playerAmountMSG.getText()) && !StringHandler.isNullOrEmpty(gameTimeMSG.getText()) && !StringHandler.isNullOrEmpty(viveCraftMSG.getText());

        super.drawScreen(mouseX, mouseY, partialTicks);

        if (pageNumber == 0) {
            // Hovering over Main Menu Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(1) + 5, fontRenderer.getStringWidth(mainMenuText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.mainmenumsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
            //Hovering over LAN Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(2) + 5, fontRenderer.getStringWidth(lanText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.lanmsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
            // Hovering over Single Player Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(3) + 5, fontRenderer.getStringWidth(singlePlayerText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.singleplayermsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
        }

        if (pageNumber == 1) {
            // Hovering over Pack Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(1) + 5, fontRenderer.getStringWidth(packText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.placeholder.packmsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
            // Hovering over Player Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(2) + 5, fontRenderer.getStringWidth(playerText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.placeholder.playermsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
            // Hovering over Player Amount Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(3) + 5, fontRenderer.getStringWidth(playerAmountText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.placeholder.playeramountmsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
            // Hovering over Game Time Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(4) + 5, fontRenderer.getStringWidth(gameTimeText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.placeholder.gametimemsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
            // Hovering over Mods Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(5) + 5, fontRenderer.getStringWidth(modsText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.placeholder.modsmsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
            // Hovering over Vivecraft Message Label
            if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 145, CraftPresence.GUIS.getButtonY(6) + 5, fontRenderer.getStringWidth(viveCraftText), fontRenderer.FONT_HEIGHT)) {
                CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.comment.statusmessages.special.vivecraftmsg")), mouseX, mouseY, width, height, -1, fontRenderer, true);
            }
        }

        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, proceedButton) && !proceedButton.enabled) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.hoverMessage.defaultempty")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == previousPageButton.id && pageNumber != 0) {
            pageNumber--;
        }
        if (button.id == nextPageButton.id && pageNumber != 1) {
            pageNumber++;
        }

        if (button.id == proceedButton.id) {
            if (!mainMenuMSG.getText().equals(CraftPresence.CONFIG.mainmenuMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.mainmenuMSG = mainMenuMSG.getText();
            }
            if (!lanMSG.getText().equals(CraftPresence.CONFIG.lanMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.lanMSG = lanMSG.getText();
            }
            if (!singleplayerMSG.getText().equals(CraftPresence.CONFIG.singleplayerMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.singleplayerMSG = singleplayerMSG.getText();
            }
            if (!packMSG.getText().equals(CraftPresence.CONFIG.packPlaceholderMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.packPlaceholderMSG = packMSG.getText();
            }
            if (!playerMSG.getText().equals(CraftPresence.CONFIG.playerPlaceholderMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.playerPlaceholderMSG = playerMSG.getText();
            }
            if (!playerAmountMSG.getText().equals(CraftPresence.CONFIG.playerAmountPlaceholderMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.playerAmountPlaceholderMSG = playerAmountMSG.getText();
            }
            if (!gameTimeMSG.getText().equals(CraftPresence.CONFIG.gameTimePlaceholderMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.gameTimePlaceholderMSG = gameTimeMSG.getText();
            }
            if (!modsMSG.getText().equals(CraftPresence.CONFIG.modsPlaceholderMSG)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.modsPlaceholderMSG = modsMSG.getText();
            }
            if (!viveCraftMSG.getText().equals(CraftPresence.CONFIG.vivecraftMessage)) {
                CraftPresence.CONFIG.hasChanged = true;
                CraftPresence.CONFIG.hasClientPropertiesChanged = true;
                CraftPresence.CONFIG.vivecraftMessage = viveCraftMSG.getText();
            }
            mc.displayGuiScreen(parentScreen);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parentScreen);
        }

        if (keyCode == Keyboard.KEY_LEFT && pageNumber != 0) {
            pageNumber--;
        }

        if (keyCode == Keyboard.KEY_RIGHT && pageNumber != 1) {
            pageNumber++;
        }

        if (pageNumber == 0) {
            mainMenuMSG.textboxKeyTyped(typedChar, keyCode);
            lanMSG.textboxKeyTyped(typedChar, keyCode);
            singleplayerMSG.textboxKeyTyped(typedChar, keyCode);
        }

        if (pageNumber == 1) {
            packMSG.textboxKeyTyped(typedChar, keyCode);
            playerMSG.textboxKeyTyped(typedChar, keyCode);
            playerAmountMSG.textboxKeyTyped(typedChar, keyCode);
            gameTimeMSG.textboxKeyTyped(typedChar, keyCode);
            modsMSG.textboxKeyTyped(typedChar, keyCode);
            viveCraftMSG.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (pageNumber == 0) {
            mainMenuMSG.mouseClicked(mouseX, mouseY, mouseButton);
            lanMSG.mouseClicked(mouseX, mouseY, mouseButton);
            singleplayerMSG.mouseClicked(mouseX, mouseY, mouseButton);
        }

        if (pageNumber == 1) {
            packMSG.mouseClicked(mouseX, mouseY, mouseButton);
            playerMSG.mouseClicked(mouseX, mouseY, mouseButton);
            playerAmountMSG.mouseClicked(mouseX, mouseY, mouseButton);
            gameTimeMSG.mouseClicked(mouseX, mouseY, mouseButton);
            modsMSG.mouseClicked(mouseX, mouseY, mouseButton);
            viveCraftMSG.mouseClicked(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        if (pageNumber == 0) {
            mainMenuMSG.updateCursorCounter();
            lanMSG.updateCursorCounter();
            singleplayerMSG.updateCursorCounter();
        }

        if (pageNumber == 1) {
            packMSG.updateCursorCounter();
            playerMSG.updateCursorCounter();
            playerAmountMSG.updateCursorCounter();
            gameTimeMSG.updateCursorCounter();
            modsMSG.updateCursorCounter();
            viveCraftMSG.updateCursorCounter();
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
