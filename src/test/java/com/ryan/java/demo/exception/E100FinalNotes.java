package com.ryan.java.demo.exception;

import org.junit.Test;

public class E100FinalNotes {

    /**
     * final:
     * 1. 修饰primitive类型变量，改变量为常量
     *
     * 2. 修饰object类型对象，对象引用不能被改变
     *      可以避免意外赋值导致的错误
     *      有人推荐奖所有方法的参数，本地变量，成员变量
     *          都声明为final的
     *
     * 3. 修饰方法，该方法不能过被override
     *      声明了方法的行为是不允许被修改的
     *
     * 4. 修饰类，类不能被继承
     *      声明了类的行为是不允许被修改的
     *
     * 注意：final不等于immutable
     */

    /**
     * finally:
     * try-catch-finally, try-finally
     */

    /**
     * finalize:
     * Object的一个方法
     *
     * 目的是在对象垃圾回收之前，释放资源
     * 可以用于回收native的资源
     *
     * 不推荐使用
     * 执行的时间不能预期，垃圾回收之前
     * 执行的结果不能预期
     * 影响垃圾回收的性能
     *
     * 推荐：
     * try-with-resource, try-finally来回收资源
     */

    @Test
    public void finallyNotRun() {
        try {
            System.exit(0);
        } finally {
            // Never go here ...!
            System.out.println("Enter finally ...");
        }
    }

    /**
     * 实现immutable的类：
     * 将class声明为final
     * 将所有成员变量定义为final private， 并且不要实现setter方法
     * 构造对象时，成员变量使用深度拷贝来初始化
     * 如果需要实现getter方法，或可能返回内部状态方法
     *      使用copy-on-write原则，创建私有的copy
     */
}
