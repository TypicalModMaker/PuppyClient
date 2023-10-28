package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.Puppy;
import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;

@CommandInfo(
    alias = "help"
)
public class HelpCommand extends Command {

  @Override
  public void execute(String... args) throws CommandException {
    if (args.length > 0) {
      ChatHelper.printMessage("\n", false);
      Command command = Puppy.INSTANCE.getCommandManager().getCommand(args[0])
          .orElseThrow(
              () -> new CommandException(String.format("Command \"%s\" not found.\n", args[0])));

      ChatHelper
          .printMessage(String.format("&5%s&f: &c%s\n", command.getAlias(), command.getUsage()));

      return;
    }

    ChatHelper.printMessage("\n", false);
    Puppy.INSTANCE.getCommandManager().getCommands().stream()
        .filter(command -> !(command instanceof HelpCommand))
        .forEach(command -> ChatHelper.printMessage(String.format("&5%s &f- &7%s", command.getAlias(), command.getDescription())));

    ChatHelper.printMessage("\n", false);
  }
}
