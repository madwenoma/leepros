package netty.lesson16;

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
                                .addLast(new PacketDecoder())
                                .addLast(new LoginResponseHandler())
                                .addLast(new MessageResponseHandler())
                                .addLast(new PacketEncoder());//addLast() 方法 添加一个逻辑处理器，
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
        LoginRequestPacket loginPacket = new LoginRequestPacket();
        new Thread(() -> {

            while (!Thread.interrupted()) {
                if (!SessionUtil.hasLogin(channel)) {
                    String nextLine = sc.nextLine();
                    System.out.println("输入用户名登录:");

                    loginPacket.setUsername(nextLine);

                    channel.writeAndFlush(loginPacket);

                    waitForLoginResponse();
                } else {
                    String toUserId = sc.next();
                    String msg = sc.next();

                    ReqMsgPacket reqMsgPacket = new ReqMsgPacket();
                    reqMsgPacket.setToUserId(toUserId);
                    reqMsgPacket.setMessage(msg);

                    channel.writeAndFlush(reqMsgPacket);
                }
            }
        }).start();
    }


    private static void waitForLoginResponse() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
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
            System.out.println("登录成功");
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
