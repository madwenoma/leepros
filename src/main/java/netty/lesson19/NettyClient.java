package netty.lesson19;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.Scanner;

class NettyClient {


    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()//返回的是和这条连接相关的逻辑处理链，采用了责任链模式，
                                .addLast(PacketCodecHandler.INSTANCE)
                                .addLast(new LoginResponseHandler())
                                .addLast(new MessageResponseHandler())
                                .addLast(new CreateGroupResponseHandler())
                                .addLast(new JoinGroupResponseHandler())
                                .addLast(new ListGroupResponseHandler())
                                .addLast(new QuitGroupResponseHandler())
                                .addLast(new GroupMessageResponseHandler());
                    }
                });
        connect(bootstrap, "localhost", 9999);
    }


    private static void connect(Bootstrap bootstrap, String host, int port) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                //关键点，此处的future可强制转换为channelFuture
                ChannelFuture channelFuture = (ChannelFuture) future;
                Channel channel = channelFuture.channel();
                startConsoleThread(channel);
            } else {
                System.out.println("connect failed");
            }

        });
    }

    //监控
    public static void startConsoleThread(Channel channel) {
        Scanner sc = new Scanner(System.in);
        LoginConsoleCommand loginConsoleCommand = new LoginConsoleCommand();
        ConsoleCommandManager manager = new ConsoleCommandManager();
        new Thread(() -> {

            while (!Thread.interrupted()) {
                if (!SessionUtil.hasLogin(channel)) {
                    loginConsoleCommand.exec(sc, channel);
                } else {
                    manager.exec(sc, channel);
                }
            }
        }).start();
    }

}


class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        LoginRequestPacket loginPacket = new LoginRequestPacket();
//        loginPacket.setUsername("leefee");
//        loginPacket.setUserId(1);
//        loginPacket.setPassword("123");
//        ctx.channel().writeAndFlush(loginPacket);
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponsePacket loginRespPacket) throws Exception {
        boolean loginResult = loginRespPacket.isSuccess();
        String userId = loginRespPacket.getUserId();
        String userName = loginRespPacket.getUserName();
        if (loginResult) {
            SessionUtil.bindSession(new Session(userId, userName), ctx.channel());
            System.out.println(userId + "登录成功");
        } else {
            System.out.println("登录失败，原因：" + loginRespPacket.getTag());
        }
    }
}

class MessageResponseHandler extends SimpleChannelInboundHandler<RespMsgPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RespMsgPacket respMsgPacket) throws Exception {
        String message = respMsgPacket.getMessage();
        String fromUserName = respMsgPacket.getFromUserName();
        System.out.println(new Date() + ": 收到" + fromUserName + "的消息: " + message);
    }
}


class CreateGroupResponseHandler extends SimpleChannelInboundHandler<CreateGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CreateGroupResponsePacket responsePacket) throws Exception {
        System.out.println(new Date() + ": 创建结果: " + responsePacket.isSuccess());
        System.out.println(new Date() + ": 聊天室id: " + responsePacket.getGroupId());
        System.out.println(new Date() + ": 当前成员: " + responsePacket.getUserNameList());
    }
}

class JoinGroupResponseHandler extends SimpleChannelInboundHandler<JoinGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinGroupResponsePacket responsePacket) throws Exception {
        System.out.println(new Date() + ": 加入聊天室操作结果: " + responsePacket.isSuccess());
    }
}

class ListGroupResponseHandler extends SimpleChannelInboundHandler<ListGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ListGroupResponsePacket responsePacket) throws Exception {
        System.out.println(new Date() + ": 当前成员: " + responsePacket.getUserNameList());
    }
}

class QuitGroupResponseHandler extends SimpleChannelInboundHandler<QuitGroupResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, QuitGroupResponsePacket responsePacket) throws Exception {
        System.out.println(new Date() + ": 离开聊天室操作结果: " + responsePacket.isSuccess());
    }
}

class GroupMessageResponseHandler extends SimpleChannelInboundHandler<GroupMessageResponsePacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupMessageResponsePacket responsePacket) {
        String fromGroupId = responsePacket.getFromGroupId();
        String fromUser = responsePacket.getFromUser();
        System.out.println("收到群[" + fromGroupId + "]中[" + fromUser + "]发来的消息：" + responsePacket.getMessage());
    }
}

