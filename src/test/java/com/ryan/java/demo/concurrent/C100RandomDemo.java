package com.ryan.java.demo.concurrent;

import org.junit.Test;

public class C100RandomDemo {

    // 中等品质的随机数生成函数
    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    @Test
    public void smoke() {
        // 种子是this.hashCode ^ nanoTime()
        int seed = (this.hashCode() ^ (int) System.nanoTime());
        for (int i = 0; i < 10; i++) {
            System.out.println(seed);
            seed = xorShift(seed);
        }

    }
}
