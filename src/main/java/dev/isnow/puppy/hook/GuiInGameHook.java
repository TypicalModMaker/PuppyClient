package dev.isnow.puppy.hook;

import dev.isnow.puppy.Puppy;
import dev.isnow.puppy.command.impl.BaltopDumpCommand;
import dev.isnow.puppy.command.impl.ScreenshotCommand;
import dev.isnow.puppy.command.impl.TestCommand;
import dev.isnow.puppy.helper.*;
import dev.isnow.puppy.holder.Holder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiInGameHook extends GuiIngame {

  private static final Minecraft mc = Minecraft.getMinecraft();

  public GuiInGameHook(Minecraft mcIn) {
    super(mcIn);
  }

  @Override
  public void renderGameOverlay(float partialTicks) {
    super.renderGameOverlay(partialTicks);

//    GL11.glScaled(2.0, 2.0, 2.0);
//    mc.fontRendererObj.drawStringWithShadow("PUPPY [Private Edition]", 0, 1, OpenGlHelper.rainbowColor(3000, 1));
//    GL11.glScaled(0.5, 0.5, 0.5);

    String watermark = "PUPPY [Private Edition]" + " | " + Minecraft.debugFPS + " FPS";

//    Gui.drawRect(2,2,	Puppy.INSTANCE.monsoonFont.getStringWidth(watermark) + 9, 19.5f,new Color(20, 20, 20, 255).getRGB());
//    Gui.drawRect(4, 4, Puppy.INSTANCE.monsoonFont.getStringWidth(watermark) + 7, 17.5f, new Color(30, 30, 30, 255).getRGB());
//    Gui.drawRect(4, 4, Puppy.INSTANCE.monsoonFont.getStringWidth(watermark) + 7, 5, ColorHelper.getRGB(4, 0.9f, 1, 10000));
    Puppy.INSTANCE.monsoonFont.drawString(watermark, 6, 7,  ColorHelper.astolfoColors(10, 14));

    if (!mc.isSingleplayer()) {
      long lastPacketMS = TimeHelper.getCurrentTime() - Holder.getLastPacketMS();
      if(TestCommand.exploit) {
        return;
      }
      if(!ScreenshotCommand.hidden) {
        Puppy.INSTANCE.monsoonFont
                .drawStringWithShadow(ChatHelper.fix("&fServer: &c" + mc.getCurrentServerData().serverIP), 6, 18, 0);
      } else {
        Puppy.INSTANCE.monsoonFont
                .drawStringWithShadow(ChatHelper.fix("&fServer: &cHIDDEN"), 6, 18, 0);
      }

      try {
        if (mc.thePlayer.getClientBrand() != null) {
          String brand = mc.thePlayer.getClientBrand().contains("<- ") ?
                  mc.thePlayer.getClientBrand().split(" ")[0] + " -> " + mc.thePlayer.getClientBrand()
                          .split("<- ")[1] : mc.thePlayer.getClientBrand().split(" ")[0];
          Puppy.INSTANCE.monsoonFont.drawStringWithShadow(
                  ChatHelper.fix("&fEngine: &c" + brand), 6, 28, 0);
        }
      } catch (Exception ignored) {
        Puppy.INSTANCE.monsoonFont.drawStringWithShadow(
                ChatHelper.fix("&fEngine: &c" + "INVALID"), 6, 28, 0);
      }

      if (Holder.getLastPacketMS() != -1) {
        String packetMs = (lastPacketMS < 1000
                ? "&a" + lastPacketMS : (lastPacketMS < 7000
                ? "&e" + lastPacketMS : (lastPacketMS < 15000
                ? "&6" + lastPacketMS : "&c" + lastPacketMS)));

        Puppy.INSTANCE.monsoonFont
                .drawStringWithShadow(ChatHelper.fix(String.format("&fLast packet: &c%sms", packetMs)), 6, 38, 0);
      }

      if (Holder.getTPS() != -1) {
        String tps = String.format((Holder.getTPS() > 15
            ? "&a%.2f" : (Holder.getTPS() > 10
            ? "&e%.2f" : (Holder.getTPS() > 5
            ? "&6%.2f" : "&c%.2f"))), Holder.getTPS());

        Puppy.INSTANCE.monsoonFont
            .drawStringWithShadow(ChatHelper.fix(String.format("&fPredicted TPS: &c%s", tps)), 6, 48, 0);
      } else {
        Puppy.INSTANCE.monsoonFont
                .drawStringWithShadow(ChatHelper.fix("&fPredicted TPS: &cInvalid"), 6, 48, 0);
      }

      if(BaltopDumpCommand.running) {
        Puppy.INSTANCE.monsoonFont
                .drawStringWithShadow(ChatHelper.fix("&fBaltopDump:"), 6, 58, 0);
        if(BaltopDumpCommand.finishedScrapingNames) {
          int nigger = BaltopDumpCommand.nigger;
          int difference = nigger - BaltopDumpCommand.playersToDump.size();
          String color = "&c";
          if(difference < (nigger/3) && difference > (nigger/2)) {
            color = "&e";
          } else if(difference < (nigger/4) && difference > (nigger/3)) {
            color = "&a";
          }
          Puppy.INSTANCE.monsoonFont
                  .drawStringWithShadow(ChatHelper.fix("&fPlayers left: " + color + BaltopDumpCommand.playersToDump.size()), 6, 68, 0);
        } else {
          Puppy.INSTANCE.monsoonFont
                  .drawStringWithShadow(ChatHelper.fix("&fCurrent Page: &a" + BaltopDumpCommand.currentPage + "&7/&a" + BaltopDumpCommand.maxPages), 6, 68, 0);
        }
      }
    }
  }
}
