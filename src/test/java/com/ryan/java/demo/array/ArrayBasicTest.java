package com.ryan.java.demo.array;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayBasicTest {
    @Test
    public void smoke() {
        int[] intArray = new int[10];

        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = i;
        }

        for (int i : intArray) {
            System.out.println(i);
        }
    }

    @Test
    public void arrayInit() {
        int[] array = {1, 2, 3, 4};
        array = new int[] {2, 3, 4, 5};
    }

    @Test
    public void copyValue() {
        int[] foo = {1, 2, 3, 4};
        int[] bar = Arrays.copyOf(foo, foo.length);

        assertThat(foo == bar).isFalse();
        assertThat(foo.equals(bar)).isFalse();

        assertThat(foo.length == bar.length).isTrue();
        for (int i = 0; i < foo.length; i++) {
            assertThat(foo[i] == bar[i]).isTrue();
        }

        // shallow copy
        bar[0] = 0;
        assertThat(foo[0] == bar[0]).isFalse();
    }
}
