package com.ryan.java.demo.generic;

import lombok.Getter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class G805FunctionGeneric {
    /**
     * fixme: 看到<? extends T> <? super T>
     *     1. 确定位置，在参数列表上，在函数体中， T是什么，
     *     2. 就使用Object, Number, Integer, Double, ... 扩展一下
     */

    /**
     * fixme: why filter take Predicate<? super T> instead of Predicate<T>
     *     class Stream {
     *      ...
     *      Stream<T> filter(Predicate<? super T> predicate);
     *      ...
     *     }
     */
    @Test
    public void filterGenericParam() {
        Predicate<Dish> dishPredicate = dish -> dish.isVegetarian();
        Predicate<AbstractDish> abstractDishPredicate = dish -> dish.getName().length() > 8;
        Predicate<Object> objectPredicate = Objects::nonNull;

        List<String> dishes = Dish.menu.stream()
                .filter(dishPredicate)
                .filter(abstractDishPredicate)
                .filter(objectPredicate)
                .map(Dish::getName)
                .limit(3)
                .collect(Collectors.toList());

        System.out.println(dishes.toString());

        /**
         * Given Stream<T> filter(Predicate<? super T> predicate),
         *  filter can take Predicate<Dish> or
         *                  Predicate<AbstractDish> or
         *                  Predicate<Object>
         *  Since Dish is a Dish, or an AbstractDish , or an Object.
         *
         *  Predicate<? super T> take more functions than Predicate<T>.
         */
    }

    @Test
    public void smoke() {

        /**
         * public interface Function<T, R> {
         * ...
         *
         *  default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
         *      return (V v) -> apply(before.apply(v));
         *
         *      // fixme: 确定各个参数的类型。
         *      //  T,R: T是本函数的输入参数，R是本函数的返回参数。给出本函数就能够确定
         *      // fixme: V 如何确定
         *      // compose函数返回的是一个lambda, apply了一个v， 这个v的类型是V
         *      // 所以V是第二个函数的参数的类型。只有在写出了这个参数后，才能得到
         *  }
         *  ...
         *  }
         */
        // T -> Number, R -> Foo
        Function<Number, Foo> function = integer -> new Foo();
        // ? Super V -> String, ? extends T -> Integer
        // fixme: what is V? String is V, V是apply里面的类型
        // function.compose(xxx).apply("hello");
        Function<String, Integer> function0 = s -> 25;
        function.compose(function0).apply("hello");
        // Integer a = function.compose(function0);

        // ? Super V -> Bar, ? extends T -> Integer
        // fixme: what is V? Bar is V, super is Bar, BarParent
        // function.compose(function00).apply(new Bar());
        Function<Bar, Double> function00 = bar -> 234.9;
        function.compose(function00).apply(new Bar());

        // ? Super v -> BarParent, ? extends T -> Float
        // fixme: what is V? --> depends on what in the "apply" function
        Function<BarParent, Float> function000 = bar -> 234.9F;
        Function<Bar, Float> function001 = bar -> 234.9F;
        Function<BarChild, Float> function002 = bar -> 234.9F;

        // V is Bar!!! Super is Bar, BarParent, could take function000, function001
        function.compose(function000).apply(new Bar());
        function.compose(function001).apply(new Bar());
        // V is BarChild!!! Super is BarChild, Bar, BarParent
        /// could take function002, function001, function000
        function.compose(function000).apply(new BarChild());
        function.compose(function001).apply(new BarChild());
        function.compose(function002).apply(new BarChild());
    }

    /**
     * fixme: why Function::andThen take Function<? super R, ? extends V>
     *  instead of Function<R, V>
     *
     *  interface Function<T,R> {
     *      ...
     *      default <V> Function<T, V> andThen(Function<? super R, ? extends V> after)
     *      ...
     *    }
     */
    @Test
    public void typeChangeInputParamSmoke() {
        /**
         * default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
         *      return (T t) -> after.apply(apply(t));
         * }
         *
         * 	 T, R -> ? super R
         * 	 	如果我是R的父类，我就能够接收R
         * 		第一个函数返回Integer, 第二个函数的输入参数声明为 <? super Integer>
         * 		则第二个函数的输入参数声明可以Interger, Number, Object
         * 	    If second function take only <Integer>,
         * 	        the type of parameter could only be Integer
         *
         * 		函数参数的类型：
         * 			声明为父类类型的参数，可以接收所有自身类和子类的对象
         * 		函数返回值的类型：
         * 		 	声明为父类类型的返回值，可以返回所有自身类和子类的对象
         *
         * 	fixme: 为什么不声明成如下形式：
         *   default <V> Function<T, V> andThen(Function<R, V> after)
         *   	? super R 接受更多的函数，而且符合语法
         *   	? extends V 接受更多的函数，而且符合语法
         */
        Function<Integer, Integer> first = i -> {
            System.out.println(i);
            return i + 1;
        };

        // Integer super Integer
        Function<Integer, Foo> second1 = integer -> {
            System.out.println(integer);
            return new Foo();
        };

        // Number super Integer
        Function<Number, Foo> second2 = number -> {
            System.out.println(number);
            return new Foo();
        };

        // Object super Integer
        Function<Object, Foo> second3 = o -> {
            System.out.println(o);
            return new Foo();
        };

        assertThat(first.andThen(second1).apply(1) instanceof Foo).isTrue();
        assertThat(first.andThen(second2).apply(1) instanceof Foo).isTrue();
        assertThat(first.andThen(second3).apply(1) instanceof Foo).isTrue();
    }

    @Test
    public void typeChangeSmoke2() {
        /**
         * default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
         *   return (T t) -> after.apply(apply(t));
         *  }
         *
         * // fixme: 确定各个参数的类型。
         *    T,R: T是本函数的输入参数，R是本函数的返回参数。给出本函数就能够确定
         *    fixme: V 如何确定
         *    compose函数返回的是一个lambda, apply了一个v， 这个v的类型是V
         *    所以V是第二个函数的参数的类型。只有在写出了这个参数后，才能得到
         *
         * 	 T, R -> ? super R, ? extends V
         * 	 	返回所有的子孙，父类都是可以接收的
         * 		第一个函数返回Integer, 第二个函数的输入参数声明为 ? super Integer
         * 		则第二个函数的输入参数声明可以Interger, Number, Object
         *
         * 	? extends V
         * 		若V=Number, 表示返回参数可以是 Number, Double, Integer, ...
         * 	fixme: 为什么不声明成如下形式：
         * 	default <V> Function<T, V> andThen(Function<R, V> after)
         * 	   	? super R 接受更多的函数，而且符合语法
         * 		? extends V 接受更多的函数，而且符合语法
         */
        Function<Integer, Integer> first = i -> {
            System.out.println(i);
            return i + 1;
        };

        // return: Foo extends Foo
        Function<Integer, Foo> second1 = integer -> new Foo();

        // return: FooChild extends Foo
        Function<Integer, Foo> second2 = number -> new FooChild();

        // 只要Function声明中的类型和返回值类型满足约束就行
        // <? extends V>
        Function<Integer, FooChild> second22 = integer -> new FooGrandChild();

        // return: FooGrandChild extends Foo
        Function<Integer, Foo> second3 = o -> new FooGrandChild();

        assertThat(first.andThen(second1).apply(1) instanceof Foo).isTrue();
        assertThat(first.andThen(second2).apply(1) instanceof FooChild).isTrue();
        assertThat(first.andThen(second3).apply(1) instanceof FooGrandChild).isTrue();
    }
}

@Getter
class Dish extends AbstractDish {

    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    public Dish(String name, boolean vegetarian, int calories, Type type) {
        super(name);
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public enum Type { MEAT, FISH, OTHER }

    public static final List<Dish> menu =
            Arrays.asList( new Dish("pork", false, 800, Dish.Type.MEAT),
                    new Dish("beef", false, 700, Dish.Type.MEAT),
                    new Dish("chicken", false, 400, Dish.Type.MEAT),
                    new Dish("french fries", true, 530, Dish.Type.OTHER),
                    new Dish("rice", true, 350, Dish.Type.OTHER),
                    new Dish("season fruit", true, 120, Dish.Type.OTHER),
                    new Dish("pizza", true, 550, Dish.Type.OTHER),
                    new Dish("prawns", false, 400, Dish.Type.FISH),
                    new Dish("salmon", false, 450, Dish.Type.FISH));
}

@Getter
abstract class AbstractDish {
    protected final String name;

    protected AbstractDish(String name) {
        this.name = name;
    }
}

class Foo {
}
class FooChild extends Foo {
}
class FooGrandChild extends FooChild {
}

class BarParent {}
class Bar extends BarParent {}
class BarChild extends Bar {}