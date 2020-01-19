package jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 像Spring提供的一些init和destory方法一样，JHM也提供有这样的钩子：
 *
 * @Setup 必须标示在@State注解的类内部，表示初始化操作
 * @TearDown 必须表示在@State注解的类内部，表示销毁操作
 * <p>
 * 链接：https://www.jianshu.com/p/23abfe3251ca
 */
@State(Scope.Thread)
public class JMHSample_05_StateFixtures {
    double x;

    @Setup
    public void prepare() {
        x = Math.PI;
    }

    @TearDown
    public void check() {
        assert x > Math.PI : "Nothing changed?";
    }

    @Benchmark
    public void measureRight() {
        x++;
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHSample_05_StateFixtures.class.getSimpleName())
                .forks(1)
                .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }
}

