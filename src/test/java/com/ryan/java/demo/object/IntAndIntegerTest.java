package com.ryan.java.demo.object;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IntAndIntegerTest {

    /**
     * 这篇文章写得比较零散，整体思路没有串起来，其实我觉得可以从这么一条线索理解这个问题。
     * 原始数据类型和 Java 泛型并不能配合使用，也就是Primitive Types 和Generic 不能混用，
     * 于是JAVA就设计了这个auto-boxing/unboxing机制，
     *      实际上就是primitive value 与 object之间的隐式转换机制，
     * 否则要是没有这个机制，开发者就必须每次手动显示转换，那多麻烦是不是？
     *
     * 但是primitive value 与 object各自有各自的优势，
     * primitive value在内存中存的是值，所以找到primitive value的内存位置，就可以获得值；
     * 不像object存的是reference，找到object的内存位置，还要根据reference找下一个内存空间，
     * 要产生更多的IO，所以计算性能比primitive value差，
     * 但是object具备generic的能力，更抽象，解决业务问题编程效率高。
     *
     * 于是JAVA设计者的初衷估计是这样的：
     *      如果开发者要做计算，就应该使用primitive value
     *      如果开发者要处理业务问题，就应该使用object，采用Generic机制；
     *
     *  反正JAVA有auto-boxing/unboxing机制，对开发者来讲也不需要注意什么。
     *  然后为了弥补object计算能力的不足，还设计了static valueOf()方法提供缓存机制，算是一个弥补。
     */
    @Test
    public void autobox() {
        // 原则上，建议避免不必要的自动装箱，拆箱操作
        Integer integer = 1; // auto boxing
        // jdk do this ..
        // Integer integer1 = Integer.valueOf(1);

        int i = integer ++;  // unboxing
        // jdk do this ...
        // int i1 = integer.intValue();
    }

    @Test
    public void integerCacheTest() {
        // integer cache [-128, 127)
        Integer integer1 = -128;
        Integer integer2 = -128;
        assertThat(integer1).isSameAs(integer2);

        Integer integer3 = 127;
        Integer integer4 = 127;
        assertThat(integer3).isSameAs(integer4);

        // 128 is out of range
        Integer integer = 128;
        Integer integer5 = 128;
        assertThat(integer).isEqualTo(integer5);
        assertThat(integer).isNotSameAs(integer5);

        /** Adjust the cache limit
         * -XX:AutoBoxCacheMax=N
         *
         */
    }

    /**
     * 1. Mark Word:标记位 4字节，类似轻量级锁标记位，偏向锁标记位等。
     *      用于存储对象自身的运行时数据，如哈希码、GC分代年龄、锁状态标志、线程持有的锁等。
     *      这部分数据长度在32位机器和64位机器虚拟机中分别为4字节和8字节；
     * 2. Class对象指针:4字节，指向对象对应class对象的内存地址。
     * 3. 对象实际数据:对象所有成员变量。
     * 4. 对齐:对齐填充字节，按照8个字节填充。
     *
     * Integer占用内存大小，4+4+4+4=16字节。
     *
     * 不错，如果是64位不用压缩指针，对象头会变大，还可能有对齐开销
     */
}
