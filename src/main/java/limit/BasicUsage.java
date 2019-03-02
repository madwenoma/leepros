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
        //����5r/s����ÿ��200���봦��һ������ƽ�������ʣ���Ϊʲô��200���� 1���ڣ�
//        RateLimiter rateLimiter = RateLimiter.create(5);

        //
//        RateLimiter rateLimiter = RateLimiter.create(1000); //ÿ��Ͷ��1000������
//        for (int i = 0; i < 100; i++) {
//            if (rateLimiter.tryAcquire()) { //tryAcquire�����û�п��õ����ƣ�������Ϸ���
//                System.out.println("11");
//            } else {
//                System.out.println("22");
//            }
//        }

        final BasicUsage mockUsage = new BasicUsage();
        final CountDownLatch latch = new CountDownLatch(1);
        final Random random = new Random(10);
        for (int i = 0; i < 20; i++) { //ģ��һ����20������
            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        latch.await();
                        int sleepTime = random.nextInt(1000); //���sleep [0,1000)����
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

