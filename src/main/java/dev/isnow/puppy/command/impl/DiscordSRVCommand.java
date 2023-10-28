package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@CommandInfo(
        alias = "discordsrv",
        description = "Spams a discordsrv channel",
        usage = ".discordsrv [channel_id] [delay] [text]",
        aliases = {"dsrv", "discoord", "ds"}
)
public class DiscordSRVCommand extends Command {
    public static Thread thread;

    @Override
    public void execute(String... args) throws CommandException {
        if(args.length < 3) {
            throw new CommandException(getUsage());
        }

        String channelID = args[0];
        long delay = Long.parseLong(args[1]);
        List<String> copiedList = new LinkedList<>(Arrays.asList(args));
        copiedList.remove(0);
        copiedList.remove(0);

        String message = String.join(" ", copiedList);
        thread = new Thread(() -> {
            while (true) {
                if(mc.thePlayer == null) {
                    thread.stop();
                    thread = null;
                    break;
                }
                mc.thePlayer.sendChatMessage("/discordsrv bcast #" + channelID + " " + message);

                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
            }
        });
        thread.start();

    }
}
