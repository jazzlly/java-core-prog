package com.ryan.alg.leetcode.simple.hash;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SumOfTwoCode {

    @Test
    public void smoke() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(3, 0);
        map.put(3, 1);

        for (Integer value : map.values()) {
            System.out.println(value);
        }
    }
}

/**
 * 提示：数组的算法就可以考虑hash
 *
 * 给定一个整数数组和一个目标值，找出数组中和为目标值的两个数。
 * 你可以假设每个输入只对应一种答案，且同样的元素不能被重复利用。
 *
 * 示例:
 * 给定 nums = [2, 7, 11, 15], target = 9
 *
 * 因为 nums[0] + nums[1] = 2 + 7 = 9
 * 所以返回 [0, 1]
 */

class Solution {
    public int[] twoSum1(int[] nums, int target) {
        int head = -1;
        int tail = 0;

        while (head < nums.length) {
            head++;
            tail = head + 1;

            if (nums[head] >= target) {
                continue;
            }

            while (tail < nums.length) {
                if (nums[head] + nums[tail] == target) {
                    return new int[]{head, tail};
                }
                tail++;
            }
        }

        throw new IllegalStateException("no answer");
    }

    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int last = target - entry.getKey();
            Integer tail = map.get(last);
            if (tail != null) {
                return new int[] {entry.getValue(), tail};
            }
        }

        throw new IllegalStateException("no answer");
    }
}