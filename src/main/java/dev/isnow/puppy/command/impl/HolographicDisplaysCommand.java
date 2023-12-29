package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;

import java.util.LinkedList;
import java.util.Queue;

@CommandInfo(
        alias = "removehd",
        description = "Removes all holograms from HolographicDisplays [BUGGY, USE FEW TIMES].",
        usage = ".removehd",
        aliases = {"removeholograms", "hd", "holographicdisplays"}
)
public class HolographicDisplaysCommand extends Command {
    public static boolean enabled;
    public static boolean removing;
    public static int maxpage, currpage = 1;
    public static Queue<String> hologramNames = new LinkedList<>();

    public static long lastEnd = System.currentTimeMillis();

    @Override
    public void execute(String... args) throws CommandException {
        toggle();
        if(enabled) {
            mc.thePlayer.sendChatMessage("/hd list");
        }
    }

    public static void toggle() {
        if((System.currentTimeMillis() - lastEnd) < 500) {
            return;
        }
        lastEnd = System.currentTimeMillis();
        enabled = !enabled;
        removing = false;
        maxpage = 0;
        currpage = 1;
        hologramNames = new LinkedList<>();
        ChatHelper.printMessage(enabled ? "Enabled" : "Finished removing all holograms!");
    }
    public static boolean handleChat(String input) {
        if(input.equals("There are no holograms yet. Create one with /hd create.")) {
            toggle();
            return true;
        }
        if(input.contains("(Page")) {
            maxpage = Integer.parseInt(input.split("Page ")[1].split(" of")[0]);
        } else if(input.startsWith("-")) {
            hologramNames.add(input.split("- ")[1].split(" \\(")[0]);
            if(maxpage == 1) {
                removing = true;
                mc.thePlayer.sendChatMessage("/hd remove " + hologramNames.poll());
            }
        } else if(input.startsWith("TIP")) {
            mc.thePlayer.sendChatMessage("/hd list " + currpage++);
        }
        if(removing) {
            Thread t = new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if(!hologramNames.isEmpty()) {
                    mc.thePlayer.sendChatMessage("/hd remove " + hologramNames.poll());
                } else {
                    toggle();
                }
            });

            t.start();
        }
        return false;
    }
}
