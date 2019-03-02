package thread;

import java.io.FileNotFoundException;

public class TestMyLock {
    private int i;

    //    private MyLock lock = new MyLock();
//    private java.util.concurrent.locks.ReentrantLock lock = new java.util.concurrent.locks.ReentrantLock();
//    private ReentrantLock lock = new ReentrantLock();
    private ReentrantLock lock = new ReentrantLock();

    public void increase() {
        lock.lock();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i++;
        lock.unlock();
    }

    public int getI() {
        return i;
    }

    public static void test(int threadNum, int loopTimes) {
        TestMyLock increment = new TestMyLock();

        Thread[] threads = new Thread[threadNum];

        for (int i = 0; i < threads.length; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < loopTimes; j++) {
                    increment.increase();
                }
            });
            threads[i] = t;
            t.start();
        }

        for (Thread t : threads) {  //main线程等待其他线程都执行完成
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println(threadNum + "个线程，循环" + loopTimes + "次结果：" + increment.getI());
    }

    public static void main(String[] args) throws FileNotFoundException {
//        InputStream in = new FileInputStream(new File("E:\\workspace-lee\\leepros\\src\\main\\java\\log4j.properties"));
//        OptionConverter.selectAndConfigure(in, null, LogManager.getLoggerRepository());
        String a = "a";
        String b = "b";
        String c = "c";
        String d = a = c;
        System.out.println(d);
        System.out.println(a);
        test(4, 1);
//        test(20, 100);
//        test(20, 10);
//        test(20, 1000);
//        test(20, 10000);
//        test(20, 100000);
//        test(20, 1000000);
    }
}
