package rxjava;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.internal.util.InternalObservableUtils;
import rx.subjects.BehaviorSubject;
import rx.subjects.UnicastSubject;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * from https://www.cnblogs.com/itar/p/9013697.html
 * 更多demo参考如上url
 */
public class WindowDemo {


    /**
     * 第一个参数是缓存在这个window的间隔时间，第二个参数是时间单位 ,
     * 1s内收到的所有的生产消息都会缓存到window里面，然后统一发出给订阅者。
     * 就好像一个时间轴上面，有个窗子在收集数据，1s钟之后收集好了之后，就发送出去，
     *      * 然后到了第二个窗子，这就是Hystrix滑动窗口的精髓所在。
     * @throws InterruptedException
     */
    @Test
    public void windowsDemo() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Observable inputEventStream = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                subscriber.onNext("我是生产者.........");
            }
        });
        inputEventStream.window(1000, TimeUnit.MILLISECONDS).subscribe(new Action1() {
            @Override
            public void call(Object o) {
                System.out.println(o);
                Calendar calendar = Calendar.getInstance();
                int i = calendar.get(Calendar.SECOND);
                System.out.println("我会{}就被唤醒触发..." + i);
            }
        });
        countDownLatch.await();
    }


    public static final Func2<Integer, Integer, Integer> PUBLIC_SUM = (integer, integer2) -> integer + integer2;

    public static final Func1<Observable<Integer>, Observable<Integer>> WINDOW_SUM =
            //跳过第一个数据，因为给了scan一个默认值0，这个值需要跳过，如果不设置就不需要跳过
            window -> window.scan(0, PUBLIC_SUM).skip(1);

    public static final Func1<Observable<Integer>, Observable<Integer>> INNER_BUCKET_SUM =
            integerObservable -> integerObservable.reduce(0, PUBLIC_SUM);


    /**
     * 第一个window产生了一个滑动窗口，每秒钟就会把生产者生产的消息累加起来，第二个window是积累2个对象，
     * 然后进行发送，每次跳一个数字，第二个window是建立在第一个windows累加之后的基础上的，可能有点难理解，
     * 打印出来的序列是 10 10 、 45 35 、95 60 、 145 85 、因为这里用的scan，每次累加之后都会把源数打印一遍，
     * 所以是0 10 35 60 85 。第二个window就在这个基础上进行累加 0+10 10+35 35+60 60+85，这样就完成了一个滑动窗口的监控过程
     *
     * @throws InterruptedException
     */
    @Test
    public void testWindowSlide() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create();
        behaviorSubject
                // 1秒作为一个基本块,横向移动
                .window(1000, TimeUnit.MILLISECONDS)
                //将flatMap汇总平铺成一个事件,然后累加成一个Observable<Integer>对象，比如说1s内有10个对象，被累加起来
                .flatMap(INNER_BUCKET_SUM)
                //对这个对象2个发送，步长为1 ,步长表示窗口移动的步数，如1秒一个窗口，步长为2，则每2秒触发一次
                .window(2, 1)
                //对窗口里面的进行求和,用的scan, 每次累加都会打印出来
                .flatMap(WINDOW_SUM)
                .subscribe((Integer integer) ->
                // 输出统计数据到日志
                System.out.println(Thread.currentThread().getName() + " call ...... " + integer));

            for (int i = 0; i < 1000; i++) {
            //200ms生产一个数据，
            behaviorSubject.onNext(i);
            System.out.println("i = " + i);
            Thread.sleep(200);
        }
        countDownLatch.await();
    }

    @Test
    public void timeWindowTest() throws Exception {
        Observable<Integer> source = Observable.interval(50, TimeUnit.MILLISECONDS).map(i -> RandomUtils.nextInt(2));
        source.window(1, TimeUnit.SECONDS).subscribe(window -> {
            int[] metrics = new int[2];
            window.subscribe(i -> metrics[i]++,
                    InternalObservableUtils.ERROR_NOT_IMPLEMENTED,
                    () -> System.out.println("窗口Metrics:" + JSON.toJSONString(metrics)));
        });
        TimeUnit.SECONDS.sleep(6);
    }
}
