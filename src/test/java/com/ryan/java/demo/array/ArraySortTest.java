package com.ryan.java.demo.array;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ArraySortTest {
    @Test
    public void smoke() {
        int[] array = new int[30];
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(100);
        }

        Arrays.sort(array);
        int min = Integer.MIN_VALUE;
        for (int i : array) {
            assertThat(i >= min).isTrue();
        }
    }
}
