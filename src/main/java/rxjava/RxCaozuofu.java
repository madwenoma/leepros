package rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

//操作符示例
public class RxCaozuofu {
    static Subscriber sb = new Subscriber<Object>() {
        @Override
        public void onCompleted() {
            System.out.println("onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("onError");
        }

        @Override
        public void onNext(Object o) {
            System.out.println(o);
        }
    };


    static Subscriber sb2 = new Subscriber<Object>() {
        @Override
        public void onCompleted() {
            System.out.println("onCompleted");
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("onError");
        }

        @Override
        public void onNext(Object o) {
            System.out.println(o);
        }
    };

    public static void main(String[] args) {

//        create();
//        just();
//        from();
//        rangeAndRepeat();
        defer();

    }


    private static void create() {
        Observable.create(subscriber -> {
            subscriber.onNext("create opt...");
        }).subscribe(sb);
    }

    //简化create
    private static void just() {
        Observable.just("your data").subscribe(sb);
    }

    //按顺序触发onNext
    private static void from() {
        Observable.from(new Integer[]{1, 2, 3, 4, 5, 6, 7}).subscribe(sb);
    }

    private static void rangeAndRepeat() {
        Observable.range(1, 4).repeat(2).subscribe(sb);
    }


    private static String str = "init1";
    private static String str2 = "init2";

    private static void defer() {
        Observable observable = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(str);
            }
        });
        Observable observable1Just = Observable.just(str2);

        str = "defer is 在订阅的时候发布";
        str2 = "defer is 在订阅的时候发布2";
        observable.subscribe(sb);
        observable1Just.subscribe(sb2);

    }

}
