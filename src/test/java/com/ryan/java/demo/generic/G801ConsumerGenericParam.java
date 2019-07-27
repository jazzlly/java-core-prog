package com.ryan.java.demo.generic;

import org.junit.Test;

import java.util.function.Consumer;

/**
 * Consumer<T>::andThen(Consumer<? super T>)
 * 为什么使用Generic的参数类型
 */
public class G801ConsumerGenericParam {
    /*
    @FunctionalInterface
    public interface Consumer<T> {
        void accept(T t);

        // fixme: 为什么参数类型 <? super T>， 而不是<T>
        default Consumer<T> andThen(Consumer<? super T> after) {
            Objects.requireNonNull(after);
            return (T t) -> { accept(t); after.accept(t); };
        }
    }
    */

    // fixme: 假设参数是<T>, 会发生什么？
    interface MyConsumer<T> extends Consumer<T> {
        default Consumer<T> andThenTakeNotGenericParam(Consumer<T> after) {
            return (T t) -> { accept(t); after.accept(t); };
        }
    }

    @Test
    public void smoke() {
        MyConsumer<Integer> firstFunc =
                integer -> System.out.println("first consumer: " + integer);

        Consumer<Integer> secondFuncTakeIntegerParam =
                integer -> System.out.println("second1, integer param: " + integer);
        Consumer<Number> secondFuncTakeNumberParam =
                number -> System.out.println("second2, number param: " + number);
        Consumer<Object> secondFuncTakeObjectParam =
                object -> System.out.println("second3, object param:" + object);

        // fixme: 对于<? super T:Integer>类型的参数，可以接受的函数类型更多
        // 其参数类型可以为Integer, Number, Object
        firstFunc.andThen(secondFuncTakeIntegerParam);
        firstFunc.andThen(secondFuncTakeNumberParam);
        firstFunc.andThen(secondFuncTakeObjectParam);

        // fixme: 对于<T:Integer>类型的参数，接受的函数类型只有一种
        // 其参数类型只能是Integer
        firstFunc.andThenTakeNotGenericParam(secondFuncTakeIntegerParam);
        // fixme: error andThenTakeNotGenericParam (Consumer<Integer>)
        //      cannot be aplied to (Consumer<Number>) or (Consumer<Object>)
        // first.andThenTakeNotGenericParam(second2);
        // first.andThenTakeNotGenericParam(second3);
    }

    @Test
    public void more() {
        /* 两个 ? Super T
        public interface BiConsumer<T, U> {
            default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after)
            ...
        }

        public interface BiPredicate<T, U> {
            default BiPredicate<T, U> and(BiPredicate<? super T, ? super U> other) {
                Objects.requireNonNull(other);
                return (T t, U u) -> test(t, u) && other.test(t, u);
            }

            default BiPredicate<T, U> or(BiPredicate<? super T, ? super U> other) {
                Objects.requireNonNull(other);
                return (T t, U u) -> test(t, u) || other.test(t, u);
            }
        }
        */
    }

    @Test
    public void more2() {
        /*
        public interface BiFunction<T, U, R> {
            // fixme: ? extends V 在后面解释
            default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        }
         */
    }

    @Test
    public void conclude() {
        /**
         * 在A->B的函数的调用链条中，如A的返回类型为R， B接收R为参数，
         * 则B可以声明输入参数类型为<R>或者<? super R>。后者接收的函数更多。
         */
    }
}

