package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;

import java.util.Objects;

@CommandInfo(
        alias = "screenshot",
        description = "Hides the server ip and makes the screenshot automatically.",
        usage = ".screenshot [on/off]",
        aliases = {"ss"}
)
public class ScreenshotCommand extends Command {

    public static boolean hidden;
    public static boolean takess;

    @Override
    public void execute(String... args) throws CommandException {
        if(args.length == 0) {
            hidden = true;
            ChatHelper.printMessage("IP Hidden!");
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                takess = true;
                hidden = true;
            });
            t.start();
        } else if(Objects.equals(args[0], "off")){
            hidden = false;
            ChatHelper.printMessage("IP UnHidden!");
        } else {
            hidden = true;
            ChatHelper.printMessage("IP Hidden!");
        }
    }
}
