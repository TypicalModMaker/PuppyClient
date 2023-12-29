package dev.isnow.puppy.hook;

import dev.isnow.puppy.Puppy;
import dev.isnow.puppy.helper.DrawHelper;
import dev.isnow.puppy.helper.SaveLoad;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainMenuHook extends GuiScreen {

    public float mouseXOffset = 0, mouseYOffset = 0;
    public MainMenuHook() {

    }

    @Override
    public void initGui() {
        super.initGui();
        int var3 = this.height / 2;
        buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 - 32, 98, 20, "Singleplayer"));
        buttonList.add(new GuiButton(2, this.width / 2 + 2, var3 - 32, 98, 20, "Multiplayer"));

        buttonList.add(new GuiButton(3, this.width / 2 - 100, var3 - 10, 98, 20, "Settings"));
        buttonList.add(new GuiButton(4, this.width / 2 + 2, var3 - 10, 98, 20, "Wallpaper"));

        buttonList.add(new GuiButton(5, this.width / 2 - 100, var3 + 12, 98, 20, "Language"));
        buttonList.add(new GuiButton(69, this.width / 2 + 2, var3 + 12, 98, 20, "Quit"));

        this.addButtons(this.height / 2 + (48 - 70), 24);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1,1,1,1);
        mouseXOffset = 4;
        mouseYOffset = 4;

        GlStateManager.pushMatrix();
        if(Puppy.INSTANCE.wallpaper != null) {
            DrawHelper.draw2DImage(Puppy.INSTANCE.wallpaper, 0, 0, this.width, this.height, new Color(0, 0, 0));
        } else {
            Gui.drawRect(0,0,width,height, new Color(23,23,23).getRGB());
        }
        GlStateManager.popMatrix();


        GlStateManager.pushMatrix();
        GlStateManager.scale(1.2, 1.2, 1.2);
        Puppy.INSTANCE.monsoonFont.drawString("Welcome to Puppy! Current build: " + Puppy.INSTANCE.VER, 2, 4, -1);
        GlStateManager.popMatrix();

        ArrayList<String> changeLogs = new ArrayList<>();

        int line = 1;
        changeLogs.add("");
        changeLogs.add("+ Improved Performance");
        changeLogs.add("+ Updated ViaMCP to 1.20.3/1.20.4 and 1.20.5 (Yes I know this haven't been released yet)");

        changeLogs.add("");

        changeLogs.add("");
        changeLogs.add("");
        changeLogs.add("Developed by 5170 [Isnow].");


        for (String s : changeLogs) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.7, 0.7, 0.7);
            if (s.startsWith("+")) {
                Gui.drawRect(3, this.fontRendererObj.FONT_HEIGHT * line + 5.5f, 4 + 5, this.fontRendererObj.FONT_HEIGHT * line + 11.f, new Color(0, 255, 0).getRGB());
                Puppy.INSTANCE.moonFont.drawString(s, 11, this.fontRendererObj.FONT_HEIGHT * line + 5, -1);
            }
            if (s.startsWith("*")) {
               Gui.drawRect(3, this.fontRendererObj.FONT_HEIGHT * line + 5.5f, 4 + 5, this.fontRendererObj.FONT_HEIGHT * line + 11.f, new Color(255, 255, 0).getRGB());
                Puppy.INSTANCE.moonFont.drawString(s, 11, this.fontRendererObj.FONT_HEIGHT * line + 5, -1);
            }
            if (s.startsWith("-")) {
                Gui.drawRect(3, this.fontRendererObj.FONT_HEIGHT * line + 5.5f, 4 + 5, this.fontRendererObj.FONT_HEIGHT * line + 11.f, new Color(255, 0, 0).getRGB());
                Puppy.INSTANCE.moonFont.drawString(s, 11, this.fontRendererObj.FONT_HEIGHT * line + 5, -1);
            } if(s.isEmpty()) {
                Puppy.INSTANCE.moonFont.drawString(s, 11, this.fontRendererObj.FONT_HEIGHT * line + 5, -1);
            }
            if (s.startsWith("Developed by 5170")) {
                Gui.drawRect(3, this.fontRendererObj.FONT_HEIGHT * line + 5.5f, 4 + 5, this.fontRendererObj.FONT_HEIGHT * line + 11.f, new Color(0, 170, 255).getRGB());
                Puppy.INSTANCE.moonFont.drawString(s, 11, this.fontRendererObj.FONT_HEIGHT * line + 5, -1);
            }
            GlStateManager.popMatrix();
            line++;
        }

        //NotificationManager.render();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void addButtons(int p_73969_1_, int p_73969_2_) {
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);

        switch(button.id) {
            case 1:
                mc.displayGuiScreen(new GuiSelectWorld(this));
                break;
            case 2:
                mc.displayGuiScreen(new GuiMultiplayer(this));
                break;
            case 3:
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                break;
            case 4:
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "JPG, PNG Images", "jpg", "png");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(file);
                        DynamicTexture texture = new DynamicTexture(img);
                        img.getRGB(0, 0, img.getWidth(), img.getHeight(), texture.getTextureData(), 0, img.getWidth());
                        Puppy.INSTANCE.wallpaper = texture;
                        SaveLoad saveLoad = new SaveLoad("wallpaper." + getExtensionByStringHandling(file.getName()), img);
                        saveLoad.save();
                    } catch (IOException e) {
                        break;
                    }
                }
                break;
            case 5:
                mc.displayGuiScreen(new GuiLanguage(this, mc.gameSettings, mc.getLanguageManager()));
                break;
            case 69:
                mc.shutdown();
                break;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void onGuiClosed() {

    }

    public String getExtensionByStringHandling(String filename) {
        return FilenameUtils.getExtension(filename);
    }

}
