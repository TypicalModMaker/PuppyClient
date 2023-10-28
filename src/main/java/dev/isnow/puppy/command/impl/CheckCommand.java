package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;

@CommandInfo(
        alias = "check",
        description = "Automatically joins servers from a file for griefable servers",
        usage = ".check [file]",
        aliases = {"b"}
)
public class CheckCommand extends Command {
    @Override
    public void execute(String... args) throws CommandException {

    }
}
