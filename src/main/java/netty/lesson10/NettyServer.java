package netty.lesson10;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        server.group(group, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ServerChannelHandler());
                    }
                })
                .bind(9999);
    }

    static class ServerChannelHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            PacketCodeC codeC = PacketCodeC.INSTANCE;
            Packet packet = codeC.decode(buf);
            if (packet instanceof LoginRequestPacket) {
                LoginRequestPacket loginPacket = (LoginRequestPacket) packet;
                String username = loginPacket.getUsername();
                String password = loginPacket.getPassword();
                System.out.println(username);
                System.out.println(password);

                if (username.equals("leefee") && password.equals("123")) {
                    loginPacket.setSuccess(true);
                }
                ByteBuf respBuf = codeC.encode(ctx.alloc(), packet);
                ctx.writeAndFlush(respBuf);

            } else if (packet instanceof ReqMsgPacket) {
                ReqMsgPacket msgPacket = (ReqMsgPacket) packet;
                String message = msgPacket.getMessage();
                System.out.println("服务的收到消息：" + message);

                RespMsgPacket respMsgPacket = new RespMsgPacket();
                respMsgPacket.setMessage("服务端回复【" + message + "】");
                ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc(), respMsgPacket);
                ctx.channel().writeAndFlush(responseByteBuf);
            }
        }
    }

}
