package vavr;
//https://blog.csdn.net/revivedsun/article/details/80088080

import io.vavr.Function2;
import io.vavr.Function5;
import io.vavr.Lazy;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Predicates.isIn;
import static io.vavr.control.Try.run;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class VavrDemos {

    @Test
    public void givenBadCode_whenTryHandles_thenCorrect() {
        Try<Integer> result = Try.of(() -> 1 / 0);

        assertTrue(result.isFailure());
    }

    @Test
    public void givenBadCode_whenTryHandles_thenCorrect2() {
        Try<Integer> computation = Try.of(() -> 1 / 0);
        int errorSentinel = computation.getOrElse(-1);

        assertEquals(-1, errorSentinel);
    }

    @Test(expected = ArithmeticException.class)
    public void givenBadCode_whenTryHandles_thenCorrect3() {
        Try<Integer> result = Try.of(() -> 1 / 0);
        result.getOrElseThrow((Supplier<ArithmeticException>) ArithmeticException::new);
    }


    //Java 8中的函数式接口最多接收两个参数，Vavr对其进行了扩展，最多支持8个参数。
    @Test
    public void whenCreatesFunction_thenCorrect5() {
        Function5<String, String, String, String, String, String> concat =
                (a, b, c, d, e) -> a + b + c + d + e;
        String finalString = concat.apply(
                "Hello ", "world", "! ", "Learn ", "Vavr");

        assertEquals("Hello world! Learn Vavr", finalString);
    }

    public int sum(int a, int b) {
        return a + b;
    }

    //通过静态工厂方法FunctionN.of使用方法引用来创建一个Vavr函数
    @Test
    public void whenCreatesFunctionFromMethodRef_thenCorrect() {
        Function2<Integer, Integer, Integer> sum = Function2.of(this::sum);
        int summed = sum.apply(5, 6);

        assertEquals(11, summed);
    }


    @Test(expected = UnsupportedOperationException.class)
    public void whenImmutableCollectionThrows_thenCorrect() {
        java.util.List<String> wordList = Arrays.asList("abracadabra");
        java.util.List<String> list = Collections.unmodifiableList(wordList);
        list.add("boom");
    }


    @Test
    public void whenCreatesVavrList_thenCorrect() {
        List<Integer> intList = List.of(1, 2, 3);

        assertEquals(3, intList.length());
        assertEquals(new Integer(1), intList.get(0));
        assertEquals(new Integer(2), intList.get(1));
        assertEquals(new Integer(3), intList.get(2));
    }

    @Test
    public void givenFunction_whenEvaluatesWithLazy_thenCorrect() {
        Lazy<Double> lazy = Lazy.of(Math::random);
        assertFalse(lazy.isEvaluated());

        double val1 = lazy.get();
        assertTrue(lazy.isEvaluated());

        double val2 = lazy.get();
        assertEquals(val1, val2, 0.1);
    }


    //在Vavr中，我们通过Match方法替换switch块
    @Test
    public void whenMatchworks_thenCorrect() {
        int input = 2;
        String output = Match(input).of(
                Case($(1), "one"),
                Case($(2), "two"),
                Case($(3), "three"),
                Case($(), "?"));

        assertEquals("two", output);

        show("-h");

    }

    public void show(String args) {
        Match(args).of(
                Case($(isIn("-h", "--help")), o -> run(this::displayHelp)),
                Case($(isIn("-v", "--version")), o -> run(this::displayVersion)),
                Case($(), o -> run(() -> {
                    throw new IllegalArgumentException(args);
                }))
        );
    }

    private void displayVersion() {
    }

    private void displayHelp() {
        System.out.println("this is help:xxxx");
    }
}
