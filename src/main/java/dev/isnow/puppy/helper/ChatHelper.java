package dev.isnow.puppy.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public final class ChatHelper {

  private static final String PREFIX = "[Puppy]";
  private static final Minecraft mc = Minecraft.getMinecraft();

  public static String fix(String string) {
    return string.replace('&', '§').replace(">>", "»");
  }

  public static void printMessage(String message) {
    printMessage(message, true);
  }

  public static void printMessage(String message, boolean prefix) {
    if(mc.thePlayer == null) {
      return;
    }
    if(prefix) {
      mc.thePlayer.addChatMessage(new ChatComponentText(fix("&c" + PREFIX + " &7" + message)));
    } else {
      mc.thePlayer.addChatMessage(new ChatComponentText(fix(message)));
    }
  }
}
