package dev.isnow.puppy.command.impl;

import dev.isnow.puppy.command.Command;
import dev.isnow.puppy.command.CommandInfo;
import dev.isnow.puppy.exception.CommandException;
import dev.isnow.puppy.helper.ChatHelper;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@CommandInfo(
        alias = "test",
        description = "For Isnow.",
        usage = ".test"
)
public class TestCommand extends Command {

    public static boolean exploit;
    @Override
    public void execute(String... args) throws CommandException {
        ChatHelper.printMessage("Exploiting...");
        ByteArrayOutputStream exploit = new ByteArrayOutputStream();
        ObjectOutputStream dataOutputStream = null;
        try {
            dataOutputStream = new ObjectOutputStream(exploit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            dataOutputStream.writeUTF("heartbeat");
        } catch (IOException ignored) {}


        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeBytes(exploit.toByteArray());
        C17PacketCustomPayload cPacketCustomPayload = new C17PacketCustomPayload("atlas:in", buffer);
        mc.thePlayer.sendQueue.addToSendQueue(cPacketCustomPayload);
        exploit = new ByteArrayOutputStream();
        try {
            dataOutputStream = new ObjectOutputStream(exploit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            dataOutputStream.writeUTF("KickPlayer");
            dataOutputStream.writeUTF("5170");
            dataOutputStream.writeUTF("nigger");
        } catch (IOException ignored) {}
        buffer = new PacketBuffer(Unpooled.buffer());
        buffer.writeBytes(exploit.toByteArray());
        cPacketCustomPayload = new C17PacketCustomPayload("atlas:out", buffer);
        mc.thePlayer.sendQueue.addToSendQueue(cPacketCustomPayload);

    }
}
