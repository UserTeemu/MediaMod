package me.dreamhopping.mediamod.gui;

import me.dreamhopping.mediamod.gui.core.MediaModGui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import me.dreamhopping.mediamod.config.Settings;
import me.dreamhopping.mediamod.gui.core.util.CustomButton;
import me.dreamhopping.mediamod.util.Metadata;

/**
 * The Gui for editing the MediaMod Settings
 *
 * @see MediaModGui
 */
public class GuiMediaModSettings extends MediaModGui {
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(0, (width / 2) - 100, getRowPos(0), getSuffix(Settings.ENABLED, "Enabled")));
        this.buttonList.add(new CustomButton(1, (width / 2) - 100, getRowPos(1), "Customise Player"));
        this.buttonList.add(new CustomButton(2, (width / 2) - 100, getRowPos(2), "Services"));
        this.buttonList.add(new CustomButton(3, (width / 2) - 100, height - 30, "Close"));


        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        drawString(fontRenderer, "General settings", (width / 2) - 100, 60, -1);

        this.drawString(this.fontRenderer, I18n.format("menu.guimediamod.text.version.name") + " " + Metadata.VERSION, this.width - this.fontRenderer.getStringWidth("Version " + Metadata.VERSION) - 2, this.height - 10, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                mc.displayGuiScreen(new GuiPlayerSettings());
                break;
            case 2:
                mc.displayGuiScreen(new GuiServices());
                break;
            case 3:
                mc.displayGuiScreen(null);
                break;
        }
    }

    protected String getButtonTooltip(int buttonId) {
        return null;
    }
}
