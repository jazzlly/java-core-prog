package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 类的静态初始化是线程安全的
 *  每个classloader对于每个类，仅仅进行一次初始化
 */
public class C091StaticInitialize {

    static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            System.out.println(Foo.getFoo());
        }
    };

    @Test
    public void smoke() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 1000; i++) {
            service.execute(runnable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(Foo.getFoo()).isEqualTo(1L);
    }
}

/**
 * 类的静态初始化是线程安全的
 *  每个classloader对于每个类，仅仅进行一次初始化
 */
class Foo {
    static long foo = 0;

    static {
        if (foo == 0) {
            foo++;
        }
    }

    public static long getFoo() {
        return foo;
    }
}
