package netty.lesson10;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Date;
import java.util.Scanner;

public class NettyClient {

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
                if(loginPacket.isSuccess()) {
                    LoginUtil.markAsLogin(ctx.channel());
                }
            } else if (packet instanceof RespMsgPacket) {
                RespMsgPacket respMsgPacket = (RespMsgPacket) packet;
                String message = respMsgPacket.getMessage();
                System.out.println(new Date() + ": 收到服务端的消息: " + message);
            }
        }
    }

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
        new Thread(() -> {
            while (!Thread.interrupted()) {
                if (LoginUtil.hasLogin(channel)) {
                    System.out.println("输入消息发送至服务端:");
                    Scanner sc = new Scanner(System.in);
                    String nextLine = sc.nextLine();

                    ReqMsgPacket reqMsgPacket = new ReqMsgPacket();
                    reqMsgPacket.setMessage(nextLine);
                    ByteBuf buf = PacketCodeC.INSTANCE.encode(channel.alloc(), reqMsgPacket);
                    channel.writeAndFlush(buf);
                }
            }
        }).start();
    }


}

