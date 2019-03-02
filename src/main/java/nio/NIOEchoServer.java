package nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/6/5.
 * 这个demo有问题 跑不通
 */
public class NIOEchoServer {

    private Selector selector;
    private ExecutorService es = Executors.newCachedThreadPool();

    public static Map<Socket, Long> statMap = new HashMap<>(10240);

    public static void main(String[] args) throws IOException {
        new NIOEchoServer().startServer();
    }

    private void startServer() throws IOException {
        selector = SelectorProvider.provider().openSelector();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

//        InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 8000);
        InetSocketAddress address = new InetSocketAddress(8000);

        serverSocketChannel.socket().bind(address);

        //绑定关系：channel和selector的关系，selectionkey就是契约合同
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        for (; ; ) {
            selector.select();
            Set<SelectionKey> readKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = readKeys.iterator();
            long e;
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();//开始处理事件，先删除避免重复处理
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel clientChannel = server.accept();
                    clientChannel.configureBlocking(false);
                    SelectionKey clientKey = clientChannel.register(selector, SelectionKey.OP_READ);
                    EchoClient echoClient = new EchoClient();
                    clientKey.attach(echoClient);
                    InetAddress inetAddress = clientChannel.socket().getInetAddress();
                    System.out.println("accept connection from " + inetAddress.getHostAddress());


                } else if (key.isValid() && key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    if (!statMap.containsKey(sc.socket())) {
                        statMap.put(sc.socket(), System.currentTimeMillis());
                    }
                    System.out.println("begin read");
                    //begin read
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer bb = ByteBuffer.allocate(8192);
                    int len;

                    len = channel.read(bb);

                    if (len < 0) {
                        //close socket
                        sc.close();
                        System.out.println("read len <0");
                        return;
                    }

                    bb.flip();
                    es.execute(new HandleMsg(key, bb));


                } else if (key.isValid() && key.isWritable()) {
                    System.out.println("begin write...");
                    //begin write
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    EchoClient echoClient = (EchoClient) key.attachment();
                    LinkedList<ByteBuffer> outputQueue = echoClient.getOutputQueue();
                    ByteBuffer buffer = outputQueue.getLast();

                    int len = clientChannel.write(buffer);
                    if (len == -1) {
                        clientChannel.close();
                        System.out.println("write len = -1");
                        return;
                    }

                    if (buffer.remaining() == 0) {
                        outputQueue.removeLast();
                    }

                    if (outputQueue.size() == 0) {
                        key.interestOps(SelectionKey.OP_READ);
                    }

                    SocketChannel sc = (SocketChannel) key.channel();
                    e = System.currentTimeMillis();
                    long b = statMap.remove(sc.socket());
                    System.out.println("spend time:" + (e - b) + "ms");

                }

            }

        }

    }

    class EchoClient {
        private LinkedList<ByteBuffer> outq;

        public EchoClient() {
            outq = new LinkedList<>();
        }

        public LinkedList<ByteBuffer> getOutputQueue() {
            return this.outq;
        }

        public void enqueue(ByteBuffer bb) {
            this.outq.addFirst(bb);
        }

    }

    class HandleMsg implements Runnable {

        private SelectionKey selectionKey;
        private ByteBuffer byteBuffer;

        public HandleMsg(SelectionKey selectionKey, ByteBuffer byteBuffer) {
            this.selectionKey = selectionKey;
            this.byteBuffer = byteBuffer;
        }

        @Override
        public void run() {
            EchoClient echoClient = (EchoClient) selectionKey.attachment();
            echoClient.enqueue(byteBuffer);
//            selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            selector.wakeup();
        }
    }


}

