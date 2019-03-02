package hystrix;

import com.netflix.hystrix.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Administrator on 2018/4/26.
 */
public class Protect extends HystrixCommand<String> {

    private String name;

    public Protect(String name) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(5000))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ExampleGroup-pool"))  //可选,默认 使用 this.getClass().getSimpleName();
                .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(4).withMaxQueueSize(10).withQueueSizeRejectionThreshold(7)));


        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        //your caller to be protected
        return "Hello " + name + " thread:" + Thread.currentThread().getName();
    }


    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        // //每个Command对象只能调用一次,不可以重复调用,
        Protect protect = new Protect("同步调用 hystrix demo");
        String result = protect.execute();
        System.out.println(result);
//        protect.execute();//再次调用会报错，必须new一个新的对象
        protect = new Protect("异步调用 demo ");
        Future<String> asynFuture = protect.queue();
        result = asynFuture.get(100, TimeUnit.SECONDS);
        System.out.println("result=" + result);
        System.out.println("mainThread=" + Thread.currentThread().getName());
    }


}
