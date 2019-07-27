package com.ryan.java.demo.generic;

import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * todo: 从函数参数传递考虑？
 * search StreamBasic.java
 */
public class G802FunctionGenericParam {
    /*
    @FunctionalInterface
    public interface Function<T, R> {
        fixme: 为什么andThen方法的第二个参数类型为? extends V, 而不是V
        default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (T t) -> after.apply(apply(t));
        }
        fixme: todo
        default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
            Objects.requireNonNull(before);
            return (V v) -> apply(before.apply(v));
        }
    }
    */

    // fixme: 假设参数是<V>, 会发生什么？
    // todo: 和? extends V是一样的
    interface MyFunction<T, R> extends Function<T, R> {
        default <V> Function<T, V> andThenNotGeneric(Function<R, V> after) {
            return (T t) -> after.apply(apply(t));
        }

        default <V> Function<V, R> composeNotGeneric(Function<V, T> before) {
            return (V v) -> apply(before.apply(v));
        }
    }

    @Test
    public void smoke() {
        MyFunction<Integer, Integer> firstFunc = integer -> integer * 2;

        Function<Integer, FooParent> secondFuncRetParent =
                integer -> new FooParent();
        Function<Integer, FooParent> secondFuncRetSelf =
                integer -> new Foo();
        Function<Integer, FooParent> secondFuncRetChild =
                integer -> new FooChild();

        // fixme: 对于<? extends V:FooParent>类型的参数，可以接受的函数类型更多
        firstFunc.andThen(secondFuncRetParent).apply(1);
        firstFunc.andThen(secondFuncRetSelf).apply(1);
        firstFunc.andThen(secondFuncRetChild).apply(1);

        // ??? fixme: 对于<V:FooParent>类型的参数，接受的函数类型只有一种 ???
        firstFunc.andThenNotGeneric(secondFuncRetParent).apply(1);
        firstFunc.andThenNotGeneric(secondFuncRetSelf).apply(1);
        assertThat(firstFunc.andThenNotGeneric(secondFuncRetSelf).apply(1) instanceof Foo).isTrue();
        //assertThat(firstFunc.andThenNotGeneric(secondFuncRetSelf) instanceof FooParent).isTrue();
        // System.out.println(firstFunc.andThenNotGeneric(secondFuncRetSelf));
        firstFunc.andThenNotGeneric(secondFuncRetChild).apply(1);
    }

    @Test
    public void smoke2() {
        MyFunction<Integer, Integer> firstFunc = integer -> integer * 2;

        Function<Foo, Integer> secondFunc = foo -> 123;
        Function<FooParent, Integer> secondFuncParent = fooParent -> 123;
        Function<FooChild, Integer> secondFuncChild = child -> 123;

        firstFunc.compose(secondFunc).apply(new Foo());
        // firstFunc.compose(secondFunc).apply(new FooParent());
        firstFunc.compose(secondFunc).apply(new FooChild());

        firstFunc.compose(secondFunc).apply(new Foo());
        // firstFunc.compose(secondFuncChild).apply(new Foo());
        firstFunc.compose(secondFuncParent).apply(new Foo());

        // fixme: ok for not  -------------------------------------------------
        firstFunc.composeNotGeneric(secondFunc).apply(new Foo());
        // firstFunc.composeNotGeneric(secondFunc).apply(new FooParent());
        firstFunc.composeNotGeneric(secondFunc).apply(new FooChild());

        firstFunc.composeNotGeneric(secondFunc).apply(new Foo());
        // firstFunc.composeNotGeneric(secondFuncChild).apply(new Foo());
        firstFunc.composeNotGeneric(secondFuncParent).apply(new Foo());


    }

    @Test
    public void more() {

    }

    @Test
    public void more2() {

    }

    @Test
    public void conclude() {
        /** todo:
         * 在A->B的函数的调用链条中，如A的返回类型为R， B接收R为参数，
         * 则B可以声明输入参数类型为<R>或者<? super R>。后者接收的函数更多。
         */
    }

    class FooParent{};
    class Foo extends FooParent {};
    class FooChild extends Foo {};
}

