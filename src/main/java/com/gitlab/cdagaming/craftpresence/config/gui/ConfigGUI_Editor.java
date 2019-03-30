package com.gitlab.cdagaming.craftpresence.config.gui;

import com.gitlab.cdagaming.craftpresence.Constants;
import com.gitlab.cdagaming.craftpresence.CraftPresence;
import com.gitlab.cdagaming.craftpresence.handler.StringHandler;
import com.gitlab.cdagaming.craftpresence.handler.discord.assets.DiscordAssetHandler;
import com.gitlab.cdagaming.craftpresence.handler.gui.controls.GUIExtendedButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class ConfigGUI_Editor extends GuiScreen {
    private final GuiScreen parentScreen, currentScreen;
    private GUIExtendedButton proceedButton, specificIconButton;
    private GuiTextField specificMessage, newValueName;
    private String attributeName, configOption, specificMSG, defaultMSG, mainTitle, removeMSG;
    private boolean isNewValue, isDefaultValue;

    ConfigGUI_Editor(GuiScreen parentScreen, String attributeName, String configOption) {
        mc = CraftPresence.instance;
        currentScreen = this;
        this.parentScreen = parentScreen;
        this.configOption = configOption;
        this.attributeName = attributeName;
        isNewValue = StringHandler.isNullOrEmpty(attributeName);
        isDefaultValue = !StringHandler.isNullOrEmpty(attributeName) && "default".equals(attributeName);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        ScaledResolution sr = new ScaledResolution(mc);

        if (isNewValue) {
            mainTitle = Constants.TRANSLATOR.translate("gui.config.title.editor.addnew");
            if (parentScreen instanceof ConfigGUI_BiomeSettings) {
                specificMSG = defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.biomeMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
            } else if (parentScreen instanceof ConfigGUI_DimensionSettings) {
                specificMSG = defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.dimensionMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
            } else if (parentScreen instanceof ConfigGUI_ServerSettings) {
                specificMSG = defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
            } else if (parentScreen instanceof ConfigGUI_AdvancedSettings) {
                if (configOption.equals(CraftPresence.CONFIG.NAME_guiMessages)) {
                    specificMSG = defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.guiMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                } else if (configOption.equals(CraftPresence.CONFIG.NAME_itemMessages)) {
                    specificMSG = defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.itemMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                }
            }
        } else {
            if (parentScreen instanceof ConfigGUI_BiomeSettings) {
                mainTitle = Constants.TRANSLATOR.translate("gui.config.title.biome.editspecificbiome", attributeName);
                defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.biomeMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                specificMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.biomeMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG);
            } else if (parentScreen instanceof ConfigGUI_DimensionSettings) {
                mainTitle = Constants.TRANSLATOR.translate("gui.config.title.dimension.editspecificdimension", attributeName);
                defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.dimensionMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                specificMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.dimensionMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG);
            } else if (parentScreen instanceof ConfigGUI_ServerSettings) {
                mainTitle = Constants.TRANSLATOR.translate("gui.config.title.server.editspecificserver", attributeName);
                defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                specificMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG);
            } else if (parentScreen instanceof ConfigGUI_AdvancedSettings) {
                if (configOption.equals(CraftPresence.CONFIG.NAME_guiMessages)) {
                    mainTitle = Constants.TRANSLATOR.translate("gui.config.title.gui.editspecificgui", attributeName);
                    defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.guiMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                    specificMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.guiMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG);
                } else if (configOption.equals(CraftPresence.CONFIG.NAME_itemMessages)) {
                    mainTitle = Constants.TRANSLATOR.translate("gui.config.title.gui.editspecificitem", attributeName);
                    defaultMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.itemMessages, "default", 0, 1, CraftPresence.CONFIG.splitCharacter, null);
                    specificMSG = StringHandler.getConfigPart(CraftPresence.CONFIG.itemMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, defaultMSG);
                }
            }
        }

        removeMSG = Constants.TRANSLATOR.translate("gui.config.message.remove");

        specificMessage = new GuiTextField(110, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(1), 180, 20);
        specificMessage.setText(specificMSG);

        if ((parentScreen instanceof ConfigGUI_DimensionSettings || parentScreen instanceof ConfigGUI_ServerSettings) && !isNewValue) {
            specificIconButton = new GUIExtendedButton(100, (sr.getScaledWidth() / 2) - 90, CraftPresence.GUIS.getButtonY(2), 180, 20, Constants.TRANSLATOR.translate("gui.config.buttonMessage.iconchange"));
            buttonList.add(specificIconButton);
        }
        if (isNewValue) {
            newValueName = new GuiTextField(120, fontRenderer, (sr.getScaledWidth() / 2) + 3, CraftPresence.GUIS.getButtonY(3), 180, 20);
        }

        proceedButton = new GUIExtendedButton(900, (sr.getScaledWidth() / 2) - 90, (sr.getScaledHeight() - 30), 180, 20, Constants.TRANSLATOR.translate("gui.config.buttonMessage.back"));

        buttonList.add(proceedButton);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        drawDefaultBackground();

        final String messageText = Constants.TRANSLATOR.translate("gui.config.editorMessage.message");
        final String valueNameText = Constants.TRANSLATOR.translate("gui.config.editorMessage.valuename");

        drawString(fontRenderer, mainTitle, (sr.getScaledWidth() / 2) - (fontRenderer.getStringWidth(mainTitle) / 2), 15, 0xFFFFFF);
        drawString(fontRenderer, messageText, (sr.getScaledWidth() / 2) - 130, CraftPresence.GUIS.getButtonY(1) + 5, 0xFFFFFF);
        if (isNewValue) {
            drawString(fontRenderer, valueNameText, (sr.getScaledWidth() / 2) - 130, CraftPresence.GUIS.getButtonY(3) + 5, 0xFFFFFF);
            newValueName.drawTextBox();
        } else {
            drawString(fontRenderer, removeMSG, (sr.getScaledWidth() / 2) - (fontRenderer.getStringWidth(removeMSG) / 2), (sr.getScaledHeight() - 45), 0xFFFFFF);
        }
        specificMessage.drawTextBox();

        proceedButton.displayString = !specificMessage.getText().equals(specificMSG) || (isNewValue && !StringHandler.isNullOrEmpty(newValueName.getText()) && !specificMessage.getText().equals(defaultMSG)) || (isDefaultValue && !StringHandler.isNullOrEmpty(specificMessage.getText()) && !specificMessage.getText().equals(specificMSG)) ? Constants.TRANSLATOR.translate("gui.config.buttonMessage.continue") : Constants.TRANSLATOR.translate("gui.config.buttonMessage.back");

        proceedButton.enabled = !(StringHandler.isNullOrEmpty(specificMessage.getText()) && isDefaultValue);

        super.drawScreen(mouseX, mouseY, partialTicks);

        // Hovering over Message Label
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(1) + 5, fontRenderer.getStringWidth(messageText), fontRenderer.FONT_HEIGHT)) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.message.remove")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
        // Hovering over Value Name Label
        if (isNewValue && CraftPresence.GUIS.isMouseOver(mouseX, mouseY, (sr.getScaledWidth() / 2f) - 130, CraftPresence.GUIS.getButtonY(3) + 5, fontRenderer.getStringWidth(valueNameText), fontRenderer.FONT_HEIGHT)) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.hoverMessage.valuename")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
        if (CraftPresence.GUIS.isMouseOver(mouseX, mouseY, proceedButton) && !proceedButton.enabled) {
            CraftPresence.GUIS.drawMultiLineString(StringHandler.splitTextByNewLine(Constants.TRANSLATOR.translate("gui.config.hoverMessage.defaultempty")), mouseX, mouseY, width, height, -1, fontRenderer, true);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == proceedButton.id) {
            if (!specificMessage.getText().equals(specificMSG) || (isNewValue && !StringHandler.isNullOrEmpty(newValueName.getText()) && !specificMessage.getText().equals(defaultMSG)) || (isDefaultValue && !StringHandler.isNullOrEmpty(specificMessage.getText()) && !specificMessage.getText().equals(specificMSG))) {
                if (isNewValue && !StringHandler.isNullOrEmpty(newValueName.getText())) {
                    attributeName = newValueName.getText();
                }
                CraftPresence.CONFIG.hasChanged = true;
                if (parentScreen instanceof ConfigGUI_BiomeSettings) {
                    CraftPresence.CONFIG.biomeMessages = StringHandler.setConfigPart(CraftPresence.CONFIG.biomeMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, specificMessage.getText());
                } else if (parentScreen instanceof ConfigGUI_DimensionSettings) {
                    CraftPresence.CONFIG.dimensionMessages = StringHandler.setConfigPart(CraftPresence.CONFIG.dimensionMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, specificMessage.getText());
                } else if (parentScreen instanceof ConfigGUI_ServerSettings) {
                    CraftPresence.CONFIG.serverMessages = StringHandler.setConfigPart(CraftPresence.CONFIG.serverMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, specificMessage.getText());
                } else if (parentScreen instanceof ConfigGUI_AdvancedSettings) {
                    if (configOption.equals(CraftPresence.CONFIG.NAME_guiMessages)) {
                        CraftPresence.CONFIG.guiMessages = StringHandler.setConfigPart(CraftPresence.CONFIG.guiMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, specificMessage.getText());
                    } else if (configOption.equals(CraftPresence.CONFIG.NAME_itemMessages)) {
                        CraftPresence.CONFIG.itemMessages = StringHandler.setConfigPart(CraftPresence.CONFIG.itemMessages, attributeName, 0, 1, CraftPresence.CONFIG.splitCharacter, specificMessage.getText());
                    }
                }
            }
            if (StringHandler.isNullOrEmpty(specificMessage.getText()) || (specificMessage.getText().equalsIgnoreCase(defaultMSG) && !specificMSG.equals(defaultMSG) && !isDefaultValue)) {
                if (isNewValue && !StringHandler.isNullOrEmpty(newValueName.getText())) {
                    attributeName = newValueName.getText();
                }
                CraftPresence.CONFIG.hasChanged = true;
                if (parentScreen instanceof ConfigGUI_BiomeSettings) {
                    CraftPresence.CONFIG.biomeMessages = StringHandler.removeFromArray(CraftPresence.CONFIG.biomeMessages, attributeName, 0, CraftPresence.CONFIG.splitCharacter);
                    CraftPresence.BIOMES.BIOME_NAMES.remove(attributeName);
                    CraftPresence.BIOMES.getBiomes();
                } else if (parentScreen instanceof ConfigGUI_DimensionSettings) {
                    CraftPresence.CONFIG.dimensionMessages = StringHandler.removeFromArray(CraftPresence.CONFIG.dimensionMessages, attributeName, 0, CraftPresence.CONFIG.splitCharacter);
                    CraftPresence.DIMENSIONS.DIMENSION_NAMES.remove(attributeName);
                    CraftPresence.DIMENSIONS.getDimensions();
                } else if (parentScreen instanceof ConfigGUI_ServerSettings) {
                    CraftPresence.CONFIG.serverMessages = StringHandler.removeFromArray(CraftPresence.CONFIG.serverMessages, attributeName, 0, CraftPresence.CONFIG.splitCharacter);
                    CraftPresence.SERVER.knownAddresses.remove(attributeName);
                    CraftPresence.SERVER.getServerAddresses();
                } else if (parentScreen instanceof ConfigGUI_AdvancedSettings) {
                    if (configOption.equals(CraftPresence.CONFIG.NAME_guiMessages)) {
                        CraftPresence.CONFIG.guiMessages = StringHandler.removeFromArray(CraftPresence.CONFIG.guiMessages, attributeName, 0, CraftPresence.CONFIG.splitCharacter);
                        CraftPresence.GUIS.GUI_NAMES.remove(attributeName);
                        CraftPresence.GUIS.getGUIs();
                    } else if (configOption.equals(CraftPresence.CONFIG.NAME_itemMessages)) {
                        CraftPresence.CONFIG.itemMessages = StringHandler.removeFromArray(CraftPresence.CONFIG.itemMessages, attributeName, 0, CraftPresence.CONFIG.splitCharacter);
                        CraftPresence.ENTITIES.ENTITY_NAMES.remove(attributeName);
                        CraftPresence.ENTITIES.getEntities();
                    }
                }
            }
            mc.displayGuiScreen(parentScreen);
        } else if (buttonList.contains(specificIconButton) && button.id == specificIconButton.id) {
            if (parentScreen instanceof ConfigGUI_DimensionSettings) {
                final String defaultIcon = StringHandler.getConfigPart(CraftPresence.CONFIG.dimensionMessages, "default", 0, 2, CraftPresence.CONFIG.splitCharacter, CraftPresence.CONFIG.defaultDimensionIcon);
                final String specificIcon = StringHandler.getConfigPart(CraftPresence.CONFIG.dimensionMessages, attributeName, 0, 2, CraftPresence.CONFIG.splitCharacter, defaultIcon);
                mc.displayGuiScreen(new ConfigGUI_Selector(currentScreen, CraftPresence.CONFIG.NAME_dimensionMessages, "CraftPresence - Select an Icon", DiscordAssetHandler.ICON_LIST, specificIcon, attributeName));
            } else if (parentScreen instanceof ConfigGUI_ServerSettings) {
                final String defaultIcon = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, "default", 0, 2, CraftPresence.CONFIG.splitCharacter, CraftPresence.CONFIG.defaultServerIcon);
                final String specificIcon = StringHandler.getConfigPart(CraftPresence.CONFIG.serverMessages, attributeName, 0, 2, CraftPresence.CONFIG.splitCharacter, defaultIcon);
                mc.displayGuiScreen(new ConfigGUI_Selector(currentScreen, CraftPresence.CONFIG.NAME_serverMessages, "CraftPresence - Select an Icon", DiscordAssetHandler.ICON_LIST, specificIcon, attributeName));
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(parentScreen);
        }

        if (isNewValue) {
            newValueName.textboxKeyTyped(typedChar, keyCode);
        }
        specificMessage.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isNewValue) {
            newValueName.mouseClicked(mouseX, mouseY, mouseButton);
        }
        specificMessage.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        if (isNewValue) {
            newValueName.updateCursorCounter();
        }
        specificMessage.updateCursorCounter();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
}
