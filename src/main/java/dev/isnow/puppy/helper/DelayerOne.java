package dev.isnow.puppy.helper;

import dev.isnow.puppy.command.impl.TestCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.util.concurrent.TimeUnit;

public class DelayerOne extends ChannelOutboundHandlerAdapter {
    private final long delayMillis;

    public DelayerOne(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if(TestCommand.exploit) {
            ctx.executor().schedule(() -> {
                ctx.write(msg, promise);
            }, delayMillis, TimeUnit.MILLISECONDS);
        } else {
            ctx.write(msg, promise);
        }
    }
}
