package rxjava;
import rx.Observable;
import rx.Subscriber;


public class HelloRxjava {
    public static void main(String[] args) {
        Observable observable = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                subscriber.onNext("hello word");
//                subscriber.onCompleted();
                throw new NullPointerException();
            }
        });

        Subscriber subscriber = new Subscriber() {
            @Override
            public void onCompleted() {
                System.out.println("on complete...");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("onError...");

            }

            @Override
            public void onNext(Object o) {
                System.out.println("on next...");
                System.out.println(o);
            }
        };
        observable.subscribe(subscriber);
    }
}
