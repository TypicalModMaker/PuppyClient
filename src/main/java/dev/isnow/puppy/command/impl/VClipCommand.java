package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;

@CommandInfo(
        alias = "vclip",
        description = "Teleports you few blocks up or down",
        usage = ".vclip [AMOUNT]",
        aliases = {"vc"}
)
public class VClipCommand extends Command {
    @Override
    public void execute(String... args) throws CommandException {
        if(args.length == 0) {
            ChatHelper.printMessage("Usage:" + this.getUsage());
            return;
        }
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + Integer.parseInt(args[0]), mc.thePlayer.posZ);
        ChatHelper.printMessage("Clipped!");
    }
}
