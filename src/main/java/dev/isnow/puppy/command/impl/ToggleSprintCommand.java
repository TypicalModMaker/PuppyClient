package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;

@CommandInfo(
        alias = "togglesprint",
        description = "Automatically sprints for you.",
        usage = ".togglesprint",
        aliases = {"ts"}
)
public class ToggleSprintCommand extends Command {

    public static boolean toggled;
    @Override
    public void execute(String... args) throws CommandException {
        toggled = !toggled;
        ChatHelper.printMessage("Toggled: " + toggled);
    }
}
