// Decompiled with: Procyon 0.6.0
// Class Version: 8
package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.connection.UserConnectionImpl;
import com.viaversion.viaversion.protocol.ProtocolPipelineImpl;
import dev.isnow.puppy.Puppy;
import dev.isnow.puppy.helper.TimeHelper;
import dev.isnow.puppy.holder.Holder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.util.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import vialoadingbase.netty.event.CompressionReorderEvent;
import viamcp.ViaMCP;

import javax.crypto.SecretKey;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class NetworkManager extends SimpleChannelInboundHandler<Packet>
{
    private static final Logger logger;
    public static final Marker logMarkerNetwork;
    public static final Marker logMarkerPackets;
    public static final AttributeKey<EnumConnectionState> attrKeyConnectionState;
    public static final LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP;
    public static final LazyLoadBase<EpollEventLoopGroup> field_181125_e;
    public static final LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP;
    private final EnumPacketDirection direction;
    private final Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue;
    private final ReentrantReadWriteLock field_181680_j;
    private Channel channel;
    private SocketAddress socketAddress;
    private INetHandler packetListener;
    private IChatComponent terminationReason;
    private boolean isEncrypted;
    private boolean disconnected;

    public NetworkManager(final EnumPacketDirection packetDirection) {
        this.outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
        this.field_181680_j = new ReentrantReadWriteLock();
        this.direction = packetDirection;
    }

    public static NetworkManager func_181124_a(final InetAddress p_181124_0_, final int p_181124_1_, final boolean p_181124_2_) {
        final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        Class<? extends SocketChannel> oclass;
        LazyLoadBase<? extends EventLoopGroup> lazyloadbase;
        if (Epoll.isAvailable() && p_181124_2_) {
            oclass = EpollSocketChannel.class;
            lazyloadbase = NetworkManager.field_181125_e;
        }
        else {
            oclass = NioSocketChannel.class;
            lazyloadbase = NetworkManager.CLIENT_NIO_EVENTLOOP;
        }
        new Bootstrap().group(lazyloadbase.getValue()).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(final Channel p_initChannel_1_) throws Exception {
                try {
                    p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, true);
                }
                catch (final ChannelException ex) {}
                p_initChannel_1_.pipeline().addLast((String)"timeout", (ChannelHandler)(new ReadTimeoutHandler(30))).addLast((String)"splitter", (ChannelHandler)(new MessageDeserializer2())).addLast((String)"decoder", (ChannelHandler)(new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))).addLast((String)"prepender", (ChannelHandler)(new MessageSerializer2())).addLast((String)"encoder", (ChannelHandler)(new MessageSerializer(EnumPacketDirection.SERVERBOUND))).addLast((String)"packet_handler", (ChannelHandler)networkmanager);

                if (p_initChannel_1_ instanceof SocketChannel && vialoadingbase.ViaLoadingBase.getInstance().getTargetVersion().getVersion() != ViaMCP.NATIVE_VERSION) {
                    final UserConnection user = new UserConnectionImpl(p_initChannel_1_, true);
                    new ProtocolPipelineImpl(user);

                    p_initChannel_1_.pipeline().addLast(new viamcp.MCPVLBPipeline(user));
                }
            }
        }).channel(oclass).connect(p_181124_0_, p_181124_1_).syncUninterruptibly();
        return networkmanager;
    }

    public void setConnectionState(final EnumConnectionState newState) {
        this.channel.attr(NetworkManager.attrKeyConnectionState).set(newState);
        this.channel.config().setAutoRead(true);
        NetworkManager.logger.debug("Enabled auto read");
    }

    public static NetworkManager provideLocalClient(final SocketAddress address) {
        final NetworkManager networkmanager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        new Bootstrap().group(NetworkManager.CLIENT_LOCAL_EVENTLOOP.getValue()).handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(final Channel p_initChannel_1_) throws Exception {
                p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
            }
        }).channel(LocalChannel.class).connect(address).syncUninterruptibly();
        return networkmanager;
    }

    @Override
    public void channelActive(final ChannelHandlerContext p_channelActive_1_) throws Exception {
        super.channelActive(p_channelActive_1_);
        this.channel = p_channelActive_1_.channel();
        this.socketAddress = this.channel.remoteAddress();
        try {
            this.setConnectionState(EnumConnectionState.HANDSHAKING);
        }
        catch (final Throwable throwable) {
            NetworkManager.logger.fatal(throwable);
        }
    }

    @Override
    public void channelInactive(final ChannelHandlerContext p_channelInactive_1_) throws Exception {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext p_exceptionCaught_1_, final Throwable p_exceptionCaught_2_) throws Exception {
        ChatComponentTranslation chatcomponenttranslation;
        if (p_exceptionCaught_2_ instanceof TimeoutException) {
            chatcomponenttranslation = new ChatComponentTranslation("disconnect.timeout");
        }
        else {
            chatcomponenttranslation = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + p_exceptionCaught_2_);
        }
        this.closeChannel(chatcomponenttranslation);
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Packet packet) {
        if (this.channel.isOpen()) {
            try {
                Holder.setLastPacketMS(TimeHelper.getCurrentTime());
                packet.processPacket(this.packetListener);
            }
            catch (final ThreadQuickExitException ex) {}
        }
    }

    public void setNetHandler(final INetHandler handler) {
        Validate.notNull(handler, "packetListener");
        NetworkManager.logger.debug("Set listener of {} to {}", new Object[] { this, handler });
        this.packetListener = handler;
    }

    public void sendPacket(final Packet packetIn) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, null);
        }
        else {
            this.field_181680_j.writeLock().lock();
            try {
                this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
            }
            finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
    }

    public void sendPacket(final Packet packetIn, final GenericFutureListener<? extends Future<? super Void>> listener, final GenericFutureListener<? extends Future<? super Void>>... listeners) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetIn, ArrayUtils.add(listeners, 0, listener));
        }
        else {
            this.field_181680_j.writeLock().lock();
            try {
                this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packetIn, ArrayUtils.add(listeners, 0, listener)));
            }
            finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
    }

    public void processReceivedPackets() {
        this.flushOutboundQueue();
        if (this.packetListener instanceof ITickable) {
            ((ITickable)this.packetListener).update();
        }
        this.channel.flush();
    }

    public SocketAddress getRemoteAddress() {
        return this.socketAddress;
    }

    private void dispatchPacket(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
        Minecraft mc = Minecraft.getMinecraft();
        final EnumConnectionState enumconnectionstate = EnumConnectionState.getFromPacket(inPacket);
        final EnumConnectionState enumconnectionstate1 = this.channel.attr(attrKeyConnectionState).get();
        if (inPacket instanceof C00Handshake) {
            if (Puppy.INSTANCE.bungeeHack && !Puppy.INSTANCE.sessionPremium && !Puppy.INSTANCE.premiumUUID && ((C00Handshake)inPacket).getRequestedState() == EnumConnectionState.LOGIN) {
                ((C00Handshake)inPacket).setIp(((C00Handshake)inPacket).getIp() + "\u0000" + Puppy.INSTANCE.ipBungeeHack + "\u0000" + UUID.nameUUIDFromBytes(("OfflinePlayer:" + Puppy.INSTANCE.fakeNick).getBytes()).toString().replace("-", ""));
            }
            if (Puppy.INSTANCE.bungeeHack && !Puppy.INSTANCE.sessionPremium && Puppy.INSTANCE.premiumUUID && ((C00Handshake)inPacket).getRequestedState() == EnumConnectionState.LOGIN) {
                ((C00Handshake)inPacket).setIp(((C00Handshake)inPacket).getIp() + "\u0000" + Puppy.INSTANCE.ipBungeeHack + "\u0000" + Puppy.INSTANCE.PreUUID);
            }
        }
        if (Puppy.INSTANCE.bungeeHack && Puppy.INSTANCE.sessionPremium && ((C00Handshake)inPacket).getRequestedState() == EnumConnectionState.LOGIN) {
            ((C00Handshake)inPacket).setIp(((C00Handshake)inPacket).getIp() + "\u0000" + Puppy.INSTANCE.ipBungeeHack + Puppy.INSTANCE.PreUUID);
        }
        if (enumconnectionstate1 != enumconnectionstate) {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (enumconnectionstate != enumconnectionstate1) {
                this.setConnectionState(enumconnectionstate);
            }
            ChannelFuture channelfuture = this.channel.writeAndFlush(inPacket);
            if (futureListeners != null) {
                channelfuture.addListeners(futureListeners);
            }
            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable(){

                @Override
                public void run() {
                    if (enumconnectionstate != enumconnectionstate1) {
                        NetworkManager.this.setConnectionState(enumconnectionstate);
                    }
                    ChannelFuture channelfuture1 = NetworkManager.this.channel.writeAndFlush(inPacket);
                    if (futureListeners != null) {
                        channelfuture1.addListeners(futureListeners);
                    }
                    channelfuture1.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    public boolean isLocalChannel() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    private void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            this.field_181680_j.readLock().lock();
            try {
                while (!this.outboundPacketsQueue.isEmpty()) {
                    final InboundHandlerTuplePacketListener networkmanager$inboundhandlertuplepacketlistener = this.outboundPacketsQueue.poll();
                    this.dispatchPacket(networkmanager$inboundhandlertuplepacketlistener.packet, networkmanager$inboundhandlertuplepacketlistener.futureListeners);
                }
            }
            finally {
                this.field_181680_j.readLock().unlock();
            }
        }
    }

    public void closeChannel(final IChatComponent message) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.terminationReason = message;
        }
        Holder.setTPS(-1.0);
        Holder.setLastPacketMS(-1L);
        Holder.getTpsTimes().clear();
    }

    public void enableEncryption(final SecretKey key) {
        this.isEncrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
    }

    public boolean getIsencrypted() {
        return this.isEncrypted;
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public boolean hasNoChannel() {
        return this.channel == null;
    }

    public INetHandler getNetHandler() {
        return this.packetListener;
    }

    public IChatComponent getExitMessage() {
        return this.terminationReason;
    }

    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    public void setCompressionTreshold(final int treshold) {
        if (treshold >= 0)
        {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder)
            {
                ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
            }
            else
            {
                this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(treshold));
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder)
            {
                ((NettyCompressionEncoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
            }
            else
            {
                this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(treshold));
            }
        }
        else
        {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder)
            {
                this.channel.pipeline().remove("decompress");
            }

            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder)
            {
                this.channel.pipeline().remove("compress");
            }
        }
        this.channel.pipeline().fireUserEventTriggered(new CompressionReorderEvent());
    }

    public void checkDisconnected() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.disconnected) {
                this.disconnected = true;
                if (this.getExitMessage() != null) {
                    this.getNetHandler().onDisconnect(this.getExitMessage());
                }
                else if (this.getNetHandler() != null) {
                    this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
                }
            }
            else {
                NetworkManager.logger.warn("handleDisconnection() called twice");
            }
        }
    }

    static {
        logger = LogManager.getLogger();
        logMarkerNetwork = MarkerManager.getMarker("NETWORK");
        logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", NetworkManager.logMarkerNetwork);
        attrKeyConnectionState = AttributeKey.valueOf("protocol");
        CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
            @Override
            protected NioEventLoopGroup load() {
                return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
            }
        };
        field_181125_e = new LazyLoadBase<EpollEventLoopGroup>() {
            @Override
            protected EpollEventLoopGroup load() {
                return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
            }
        };
        CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
            @Override
            protected LocalEventLoopGroup load() {
                return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
            }
        };
    }

    static class InboundHandlerTuplePacketListener
    {
        private final Packet packet;
        private final GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

        public InboundHandlerTuplePacketListener(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}
