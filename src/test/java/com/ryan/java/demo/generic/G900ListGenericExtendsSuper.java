package com.ryan.java.demo.generic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://stackoverflow.com/questions/4343202/difference-between-super-t-and-extends-t-in-java
 * https://stackoverflow.com/questions/2723397/what-is-pecs-producer-extends-consumer-super
 *
 * List<? extentds Foo> 不论放在函数体内，还是作为函数参数
 *      都不能够给向其中写入任何对象
 *      它可以作为一个Foo对象的Producer
 *      Foo一般是一个基类对象，如Number, Shape, Fruit, ...
 *
 * fixme: 看到List<? extends T> ，先确定位置，函数体 vs 参数变量， 扩展一下；
 *  List<? extends Number> numbers = new ArrayList<>();
 *  List<? extends Number> numbers1 = new ArrayList<Number>(); // Number extends Number
 *  List<? extends Number> integers = new ArrayList<Integer>(); // Integer extends Number
 *  List<? extends Number> doubles = new ArrayList<Double>(); // Double extends Number
 *
 * fixme: 看到List<? super T>，确定位置，函数体 vs 参数变量， 扩展一下：
 *  List<? super Integer> list = new ArrayList<>();
 *  List<? super Integer> list0 = new ArrayList<Integer>(); // Integer super Integer
 *  List<? super Integer> list1 = new ArrayList<Number>(); // Number super Integer
 *  List<? super Integer> list2 = new ArrayList<Object>(); // Object super Integer
 */

public class G900ListGenericExtendsSuper {

    @Test
    public void listExtendsProducerSmoke() {
        List<? extends Number> numbers = new ArrayList<>();
        List<? extends Number> numbers1 = new ArrayList<Number>(); // Number extends Number
        List<? extends Number> integers = new ArrayList<Integer>(); // Integer extends Number
        List<? extends Number> doubles = new ArrayList<Double>(); // Double extends Number

        // fixme: compile error
        // numbers.add(new Integer(123));
        // numbers1.add(new Integer(123));
        // integers.add(new Integer(123));
        // doubles.add(new Double(123.3));

        listExtendsProducer(numbers);
        listExtendsProducer(integers);
        listExtendsProducer(doubles);

        ArrayList<Float> floats = new ArrayList<>();
        floats.add(123.3f);     // we can add element to it before cast to
        // List<? extends Number>
        List<? extends Number> numbers2 = floats;
        listExtendsProducer(floats);
    }

    private void listExtendsProducer(List<? extends Number> numberProcuder) {
        for (Number number : numberProcuder) {
            assertThat(number instanceof Number).isTrue();
        }

        // fixme: 不能向producer里面写入任何东西
        // List<?extends Number> 作为函数的参数，就是一个Number的提供者
        // 限制了向其中写入任何数据
        // 只能够从中获取Number类型的数据

        // fixme: compile error
        // numberProcuder.add(new Integer(123)); // error
        // numberProcuder.add(new Double(123.0)); // error
        // numberProcuder.add(new Number(123)); // Number是abstract的
        /* fixme: 由于list可能是下面的所有类型，所以无法向其中添加任何东西
            List<? extends Number> numbers = new ArrayList<>();
            List<? extends Number> numbers1 = new ArrayList<Number>();
            List<? extends Number> integers = new ArrayList<Integer>();
            List<? extends Number> doubles = new ArrayList<Double>();
         */
    }

    @Test
    public void listSuperConsumerSmoke() {
        List<? super Integer> list = new ArrayList<>();
        List<? super Integer> list0 = new ArrayList<Integer>(); // Integer super Integer
        List<? super Integer> list1 = new ArrayList<Number>(); // Number super Integer
        List<? super Integer> list2 = new ArrayList<Object>(); // Object super Integer

        // fixme: compile error:
        // List<? super Integer> list3 = new ArrayList<Double>();

        list.add(123);
        list0.add(123);
        list1.add(234);
        list2.add(234);

        listSuperConsumer(list);
        listSuperConsumer(list0);
        listSuperConsumer(list1);
        listSuperConsumer(list2);
    }

    private void listSuperConsumer(List<? super Integer> objects) {
        for (Object object : objects) {
            assertThat(object instanceof Object).isTrue();
            // fixme: 不能保证object是Number, Integer
            // assertThat(object instanceof Number).isTrue();
            // assertThat(object instanceof Integer).isTrue();
        }
        objects.add(new Integer(123));
        // fixme: 也可以添加Integer的子类，不过integer是final的
        // objects.add(new IntegerChild(123));


        // fixme: compile error
        // objects.add(new Object());
        // objects.add(new Number(123));
        /* fixme: 由于objects可能是下面三种类型的list， 所有只能够添加Integer
            List<? super Integer> list = new ArrayList<>();
            List<? super Integer> list0 = new ArrayList<Integer>();
            List<? super Integer> list1 = new ArrayList<Number>();
            List<? super Integer> list2 = new ArrayList<Object>();
         */
    }

    public static <T> void copy(List<? extends T> src, List<? super T> dest) {
        for (int i = 0; i < src.size(); i++)
            dest.set(i, src.get(i));
    }
}

/**
 * PECS
 * Remember PECS: "Producer Extends, Consumer Super".
 *
 * "Producer Extends"
 *  - If you need a List to produce T values (you want to read Ts from the list),
 *    you need to declare it with ? extends T, e.g. List<? extends Integer>.
 *    But you cannot add to this list.
 *
 * "Consumer Super"
 *  - If you need a List to consume T values (you want to write Ts into the list),
 *  you need to declare it with ? super T, e.g. List<? super Integer>.
 *  But there are no guarantees what type of object you may read from this list.
 *
 * If you need to both read from and write to a list,
 * you need to declare it exactly with no wildcards, e.g. List<Integer>.
 */
