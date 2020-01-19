package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final int MAX_RETRY = 5;

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline()//返回的是和这条连接相关的逻辑处理链，采用了责任链模式，
                                .addLast(new FirstClientHandler());//addLast() 方法 添加一个逻辑处理器，
                    }
                });

        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);


//        connect(bootstrap, "juejin.im", 80);
//        connect(bootstrap, "juejin.im", 810, MAX_RETRY);
        connect(bootstrap, "localhost", 4302, MAX_RETRY);
    }

    /**
     * 自动重连(有时候首次连接的时候可能出现失败，需要自动重试）
     * 两一个场景：服务器启动后，连接断开，会触发客户端的连接断开事件（如：channelInactive）, 你需要在这里面调用 重连方法
     *
     * @param bootstrap
     * @param host
     * @param port
     */
    private static void connect(Bootstrap bootstrap, String host, int port) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
            } else {
                System.err.println("连接失败，开始重连");
                connect(bootstrap, host, port);
            }
        });
    }


    //间隔
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("连接成功!");
            } else if (retry == 0) {
                System.err.println("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order; //2,4,8,16
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……" + "间隔:" + delay);
                bootstrap.config()
                        .group()
                        .schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }

    /**
     * 重点关注channelActive和channelRead两个方法，前者是建立连接后激活，后者是收到数据后激活
     */
    private static class FirstClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("client begin to write data, at" + new Date());
            ByteBuf byteBuf = createByteBuf(ctx);
            ctx.channel().writeAndFlush(byteBuf);
        }

        private ByteBuf createByteBuf(ChannelHandlerContext ctx) {
            ByteBuf buffer = ctx.alloc().buffer();
            String msg = "你好啊，李银河";
            buffer.writeBytes(msg.getBytes(Charset.forName("utf-8")));
            return buffer;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;

            System.out.println(new Date() + ": 客户端收到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));


        }
    }
}
