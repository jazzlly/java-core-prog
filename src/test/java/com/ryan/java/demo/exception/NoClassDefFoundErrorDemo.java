package com.ryan.java.demo.exception;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * https://www.baeldung.com/java-classnotfoundexception-and-noclassdeffounderror
 *
 * NoClasDefFoundError:
 * It occurs when JVM can not find the definition of the class while trying to:
 *
 * Instantiate a class by using the new keyword
 * Load a class with a method call
 *
 *  编译时能在classpath里面找到这个类，但是运行时找不到
 *
 * 错误原因1：
 *  一般是执行类的static block或初始化static field时出现异常，导致类初始化失败
 *      这时会抛出ExceptionInInitializerError
 *  然后第二次创建实例或调用方法时，会抛出NoClassDefFoundError
 *
 *  错误原因2：
 *      能够找到这个类，但是这个类的版本不对
 *      编译时使用的是正确的类的版本，但是运行时classpath里面有多个版本
 *
 *  如maven打包时，多个包里面包括了不通版本的这个类？
 *      编译能通过，但是运行时找类的机制？
 *
 *  解决方法：ClassNotFound && NoClassDefFound
 *      应用程序依赖的jar包中有没有这个类？
 *      确定这个类的位置, 在哪个jar包里面，这个jar包是不是有多个版本？
 *      确定运行时的classpath
 *      是否使用了多个classloader?
 */
public class NoClassDefFoundErrorDemo {
    public static void main(String[] args) {
        try {
            // The following line would throw ExceptionInInitializerError
            ClassWithInitErrors calculator1 = new ClassWithInitErrors();
        } catch (Throwable t) {
            System.out.println(t);
            assertThat(t instanceof ExceptionInInitializerError).isTrue();
        }

        try {
            // The following line would cause NoClassDefFoundErrorDemo
            ClassWithInitErrors calculator2 = new ClassWithInitErrors();
        } catch (Throwable t) {
            System.out.println(t);
            assertThat(t instanceof NoClassDefFoundError).isTrue();
        }
    }

    public static class ClassWithInitErrors {
        static int undefined = 1 / 0;
    }
}

