package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;

@CommandInfo(
        alias = "srs",
        description = "Stops the rainbow spam.",
        usage = ".stoprainbowspam",
        aliases = {"stoprainbowspam"}
)
public class StopRainbowSpamCommand extends Command {
    @Override
    public void execute(String... args) throws CommandException {
        if(RainbowSpamCommand.thread != null) {
            RainbowSpamCommand.thread.stop();
            RainbowSpamCommand.thread = null;
            ChatHelper.printMessage("Stopped!");
        } else {
            ChatHelper.printMessage("Rainbow spam is not running!");
        }
    }
}
