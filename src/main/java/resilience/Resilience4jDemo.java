package resilience;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.control.Try;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * https://blog.csdn.net/java_zjh/article/details/85119124
 *  ochina
 */

public class Resilience4jDemo {
    class BackendService {

        public String doSomethingWithArgs(String world) {
            //from db
            return "data from db by " + world;

        }

        public String doSomethingTimeoutOrThrowException() {
            System.out.println("before error");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            int x = 1 / 0;
            System.out.println("after error");
            return " from db";
        }

        public String doSomething() {
            return "-";
        }

        public String doSomethingThrowException() {
            int x = 1 / 0;
            return "result...";
        }
    }

    BackendService backendService = new BackendService();

    //CircuitBreaker主要是实现针对接口异常的断路统计以及断路处理
    @Test
    public void testCircuitBreaker() {
        // Create a CircuitBreaker (use default configuration)
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .build();
        CircuitBreaker circuitBreaker = CircuitBreaker.of("backendName", circuitBreakerConfig);
        String result = circuitBreaker.executeSupplier(() -> backendService.doSomethingWithArgs("world"));
        System.out.println(result);
    }

    //控制超时和异常
    @Test
    public void testTimelimiter() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(2000))
                .cancelRunningFuture(true)
                .build();
        TimeLimiter timeLimiter = TimeLimiter.of(config);

        Supplier<Future<String>> futureSupplier = () -> executorService.submit(backendService::doSomethingTimeoutOrThrowException);
        Callable<String> restrictedCall = TimeLimiter.decorateFutureSupplier(timeLimiter, futureSupplier);
        Try.of(restrictedCall::call)
                .onFailure(throwable -> System.out.println("We might have timed out or the circuit breaker has opened."));
    }


    /**
     * A Bulkhead can be used to limit the amount of parallel executions
     */
    @Test
    public void testBulkhead() {
        Bulkhead bulkhead = Bulkhead.of("test", BulkheadConfig.custom()
                .maxConcurrentCalls(2)
                .build());
        Supplier<String> decoratedSupplier = Bulkhead.decorateSupplier(bulkhead, backendService::doSomethingTimeoutOrThrowException);
        IntStream.rangeClosed(1, 2)
                .parallel()
                .forEach(i -> {
                    String result = Try.ofSupplier(decoratedSupplier).recover(throwable -> "Hello from Recovery").get();
                    System.out.println(result);
                });

    }


    @Test
    public void testRateLimiter() {
        // Create a custom RateLimiter configuration
        RateLimiterConfig config = RateLimiterConfig.custom()
                .timeoutDuration(Duration.ofMillis(100))
                .limitRefreshPeriod(Duration.ofSeconds(5))
                .limitForPeriod(2)
                .build();
        // Create a RateLimiter
        RateLimiter rateLimiter = RateLimiter.of("backendName", config);

        // Decorate your call to BackendService.doSomething()
        Supplier<String> restrictedSupplier = RateLimiter.decorateSupplier(rateLimiter, backendService::doSomething);

        IntStream.rangeClosed(1, 100).parallel().forEach(i -> {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            Try<String> aTry = Try.ofSupplier(restrictedSupplier);
            System.out.println(aTry.isSuccess());
            if (aTry.isSuccess()) {
                System.out.println(aTry.get());
            }
        });
    }


    @Test
    public void whenCreatesTuple_thenCorrect2() {
        Tuple3<String, Integer, Double> java8 = Tuple.of("Java", 8, 1.8);
        String element1 = java8._1;
        int element2 = java8._2();
        double element3 = java8._3();

        assertEquals("Java", element1);
        assertEquals(8, element2);
        assertEquals(1.8, element3, 0.1);
    }


    //fallback基本上是高可用操作的标配
    @Test
    public void testFallback() {
        // Execute the decorated supplier and recover from any exception
        String result = Try.ofSupplier(() -> backendService.doSomethingThrowException())
                .recover(throwable -> "Hello from Recovery").get();
        System.out.println(result);
    }

    @Test
    public void testCircuitBreakerAndFallback() {
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendName");
        Supplier<String> decoratedSupplier =
                CircuitBreaker.decorateSupplier(circuitBreaker, backendService::doSomethingThrowException);
        String result = Try.ofSupplier(decoratedSupplier).recover(throwable -> "Hello from Recovery").get();
        System.out.println(result);
    }


    @Test
    public void testRetry() {
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("backendName");
        // Create a Retry with at most 3 retries and a fixed time interval between retries of 500ms
        Retry retry = Retry.ofDefaults("backendName");

        // Decorate your call to BackendService.doSomething() with a CircuitBreaker
        Supplier<String> decoratedSupplier = CircuitBreaker
                .decorateSupplier(circuitBreaker, backendService::doSomething);

        // Decorate your call with automatic retry
        decoratedSupplier = Retry.decorateSupplier(retry, decoratedSupplier);

        // Execute the decorated supplier and recover from any exception
        String result = Try.ofSupplier(decoratedSupplier).recover(throwable -> "Hello from Recovery").get();
        System.out.println(result);
    }
}
