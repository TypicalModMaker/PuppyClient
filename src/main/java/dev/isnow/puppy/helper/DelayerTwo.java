package dev.isnow.puppy.helper;

import dev.isnow.puppy.command.impl.TestCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

public class DelayerTwo extends ChannelInboundHandlerAdapter {
    private final long delayMillis;

    public DelayerTwo(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(TestCommand.exploit) {
            ctx.executor().schedule(() -> {
                ctx.fireChannelRead(msg);
            }, delayMillis, TimeUnit.MILLISECONDS);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
