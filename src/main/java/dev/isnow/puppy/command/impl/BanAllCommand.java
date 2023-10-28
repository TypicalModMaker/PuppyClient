package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.Puppy;
import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.SaveLoad;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@CommandInfo(
        alias = "banall",
        description = "Bans all online players from a file",
        usage = ".banall [command]"
)
public class BanAllCommand extends Command {
    @Override
    public void execute(String... args) throws CommandException {
        if (args.length < 1) {
            return;
        }
        String command = String.join(" ", args);
        command = command.replaceAll("/", "");
        final SaveLoad players = new SaveLoad("players.txt");
        players.load();
        final String stringarray = (String) players.item;
        String[] resplit = stringarray.split( ", ");
        for(String player : resplit) {
            mc.thePlayer.sendChatMessage(command.replaceAll("%player%", player));
        }
    }

}
