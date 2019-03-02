package limit;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/3/21.
   lua文件内容如下：
 --------------------------------
 local times = redis.call('incr', KEYS[1]) --设置key(KEY[1])并加1

 if times == 1 then
 redis.call('expire', KEYS[1], ARGV[1]) --设置超时时间
 end


 if times > tonumber(ARGV[2]) then --限流大小
 return 0
 end

 return 1
 ---------------------------------------


 local key = KEYS[1] --限流KEY（一秒一个）
 local limit = tonumber(ARGV[1]) --限流大小
 local current = tonumber(redis.call("get", key) or "0")
 if current + 1 > limit then
    return 0
 else --请求数+1，并设置过期
    redis.call("INCRBY", key, "1")
    redis.call("expire", key, "2")
    return 1
 end







 */
public class DistrubuteLimit {

    /**
     * 就是个计数器，缺点是不会释放
     * 没有guava的成熟
     * @return
     * @throws IOException
     */
    public Long aquire1() throws IOException {
        String luaScript = Files.toString(new File("D:\\limit.lua"), Charset.defaultCharset());
        Jedis jedis = new Jedis("localhost", 6379);
//        String key = "ip:" + System.currentTimeMillis() / 1000; //此处将当前时间戳取秒数
        String key = "ip:" + 1; //此处硬编码时间，保证请求都是在同一秒内发起
        String limit = "6"; //限流大小
        return (Long) jedis.eval(luaScript, Lists.newArrayList(key), Lists.newArrayList("2", limit));
    }


    /**
     * 就是个计数器，缺点是不会释放
     * 没有guava的成熟
     * @return
     * @throws IOException
     */
    public Long aquire2() throws IOException {
        String luaScript = Files.toString(new File("D:\\limit2.lua"), Charset.defaultCharset());
        Jedis jedis = new Jedis("localhost", 6379);
//        String key = "ip:" + System.currentTimeMillis() / 1000; //此处将当前时间戳取秒数
        String key = "ip:" + 1; //此处硬编码时间，保证请求都是在同一秒内发起
        String limit = "6"; //限流大小
        return (Long) jedis.eval(luaScript, Lists.newArrayList(key), Lists.newArrayList(limit));
    }

    public static void main(String[] args) throws IOException {
        final DistrubuteLimit distrubuteLimit = new DistrubuteLimit();
        final CountDownLatch latch = new CountDownLatch(1);//两个工人的协作
        final Random random = new Random(10);
        for (int i = 0; i < 1; i++) {
            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                        int sleepTime = random.nextInt(1000);
                        Thread.sleep(sleepTime);
                        Long rev = distrubuteLimit.aquire2();
                        if (rev == 1) {
                            System.out.println("t:" + finalI + ":" + "-----------------");
                        } else {
                            System.out.println("t:" + finalI + ":" + "xxxxxxxxxxxxxxxxxx");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        latch.countDown();
        System.in.read();
    }
}
