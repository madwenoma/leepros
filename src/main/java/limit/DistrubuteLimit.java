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
   lua�ļ��������£�
 --------------------------------
 local times = redis.call('incr', KEYS[1]) --����key(KEY[1])����1

 if times == 1 then
 redis.call('expire', KEYS[1], ARGV[1]) --���ó�ʱʱ��
 end


 if times > tonumber(ARGV[2]) then --������С
 return 0
 end

 return 1
 ---------------------------------------


 local key = KEYS[1] --����KEY��һ��һ����
 local limit = tonumber(ARGV[1]) --������С
 local current = tonumber(redis.call("get", key) or "0")
 if current + 1 > limit then
    return 0
 else --������+1�������ù���
    redis.call("INCRBY", key, "1")
    redis.call("expire", key, "2")
    return 1
 end







 */
public class DistrubuteLimit {

    /**
     * ���Ǹ���������ȱ���ǲ����ͷ�
     * û��guava�ĳ���
     * @return
     * @throws IOException
     */
    public Long aquire1() throws IOException {
        String luaScript = Files.toString(new File("D:\\limit.lua"), Charset.defaultCharset());
        Jedis jedis = new Jedis("localhost", 6379);
//        String key = "ip:" + System.currentTimeMillis() / 1000; //�˴�����ǰʱ���ȡ����
        String key = "ip:" + 1; //�˴�Ӳ����ʱ�䣬��֤��������ͬһ���ڷ���
        String limit = "6"; //������С
        return (Long) jedis.eval(luaScript, Lists.newArrayList(key), Lists.newArrayList("2", limit));
    }


    /**
     * ���Ǹ���������ȱ���ǲ����ͷ�
     * û��guava�ĳ���
     * @return
     * @throws IOException
     */
    public Long aquire2() throws IOException {
        String luaScript = Files.toString(new File("D:\\limit2.lua"), Charset.defaultCharset());
        Jedis jedis = new Jedis("localhost", 6379);
//        String key = "ip:" + System.currentTimeMillis() / 1000; //�˴�����ǰʱ���ȡ����
        String key = "ip:" + 1; //�˴�Ӳ����ʱ�䣬��֤��������ͬһ���ڷ���
        String limit = "6"; //������С
        return (Long) jedis.eval(luaScript, Lists.newArrayList(key), Lists.newArrayList(limit));
    }

    public static void main(String[] args) throws IOException {
        final DistrubuteLimit distrubuteLimit = new DistrubuteLimit();
        final CountDownLatch latch = new CountDownLatch(1);//�������˵�Э��
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
