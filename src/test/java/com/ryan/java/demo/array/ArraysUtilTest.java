package com.ryan.java.demo.array;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ArraysUtilTest {
    @Test
    public void smoke() {
        int[] array = {0, 1, 2, 3, 4, 5, 6};

        // toString
        assertThat(Arrays.toString(array)).isEqualTo("[0, 1, 2, 3, 4, 5, 6]");

        // deepToString
        int[][] array1 = {
                {1,2,3},
                {2,3,4},
                {3,4,5}
        };
        System.out.println(Arrays.deepToString(array1));

        // copyOf
        int[] foo = Arrays.copyOf(array, 3);
        assertThat(Arrays.toString(foo)).isEqualTo("[0, 1, 2]");

        // fill
        int[] bar = new int[20];
        Arrays.fill(bar, 234);
        for (int i : bar) {
            assertThat(i == 234).isTrue();
        }

        // equal
        int[] wahaha = Arrays.copyOf(array, array.length);
        assertThat(wahaha.equals(array)).isFalse();
        assertThat(Arrays.equals(array, wahaha)).isTrue();
    }
}
