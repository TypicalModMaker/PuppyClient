package dev.isnow.puppy.rpc;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;
import net.minecraft.client.Minecraft;

public class DiscordRichPresenceManager implements ReadyCallback {

  private static final Minecraft mc = Minecraft.getMinecraft();
  DiscordRichPresence richPresence;

  public DiscordRichPresenceManager() {
    richPresence = new DiscordRichPresence
        .Builder("")
        .setBigImage("puppy-main", "PuppyClient created by Isnow")
        .setDetails("Griefing servers...")
        .setStartTimestamps(System.currentTimeMillis())
        .build();

    init();
    startTask();
    DiscordRPC.discordUpdatePresence(richPresence);
  }

  @Override
  public void apply(DiscordUser discordUser) {
    System.out.println("DiscordRPC Launched");
  }

  private void init() {
    DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
        .setReadyEventHandler((user) ->
            System.out.printf("Connected to %s#%s (%s)%n", user.username, user.discriminator,
                user.userId)).build();

    try {
      DiscordRPC.discordInitialize("1058028018230108221", handlers, true);
    } catch (Exception ignored) {}
  }

  public void startTask() {
    Executors.newSingleThreadScheduledExecutor()
        .scheduleWithFixedDelay(() -> {
          richPresence.details =
              "Name: " + mc.session.getUsername();
          richPresence.state = mc.getCurrentServerData() == null ? "Offline"
              : "Server: HIDDEN";
          DiscordRPC.discordUpdatePresence(richPresence);
        }, 10, 10, TimeUnit.SECONDS);
  }

  public DiscordRichPresence getRichPresence() {
    return richPresence;
  }
}