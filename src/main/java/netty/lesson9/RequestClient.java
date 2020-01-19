package netty.lesson9;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;

public class RequestClient {


    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()//返回的是和这条连接相关的逻辑处理链，采用了责任链模式，
                                .addLast(new ReqClientHandler());//addLast() 方法 添加一个逻辑处理器，
                    }
                })
                .connect("localhost", 9999);
    }


    static class ReqClientHandler extends ChannelInboundHandlerAdapter {
        //连接建立后，开发发送登录请求
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            LoginRequestPacket loginPacket = new LoginRequestPacket();
            loginPacket.setUsername("leefee");
            loginPacket.setUserId(1);
            loginPacket.setPassword("123");
            PacketCodeC codeC = PacketCodeC.INSTANCE;
            ByteBuf buf = codeC.encode(ctx.alloc(), loginPacket);
            ctx.writeAndFlush(buf);
        }

        //收到服务端响应后激活该read方法
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            Packet packet = PacketCodeC.INSTANCE.decode(buf);
            if (packet instanceof LoginRequestPacket) {
                LoginRequestPacket loginPacket = (LoginRequestPacket) packet;
                System.out.println(new Date() + ": 客户端收到登录结果： -> " + loginPacket.isSuccess());
            }
        }
    }


}

