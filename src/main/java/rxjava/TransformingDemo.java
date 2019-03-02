package rxjava;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class TransformingDemo {
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

    public static void main(String[] args) {

        //map转换string转为Object
        Observable.just("hehe").map(new Func1<String, Object>() {
            @Override
            public Object call(String s) {
                return s + "==";
            }
        }).subscribe(sb);


    }
}
