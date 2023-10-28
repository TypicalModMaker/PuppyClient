package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;

@CommandInfo(
        alias = "sdsrvs",
        description = "Stops the discordsrv spam.",
        usage = ".stopdiscordsrvspam",
        aliases = {"stopdiscordsrvspam", "sdsrvs", "stopdiscordspam", "sdcs"}
)
public class StopDiscordSRVCommand extends Command {

    @Override
    public void execute(String... args) throws CommandException {
        if(DiscordSRVCommand.thread != null) {
            DiscordSRVCommand.thread.stop();
            DiscordSRVCommand.thread = null;
            ChatHelper.printMessage("Stopped!");
        } else {
            ChatHelper.printMessage("DiscordSRV spam is not running!");
        }
    }
}
