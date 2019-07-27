package com.ryan.java8;

/**
 * 声明接口为函数式接口，接口满足如下条件:
 *  有且仅有一个抽象方法的接口
 */
@FunctionalInterface
public interface FuncInterface {

    // 唯一的一个抽象方法
    void handler();


    // 这个方法被Object实现了，不是抽象方法
    boolean equals(Object object);

    // 不是抽象方法
    default void run() {
        System.out.println("run");
    }
}
