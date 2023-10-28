package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.Puppy;
import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

@CommandInfo(
        alias = "bind",
        description = "Binds a command to a tool",
        usage = ".bind [command]",
        aliases = {"b"}
)
public class BindCommand extends Command {
    @Override
    public void execute(String... args) throws CommandException {
        if(args.length == 0) {
            throw new CommandException(getUsage());
        }
        if(mc.thePlayer.inventory.getCurrentItem() != null) {
            Puppy.INSTANCE.toolBinds.put(mc.thePlayer.inventory.getCurrentItem().getItem(), String.join(" ", args));
            ChatHelper.printMessage("Bound command to " + mc.thePlayer.inventory.getCurrentItem().getItem().getUnlocalizedName());
        }
    }
}
