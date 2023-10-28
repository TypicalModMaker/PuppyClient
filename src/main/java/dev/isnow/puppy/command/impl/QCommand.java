package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.Puppy;
import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import org.lwjgl.opengl.Display;

@CommandInfo(
        alias = "q",
        description = "SpookyGriefing mode.",
        usage = ".q",
        aliases = {"qmode"}
)
public class QCommand extends Command {
    @Override
    public void execute(String... args) throws CommandException {
        Puppy.INSTANCE.qMode = !Puppy.INSTANCE.qMode;

        String q = Puppy.INSTANCE.qMode ? "q" : "PuppyClient | " + Puppy.INSTANCE.VER;
        Display.setTitle(q);
    }
}
