package com.ryan.java8;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.IntConsumer;
import java.util.function.IntUnaryOperator;

public class Java8Demo {
    @Test
    public void arrayStream() {
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8};
        /*
        Arrays.stream(ints).forEach(new IntConsumer() {
            @Override
            public void accept(int value) {
                System.out.println(value);
            }
        });
        */

        Arrays.stream(ints).forEach(System.out::println);

        Arrays.stream(ints).map(operand -> operand * 2).forEach(System.out::println);
    }
}
