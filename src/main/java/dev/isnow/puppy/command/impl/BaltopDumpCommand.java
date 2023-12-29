package dev.isnow.puppy.command.impl;


import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;
import dev.isnow.puppy.helper.SaveLoad;

import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@CommandInfo(
        alias = "baltopdump",
        description = "Dumps ips thru essential's baltop & seen",
        usage = ".baltopdump [pagestring] [addressstring] [playerstring] [antispam]",
        aliases = {"bd", "baltopd"}
)
public class BaltopDumpCommand extends Command {

    public static Queue<String> playersToDump = new LinkedList<>();
    public static ConcurrentHashMap<String, String> parsedAddresses = new ConcurrentHashMap<>();

    public static boolean running, finishedScrapingNames, black;

    public static String page,address, player;

    public static String lastPlayer = "";

    public static int maxPages, nigger;
    public static int currentPage = 1;

    @Override
    public void execute(String... args) throws CommandException {
        if(running) {
            reset();
            return;
        }
        if(args.length < 3) {
            throw new CommandException(getUsage());
        }

        page = args[0];
        address = args[1];
        if(address.equals("Address")) {
            address = "Address:";
        }
        player = args[2];
        running = true;

        mc.thePlayer.sendChatMessage("/essentials:baltop " + currentPage);
    }

    public static void reset() {
        ChatHelper.printMessage("Finished dumping!");
        SaveLoad sl = new SaveLoad("baltop.txt", parsedAddresses);
        sl.save();
        try {
            Desktop.getDesktop().open(sl.dataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        running = false;
        finishedScrapingNames = false;
        maxPages = 0;
        nigger = 0;
        currentPage = 1;
        lastPlayer = "";
        black = false;
        parsedAddresses = new ConcurrentHashMap<>();
        playersToDump = new LinkedList<>();
    }

    public static void handlePlayer(String output) {
        if(Character.isDigit(output.charAt(0))) {
            playersToDump.add(output.split(". ")[1].split(",")[0]);
        }
    }

    public static void retrieveBaltopPage(String output) {
        if(mc.thePlayer == null) {
            reset();
            return;
        }
        if(output.startsWith("Warning:") || output.contains("never joined")) {
            if(!lastPlayer.equals(""))  {
                parsedAddresses.put(lastPlayer, "FAILED");
                lastPlayer = "";

                String player = playersToDump.poll();
                if(player != null && !player.equals("")) {
                    lastPlayer = player;
                    mc.thePlayer.sendChatMessage("/essentials:seen " + player);
                } else {
                    reset();
                }
            } else {
                ChatHelper.printMessage("INVALID PLAYER!");
                lastPlayer = "NEVERJOINED";
                black = true;
                int allah = playersToDump.size();
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if(allah == playersToDump.size()) {
                        // uh oh noob
                        String player = playersToDump.poll();
                        if(player != null && !player.equals("")) {
                            lastPlayer = player;
                            mc.thePlayer.sendChatMessage("/essentials:seen " + player);
                        } else {
                            reset();
                        }
                    }
                });
                t.start();
            }
            return;
        }
        if(output.contains(player)) {
            lastPlayer = output.split(" ")[1].split(" ")[0];
        }
        if(output.contains(page)) {
            String maxPage = output.split(page + " ")[1].split("/")[1].split(" ")[0];
            maxPages = Integer.parseInt(maxPage.replaceAll("[^0-9]", ""));

            String currentPageString = output.split(page + " ")[1].split("/")[0];

            currentPage = Integer.parseInt(currentPageString.replaceAll("[^0-9]", ""));

            if(currentPage == maxPages) {
                ChatHelper.printMessage("Finished scraping player names!");
                nigger = playersToDump.size();
                finishedScrapingNames = true;

                String player = playersToDump.poll();
                if(player != null && !player.equals("")) {
                    lastPlayer = player;
                    mc.thePlayer.sendChatMessage("/essentials:seen " + player);
                }
                return;
            }

            currentPage++;
            mc.thePlayer.sendChatMessage("/essentials:baltop " + currentPage);
        }
        if(output.contains(address)) {
            if(black) {
                black = false;
                String player = playersToDump.poll();
                if(player != null && !player.equals("")) {
                    lastPlayer = player;
                    mc.thePlayer.sendChatMessage("/essentials:seen " + player);
                } else {
                    reset();
                }
                return;
            }
            String ip = output.split(address + " ")[1];
            if(!lastPlayer.equals(""))  {
                parsedAddresses.put(lastPlayer, ip);
                lastPlayer = "";

                String player = playersToDump.poll();
                if(player != null && !player.equals("")) {
                    mc.thePlayer.sendChatMessage("/essentials:seen " + player);
                } else {
                    reset();
                }
            } else {
                ChatHelper.printMessage("INVALID PLAYER!");
            }
        }
    }
}
