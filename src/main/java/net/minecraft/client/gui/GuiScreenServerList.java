package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;

import dev.isnow.puppy.Puppy;
import dev.isnow.puppy.helper.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenServerList extends GuiScreen
{
    private final GuiScreen field_146303_a;
    private final ServerData field_146301_f;
    private GuiTextField field_146302_g;

    public GuiScreenServerList(GuiScreen p_i1031_1_, ServerData p_i1031_2_)
    {
        this.field_146303_a = p_i1031_1_;
        this.field_146301_f = p_i1031_2_;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.field_146302_g.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectServer.select")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        this.field_146302_g = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 116, 200, 20);
        this.field_146302_g.setMaxStringLength(128);
        this.field_146302_g.setFocused(true);
        this.field_146302_g.setText(this.mc.gameSettings.lastServer);
        this.buttonList.get(0).enabled = this.field_146302_g.getText().length() > 0 && this.field_146302_g.getText().split(":").length > 0;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.field_146302_g.getText();
        this.mc.gameSettings.saveOptions();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 1)
            {
                this.field_146303_a.confirmClicked(false, 0);
            }
            else if (button.id == 0)
            {
                this.field_146301_f.serverIP = this.field_146302_g.getText();
                this.field_146303_a.confirmClicked(true, 0);
            }
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.field_146302_g.textboxKeyTyped(typedChar, keyCode))
        {
            this.buttonList.get(0).enabled = this.field_146302_g.getText().length() > 0 && this.field_146302_g.getText().split(":").length > 0;
        }
        else if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed(this.buttonList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.field_146302_g.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        Gui.drawRect(4, 4, 148, 98, new Color(30, 30, 30, 255).getRGB());
        Gui.drawRect(4, 4, 148, 5, ColorHelper.getRGB(4, 0.9f, 1, 10000));
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.8, 0.8, 0.8);
        Puppy.INSTANCE.moonFont.drawString("IP: " + String.join(" ", field_146302_g.getText().split(":")), 6, 7, new Color(0xFFFFFF).getRGB());
        Puppy.INSTANCE.moonFont.drawString("Organization: OVH Singapore PTE. LTD", 6, 22, new Color(0xFFFFFF).getRGB());
        Puppy.INSTANCE.moonFont.drawString("AS: AS16276 OVH SAS", 6, 37, new Color(0xFFFFFF).getRGB());
        Puppy.INSTANCE.moonFont.drawString("Country: Singapore", 6, 52, new Color(0xFFFFFF).getRGB());
        Puppy.INSTANCE.moonFont.drawString("City: Singapore", 6, 67, new Color(0xFFFFFF).getRGB());
        Puppy.INSTANCE.moonFont.drawString("Region: Central Singapore", 6, 82, new Color(0xFFFFFF).getRGB());
        Puppy.INSTANCE.moonFont.drawString("ISP: OVH SAS", 6, 97, new Color(0xFFFFFF).getRGB());
        GlStateManager.popMatrix();
        this.drawCenteredString(this.fontRendererObj, I18n.format("selectServer.direct"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, I18n.format("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
        this.field_146302_g.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}