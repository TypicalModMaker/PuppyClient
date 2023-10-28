package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@CommandInfo(
        alias = "rainbowspam",
        description = "Spams a command with specific rainbow message.",
        usage = ".rainbowspam [delay] [command] [message]",
        aliases = {"rs"}
)
public class RainbowSpamCommand extends Command {

    public static Thread thread;

    public String[] rainbowColors = {"4", "c", "6", "e", "2", "a", "b", "3", "1", "9", "d", "5", "f", "7", "8", "0"};

    @Override
    public void execute(String... args) throws CommandException {
        if(args.length < 3) {
            throw new CommandException(getUsage());
        }

        int delay = Integer.parseInt(args[0]);
        String command = args[1];
        List<String> copiedList = new LinkedList<>(Arrays.asList(args));
        copiedList.remove(0);
        copiedList.remove(0);

        ChatHelper.printMessage("Started!");
        thread = new Thread(() -> {
            int currentColor = 0;
            while (true) {
                if(mc.thePlayer == null) {
                    thread.stop();
                    thread = null;
                    break;
                }

                if(currentColor > rainbowColors.length - 1) {
                    currentColor = 0;
                }

                mc.thePlayer.sendChatMessage(command + " &" + rainbowColors[currentColor] + String.join(" ", copiedList));
                currentColor++;

                try { Thread.sleep(delay); } catch (InterruptedException ignored) {}
            }
        });
        thread.start();

    }
}
