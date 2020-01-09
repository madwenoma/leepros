package netty.lesson11;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
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
                        ch.pipeline()
                                .addLast(new PacketDecoder())
                                .addLast(new LoginRequestHandler())
                                .addLast(new MessageRequestHandler())
                                .addLast(new PacketEncoder());
                    }
                })
                .bind(9999);
    }


}

class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestPacket loginPacket) throws Exception {
        String username = loginPacket.getUsername();
        String password = loginPacket.getPassword();
        System.out.println(username);
        System.out.println(password);
        LoginResponsePacket responsePacket = new LoginResponsePacket();
        if (username.equals("leefee") && password.equals("123")) {
            responsePacket.setSuccess(true);
        }
        ctx.channel().writeAndFlush(responsePacket);
    }
}

class MessageRequestHandler extends SimpleChannelInboundHandler<ReqMsgPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReqMsgPacket reqMsgPacket) throws Exception {
        String message = reqMsgPacket.getMessage();
        System.out.println("服务的收到消息：" + message);

        RespMsgPacket respMsgPacket = new RespMsgPacket();
        respMsgPacket.setMessage("服务端回复【" + message + "】");
        ctx.channel().writeAndFlush(respMsgPacket);
    }
}
