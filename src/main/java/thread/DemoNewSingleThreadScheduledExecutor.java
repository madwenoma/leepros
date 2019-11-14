package thread;

import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Executor是通过将任务放在队列中，生成的futureTask。然后将生成的任务在队列中排序，将时间最近的需要出发的任务做检查。
 * 如果时间不到，就阻塞线程到下次出发时间。注意：newSingleThreadScheduledExecutor只会有一个线程，不管你提交多少任务，
 * 这些任务会顺序执行，如果发生异常会取消下面的任务，线程池也不会关闭，注意捕捉异常
 *
 * 普通老百姓和x牛人都会耽误飞机啊
 * scheduleAtFixedRate 上一个任务和下一个任务之间的时间间隔
 * scheduleWithFixedDealy 即使来晚了，也要再等会
 *
 */
public class DemoNewSingleThreadScheduledExecutor {

    public static void main(String[] args) throws InterruptedException {

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 1秒打印一次 当前线程名
        service.scheduleAtFixedRate(() -> System.out.println(Thread.currentThread().getName()), 1,
                1, TimeUnit.SECONDS);
        // 主线程等待10秒
        TimeUnit.SECONDS.sleep(10);
        System.out.println("main thread exe");
    }

    @Test
    public void test2() throws InterruptedException {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SubThread");
            // 设置线程为守护线程，主线程退出，子线程也随之退出
            t.setDaemon(true);
            return t;
        });
        // 1秒打印一次 当前线程名
        service.scheduleAtFixedRate(() -> System.out.println(Thread.currentThread().getName()), 1, 1, TimeUnit.SECONDS);
        // 主线程等待10秒
        TimeUnit.SECONDS.sleep(10);
        System.out.println("猪");
    }


    @Test
    public void test3() throws InterruptedException, IOException {

        ScheduledExecutorService actionService = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable1 = () -> {
            try {
                Thread.sleep(4000);
                System.out.println("11111111111111");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Runnable runnable2 = () -> {
            try {
                Thread.sleep(4000);
                System.out.println("222");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        actionService.scheduleWithFixedDelay(runnable1, 0, 1, TimeUnit.SECONDS);
        actionService.scheduleWithFixedDelay(runnable2, 0, 2, TimeUnit.SECONDS);
        System.in.read();
//        actionService = Executors.newSingleThreadScheduledExecutor();
//        actionService.scheduleWithFixedDelay(() -> {
//            try {
//                Thread.currentThread().setName("robotActionService");
//                Integer robotId = robotQueue.poll();
//                if (robotId == null) { // 关闭线程池 actionService.shutdown(); } else { int aiLv = robots.get(robotId); if (actionQueueMap.containsKey(aiLv)) { ActionQueue actionQueue = actionQueueMap.get(aiLv); actionQueue.doAction(robotId); } } } catch (Exception e) { // 捕捉异常 LOG.error("",e); } }, 1, 1, TimeUnit.SECONDS);
//
//                }
//            } catch () {}
//        });
    }


    static class ScheduleExecutorServiceTest {

        public static void main(String[] args) {
            ScheduleExecutorServiceTest test = new ScheduleExecutorServiceTest();
            test.testAtFixedRate();
        }

        private ScheduledExecutorService executor;

        public ScheduleExecutorServiceTest() {
            executor = Executors.newScheduledThreadPool(4);
        }

        public void testAtFixedRate() {
            executor.scheduleAtFixedRate(new Runnable() {

                public void run() {
                    System.out.println("====");
                    try {
                        Thread.sleep(10000);
                        System.out.println("exe over..");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000, 3000, TimeUnit.MILLISECONDS);
        }

        public void testWithFixedDelay() {
            executor.scheduleWithFixedDelay(new Runnable() {

                public void run() {
                    System.out.println("====");
                    try {
                        int i = 1 / 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				/*
				try {
					Thread.sleep(10000);
					System.out.println("执行完毕");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				*/
                }
            }, 1000, 3000, TimeUnit.MILLISECONDS);
        }

    }
}