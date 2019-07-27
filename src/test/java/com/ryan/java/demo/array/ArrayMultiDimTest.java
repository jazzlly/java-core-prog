package com.ryan.java.demo.array;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayMultiDimTest {
    @Test
    public void smoke() {
        int[][] array = new int[3][3];

        for (int[] ints : array) {
            Arrays.fill(ints, 10);
        }
        for (int[] ints : array) {
            for (int anInt : ints) {
                assertThat(anInt).isEqualTo(10);
            }
        }
    }

    @Test
    public void smoke2() {
        int[][] array = {
                {1,2,3},
                {2,3,4},
                {3,4,5}
        };
        System.out.println(Arrays.deepToString(array));

        // 不规则数组
        int[][] array2 = {
                {1},
                {2,3},
                {3,4,5}
        };
        System.out.println(Arrays.deepToString(array2));
    }
}
