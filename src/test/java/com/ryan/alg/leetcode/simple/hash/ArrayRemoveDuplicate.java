package com.ryan.alg.leetcode.simple.hash;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 给定一个排序数组，你需要在原地删除重复出现的元素，
 *  使得每个元素只出现一次，返回移除后数组的新长度。
 *
 * 不要使用额外的数组空间，你必须在原地修改输入数组并在使用 O(1) 额外空间的条件下完成。
 *
 * fixme: 解题思路
 *  随手带着笔和纸
 *
 *  枚举出一些测试用例，人脑一下解题过程
 *      先写出通用的用例，在找出边界
 *
 *  写出大体代码框架
 *  为一些变量命名
 *
 */
class Solution1 {
    public static int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        int last = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] == nums[last]) {
                continue;
            }
            nums[++last] = nums[i];
        }

        return last + 1;
    }
}

public class ArrayRemoveDuplicate {
    @Test
    public void emptyTest() {
        int[] input = {};
        assertThat(Solution1.removeDuplicates(input)).isEqualTo(0);
    }

    @Test
    public void simpleTest() {
        int[] ints = {0};
        assertThat(Solution1.removeDuplicates(ints)).isEqualTo(1);
        assertThat(ints).isEqualTo(new int[] {0});
    }

    @Test
    public void headerTest() {
        int[] ints = {0, 0, 0};
        assertThat(Solution1.removeDuplicates(ints)).isEqualTo(1);
        assertThat(ints).isEqualTo(new int[] {0, 0, 0});
    }
    @Test
    public void smoke() {
        int[] ints = {-1, -1, 0, 1, 2, 2, 3, 3, 3, 3, 4, 5, 6, 6, 8};
        assertThat(Solution1.removeDuplicates(ints)).isEqualTo(9);

        assertThat(Arrays.copyOf(ints, 9)).isEqualTo(
                new int[] {-1, 0, 1, 2, 3, 4, 5, 6, 8});
    }

}

