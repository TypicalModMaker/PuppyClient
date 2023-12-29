package dev.isnow.puppy;

import dev.isnow.puppy.command.CommandManager;
import dev.isnow.puppy.command.impl.*;
import dev.isnow.puppy.exploit.ExploitManager;
import dev.isnow.puppy.exploit.impl.creative.AnvilExploit;
import dev.isnow.puppy.exploit.impl.flood.AttackExploit;
import dev.isnow.puppy.exploit.impl.flood.ChannelExploit;
import dev.isnow.puppy.exploit.impl.flood.CommandBlockExploit;
import dev.isnow.puppy.exploit.impl.flood.LuckpermsExploit;
import dev.isnow.puppy.exploit.impl.nbt.*;
import dev.isnow.puppy.exploit.impl.other.FaweExploit;
import dev.isnow.puppy.exploit.impl.other.MultiverseExploit;
import dev.isnow.puppy.exploit.impl.other.PEXExploit;
import dev.isnow.puppy.exploit.impl.other.SpamExploit;
import dev.isnow.puppy.helper.MinecraftFontRenderer;
import dev.isnow.puppy.rpc.DiscordRichPresenceManager;
import net.arikia.dev.drpc.DiscordRPC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.item.Item;
import net.minecraft.util.Session;
import org.lwjgl.opengl.Display;
import viamcp.ViaMCP;

import java.io.File;
import java.util.HashMap;

public enum Puppy {
  INSTANCE;

  public final CommandManager commandManager;
  private final ExploitManager exploitManager;
  private DiscordRichPresenceManager discordRichPresence;

  public final String VER = "1.5-PUBLIC";

  public Session orginalSession;
  public String PreUUID;
  public boolean bungeeHack;
  public boolean premiumUUID;
  public boolean sessionPremium;

  public boolean autoVersion;

  public boolean qMode;

  public String ipBungeeHack = "1.2.3.4";
  public String fakeNick = "5170";

  public MinecraftFontRenderer monsoonFont;
  public MinecraftFontRenderer monsoonLargeFont;

  public MinecraftFontRenderer moonFont;
  public final HashMap<Item, String> toolBinds = new HashMap<>();
  public final HashMap<Integer, String> keyToolBinds = new HashMap<>();

  public DynamicTexture wallpaper;

  public boolean dev = false;

  Puppy() {
    String hey = "Dear Person who decompiled this. Please fuck yourself! And to whoever leaked this jar, kill yourself!";

    System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");

    try
    {
      ViaMCP.create();
      ViaMCP.INSTANCE.initAsyncSlider(80, 5, 100, 20);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    try {
      discordRichPresence = new DiscordRichPresenceManager();
    } catch (Exception ignored) {}

    commandManager = new CommandManager(
        new ExploitCommand(),
        new HelpCommand(),
        new OnlineCommand(),
        new FakeGamemodeCommand(),
        new BindCommand(),
        new PluginsCommand(),
        new RainbowSpamCommand(),
        new StopRainbowSpamCommand(),
        new ScreenshotCommand(),
        new ToggleSprintCommand(),
        new BindButtonCommand(),
        new BaltopDumpCommand(),
        new QCommand(),
        new DiscordSRVCommand(),
        new StopDiscordSRVCommand(),
        new HolographicDisplaysCommand(),
        new TestCommand(),
        new MotionBlurCommand(),
        new VClipCommand(),
        new BanAllCommand(),
        new FetchAllCommand(),
        new FakeBroadcast()
    );

    exploitManager = new ExploitManager(
        new AnvilExploit(),
        new AttackExploit(),
        new BookExploit(),
        new InvalidItem(),
        new SpamExploit(),
        new CommandBlockExploit(),
        new FaweExploit(),
        new ExploitFixer2Exploit(),
        new ExploitFixerExploit(),
        new OnePacketExploit(),
        new MultiverseExploit(),
        new PEXExploit(),
        new ChannelExploit(),
        new LuckpermsExploit()
    );

    if(orginalSession == null) {
      orginalSession = Minecraft.getMinecraft().getSession();
      fakeNick = orginalSession.getUsername();
    }

    File dir = new File(Minecraft.getMinecraft().mcDataDir, "Puppy");
    File configDir = new File(dir, "SaveableItems");
    if(!dir.exists()) {
      dir.mkdir();
    }
    if(!configDir.exists()) {
      configDir.mkdir();
    }
    Runtime.getRuntime().addShutdownHook(new Thread(this::shutDown));
  }

  public void setDisplay() {
    Display.setTitle("PuppyClient | " + VER);
  }

  public void shutDown() {
    DiscordRPC.discordShutdown();
  }

  public CommandManager getCommandManager() {
    return commandManager;
  }

  public ExploitManager getExploitManager() {
    return exploitManager;
  }

  public DiscordRichPresenceManager getDiscordRichPresence() {
    return discordRichPresence;
  }
}
