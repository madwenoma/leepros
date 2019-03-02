package limit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2018/3/20.
 */
public class BasicUsage {

    RateLimiter limiter = RateLimiter.create(1);

    public void doRequest(String threadName) {
        if (limiter.tryAcquire()) {
            System.out.println(threadName + "do request");

        } else {
            System.out.println(threadName + "please try later");
        }


    }


    public static void main(String[] args) {
        //比如5r/s，则每隔200毫秒处理一个请求，平滑了速率）。为什么是200毫秒 1秒内？
//        RateLimiter rateLimiter = RateLimiter.create(5);

        //
//        RateLimiter rateLimiter = RateLimiter.create(1000); //每秒投放1000个令牌
//        for (int i = 0; i < 100; i++) {
//            if (rateLimiter.tryAcquire()) { //tryAcquire检测有没有可用的令牌，结果马上返回
//                System.out.println("11");
//            } else {
//                System.out.println("22");
//            }
//        }

        final BasicUsage mockUsage = new BasicUsage();
        final CountDownLatch latch = new CountDownLatch(1);
        final Random random = new Random(10);
        for (int i = 0; i < 20; i++) { //模拟一秒内20个并发
            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                        int sleepTime = random.nextInt(1000); //随机sleep [0,1000)毫秒
                        System.out.println(new Date());

//                        Thread.sleep(finalI * 100);
                        mockUsage.doRequest("t-" + finalI);
                        System.out.println(new Date());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
        latch.countDown();

    }
}

