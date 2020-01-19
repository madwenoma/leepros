package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author 闪电侠
 */
public class NettyServer {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        serverBootstrap
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    protected void initChannel(NioSocketChannel ch) {
//                        ch.pipeline().addLast(new StringDecoder());
//                        ch.pipeline().addLast(new SimpleChannelInboundHandler<String>() {
//                            @Override
//                            protected void channelRead0(ChannelHandlerContext ctx, String msg) {
//                                System.out.println(msg);
//                            }
//                        });

                        ch.pipeline().addLast(new FirstServerHandler());
                    }
                });
        serverBootstrap.attr(AttributeKey.newInstance("serverName"), "demo-server");//channel.attr()可以取到，但一般用不到
        //childOption设置给每条连接的TCP底层设置
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);//表示是否开启TCP底层心跳机制，true为开启
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);//nagle算法是否开启；实时性高，有数据就发送，设置为true，否则false
        //设置server的配置，请求队列长度，如果频繁建立连接，可以加大长度
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bind(serverBootstrap, 4300);
    }

    public static void bind(ServerBootstrap bootstrap, int port) {
        bootstrap.bind(port).addListener(future -> {//通过监听器，监测绑定结果，失败重试下一个端口
            if (future.isSuccess()) {
                System.out.println("bind success");
            } else {
                int nextPort = port + 1;
                System.out.println("bind failed, try next port:" + nextPort);
                bind(bootstrap, nextPort);
            }
        });
    }

    private static class FirstServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;

            System.out.println(new Date() + ": 服务端读到数据 -> " + byteBuf.toString(Charset.forName("utf-8")));

            ByteBuf outBuf = createByteBuf(ctx);
            ctx.channel().writeAndFlush(outBuf);
        }

        private ByteBuf createByteBuf(ChannelHandlerContext ctx) {
            ByteBuf buffer = ctx.alloc().buffer();
            String msg = "你好啊，小波";
            buffer.writeBytes(msg.getBytes(Charset.forName("utf-8")));
            return buffer;
        }
    }
}