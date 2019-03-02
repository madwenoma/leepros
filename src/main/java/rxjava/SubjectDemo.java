package rxjava;

import org.junit.Test;
import rx.functions.Action1;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * Subject翻译为主题、科目。可以想象成杂志中的板块吧
 * subject 是 Observable 的一个扩展，同时还实现了 Observer 接口。第一眼看上去可能有点奇怪，
 * 但是在有些场合下使用 Subject 将会非常便捷。他们可以像 Observer 一样接收事件，
 * 同时还可以像 Observable 一样把接收到的事件再发射出去。
 * <p>
 * 这种特性非常适合 Rx 中的接入点，当你的事件来至于 Rx 框架之外的代码的时候，
 * 你可以把这些数据先放到 Subject 中，然后再把 Subject转换为一个 Observable，
 * 就可以在 Rx 中使用它们了。你可以把 Subject 当做 Rx 中的 事件管道。
 */

public class SubjectDemo {

    @Test
    public void testsSubject() {
        // 无论订阅的时候AsyncSubject是否Completed，均可以收到最后一个值的回调
        AsyncSubject as = AsyncSubject.create();
        as.onNext(1);
        as.onNext(2);
        as.onNext(3);
// 这里订阅收到3
        as.onCompleted();
// 结束后，这里订阅也能收到3
        as.subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer o) {
                        System.out.println("S:" + o);// 这里只会输出3
                    }
                });

// 不要这样使用Subject
        AsyncSubject.just(1, 2, 3).subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer o) {
                        // 这里会输出1， 2， 3
                        System.out.println("S:" + o);
                    }
                });
// 因为just(T)、from(T)、create(T)会把Subject转换为Obserable

    }

    @Test
    public void testPublish() {
        PublishSubject<Integer> subject = PublishSubject.create();
        subject.onNext(1);
        subject.subscribe(System.out::println);
        subject.onNext(2);
        subject.onNext(3);
        subject.onNext(4);

    }

    //一个重要的规则就是当一个事件流结束（onError 或者 onCompleted 都会导致事件流结束）
    @Test
    public void testBehivior() {
        //只保留最后一个值。 等同于限制 ReplaySubject 的个数为 1 的情况。
        // 在创建的时候可以指定一个初始值，这样可以确保党订阅者订阅的时候可以立刻收到一个值。
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(3);
        //只是打印出 Completed， 由于最后一个事件就是 Completed。
        s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.onCompleted();
        s.subscribe(
                v -> System.out.println("Late: " + v),
                e -> System.out.println("Error"),
                () -> System.out.println("Completed")
        );
    }

    @Test
    public void testRelay() {
        ReplaySubject<Integer> s = ReplaySubject.create();
        s.subscribe(v -> System.out.println("Early:" + v));
        s.onNext(0);
        s.onNext(1);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(2);//early也会收到
    }

    @Test
    public void testRelay2() {
        ReplaySubject<Integer> s = ReplaySubject.createWithSize(2);
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.subscribe(v -> System.out.println("Late: " + v));
        s.onNext(3);
    }
}
