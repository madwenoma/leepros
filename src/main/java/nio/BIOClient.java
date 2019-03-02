package nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by Administrator on 2018/6/6.
 * 传统io client
 */
public class BIOClient {
    private static ExecutorService es = Executors.newCachedThreadPool();
    private static final int sleepTime = 1000 * 1000 * 1000;

    public static class EchoClient implements Runnable {

        @Override
        public void run() {
            Socket client = null;
            PrintWriter writer = null;
            BufferedReader reader = null;

            client = new Socket();
            try {
                client.connect(new InetSocketAddress("localhost", 8000));
                writer = new PrintWriter(client.getOutputStream(), true);
                writer.write("H");
                LockSupport.parkNanos(sleepTime);
                writer.write("e");
                LockSupport.parkNanos(sleepTime);
                writer.write("l");
                LockSupport.parkNanos(sleepTime);
                writer.write("l");
                LockSupport.parkNanos(sleepTime);
                writer.write("o");
                LockSupport.parkNanos(sleepTime);
                writer.write("!");
                LockSupport.parkNanos(sleepTime);
                writer.println();
                writer.flush();
                //read

                reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                System.out.println("read from server:" + reader.readLine());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null)
                        writer.close();
                    if (reader != null)
                        reader.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        EchoClient client = new EchoClient();
        for (int i = 0; i < 10; i++) {
            es.execute(client);
        }

        System.in.read();
    }




}
