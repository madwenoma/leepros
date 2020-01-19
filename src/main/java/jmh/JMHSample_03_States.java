

package jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
//它主要是方便框架来控制变量的过程逻辑，通过@State标示的类都被用作属性的容器，
// 然后框架可以通过自己的控制来配置不同级别的隔离情况。
// 被@Benchmark标注的方法可以有参数，但是参数必须是被@State注解的，就是为了要控制参数的隔离。
//

public class JMHSample_03_States {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        volatile double x = Math.PI;
    }

    @State(Scope.Thread)
    public static class ThreadState {
        volatile double x = Math.PI;
    }

    @Benchmark
    @Warmup(iterations = 3, time = 1)
    public void measureShared(BenchmarkState state) {
        state.x++;
    }

    @Benchmark
    @Warmup(iterations = 3, time = 1)
    public void measureUnshared(ThreadState state) {
        state.x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_03_States.class.getSimpleName())
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}