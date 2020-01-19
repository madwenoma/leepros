package netty.lesson16;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.UUID;

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
                                .addLast(new AuthHandler())
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

        if (username != null) {
            String userId = UUID.randomUUID().toString().split("-")[0];
            System.out.println(userId + "登录成功");
            responsePacket.setSuccess(true);
            responsePacket.setUserId(userId);
            responsePacket.setUserName(username);
            SessionUtil.bindSession(new Session(userId, username), ctx.channel());
        } else {
            System.out.println("登录失败");
        }
        // 登录响应
        ctx.channel().writeAndFlush(responsePacket);
    }
}

class MessageRequestHandler extends SimpleChannelInboundHandler<ReqMsgPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReqMsgPacket reqMsgPacket) throws Exception {
        Session session = SessionUtil.getSession(ctx.channel());


        String message = reqMsgPacket.getMessage();
        String toUserId = reqMsgPacket.getToUserId();
        System.out.println("服务端接收到要发送给[" + toUserId + "]的消息：" + message);

        RespMsgPacket respMsgPacket = new RespMsgPacket();

        Channel toUserChanel = SessionUtil.getChannel(toUserId);
        if (toUserChanel == null)
            System.out.println(toUserId + "未登录");
        respMsgPacket.setMessage(message);
        respMsgPacket.setFromUserName(session.getUserName());
        toUserChanel.writeAndFlush(respMsgPacket);
    }
}

class AuthHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (SessionUtil.hasLogin(ctx.channel())) {
            ctx.pipeline().remove(this);//已经登录了，不需要在鉴权
            super.channelRead(ctx, msg);
        } else {
            ctx.channel().close();//未登录不能操作
        }
    }
}