package net.kore.mixins.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.kore.Kore;
import net.kore.events.JoinGameEvent;
import net.kore.events.PacketReceivedEvent;
import net.kore.events.PacketSentEvent;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetAddress;
import java.net.Proxy;

@Mixin(NetworkManager.class)
public abstract class MixinNetworkManager {
    @Shadow public abstract void sendPacket(Packet packetIn);

    @Inject(method = "createNetworkManagerAndConnect", at = @At("HEAD"), cancellable = true)
    private static void createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport, CallbackInfoReturnable<NetworkManager> cir)
    {
        if (!Kore.proxy.isToggled())
            return;

        NetworkManager manager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        Bootstrap bootstrap = new Bootstrap();
        Proxy proxy = Kore.proxy.getProxy();
        OioEventLoopGroup eventLoopGroup = new OioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
        bootstrap.channelFactory(new net.kore.modules.protection.Proxy.ProxyOioChannelFactory(proxy));
        bootstrap.group(eventLoopGroup).handler(new ChannelInitializer<Channel>() {
            protected void initChannel(Channel channel) {
                System.out.println("ILLEGAL CHANNEL INITIALIZATION: This should be patched to net/minecraft/network/NetworkManager$5!");
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException var3) {
                    var3.printStackTrace();
                }
                Kore.proxy.setToggled(false);
                channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new MessageDeserializer2()).addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND)).addLast("prepender", new MessageSerializer2()).addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND)).addLast("packet_handler", manager);
            }
        });
        bootstrap.connect(address, serverPort).syncUninterruptibly();
        cir.setReturnValue(manager);
        cir.cancel();
    }

    private boolean fakePacket = false;
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", cancellable = true, at = @At("HEAD"))
    public void sendPacket(Packet packet, CallbackInfo ci)
    {
        if (MinecraftForge.EVENT_BUS.post(new PacketSentEvent(packet)))
        {
            ci.cancel();
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", cancellable = true, at = @At("RETURN"))
    public void sendPacketPost(Packet packet, CallbackInfo ci)
    {
        if (MinecraftForge.EVENT_BUS.post(new PacketSentEvent.Post(packet)))
            ci.cancel();
    }

    @Inject(method = { "channelRead0" }, at = { @At("HEAD") }, cancellable = true)
    private void onChannelReadHead(final ChannelHandlerContext context, final Packet<?> packet, final CallbackInfo callbackInfo) {
        if (packet instanceof S01PacketJoinGame) {
            MinecraftForge.EVENT_BUS.post(new JoinGameEvent());
        }
        if (MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent(packet))) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = { "channelRead0" }, at = { @At("RETURN") }, cancellable = true)
    private void onPost(final ChannelHandlerContext context, final Packet<?> packet, final CallbackInfo callbackInfo) {
        if (MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent.Post(packet))) {
            callbackInfo.cancel();
        }
    }
}
