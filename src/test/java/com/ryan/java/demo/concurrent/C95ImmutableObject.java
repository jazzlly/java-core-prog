package com.ryan.java.demo.concurrent;

/**
 * 1. 类是final的
 * 2. 所有成员是final, private的
 * 3，没有setter
 * 4. all argument constructor
 * 5. getter返回的都是immutable对象，或是进行了deepcopy
 *
 *  被正确的创建了，在创建期间没有发生this逃逸
 *
 * Immutable class examples:
 *  String, Boolean, Byte, Integer, ...., Long
 */
public class C95ImmutableObject {
}

/**
 * 好的实践：
 *  将一组相关联的变量组合在一起，形成一个immutable对象
 *      不可变容器
 *  使用volatile修饰这个immutable对象
 *
 *  对于final类型的变量，构造函数也是线程安全的
 *
 */

final class ImmutableObject {

    // Integer is also an immutable variable
    private final Integer foo;

    public ImmutableObject(Integer foo) {
        this.foo = foo;
    }

    public Integer getFoo() {
        return foo;
    }
}

