package com.ryan.java.demo.concurrent;

import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.ThreadSafe;
import org.junit.Test;
import org.junit.runner.notification.RunListener;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.filter;

/**
 * 不要使用double check locking方式
 */
public class C090Singleton {

    @Test
    public void smoke() {
        assertThat(Singleton.getInstance())
                .isSameAs(Singleton.getInstance());
    }

    @Test
    public void multipleThreadInit() throws InterruptedException {
        final Set<Singleton> singletons =
                Collections.synchronizedSet(new HashSet<>());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Singleton singleton = Singleton.getInstance();
                singletons.add(singleton);
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 1000; i++) {
            service.execute(runnable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(singletons).hasSize(1);
        assertThat(singletons).containsExactly(Singleton.getInstance());

    }


    @Test
    public void multipleThreadInit1() throws InterruptedException {
        final Set<LazyGoodSingleton> singletons =
                Collections.synchronizedSet(new HashSet<>());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LazyGoodSingleton singleton = LazyGoodSingleton.getInstance();
                singletons.add(singleton);
            }
        };

        ExecutorService service = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 1000; i++) {
            service.execute(runnable);
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

        assertThat(singletons).hasSize(1);
        assertThat(singletons).containsExactly(LazyGoodSingleton.getInstance());

    }
}

/**
 * 线程安全的, 简单易行的
 *  instance创建的比较早
 *  Instance is created when Class is initialized
 */
@Slf4j
@ThreadSafe
class Singleton {
    // 静态初始化是线程安全的，jvm内部会加锁
    private static Singleton instance = new Singleton();

    public static Singleton getInstance() {
        return instance;
    }

    private Singleton() {
        log.info("Created!");
    }
}

/**
 * 线程安全的
 * instance在调用getInstance()时创建
 *  使用synchronized性能不是很好
 */
@Slf4j
@ThreadSafe
class LazySingleton {
    private static LazySingleton instance = null;

    // performance is NOT good
    public static synchronized LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }

    private LazySingleton() {
        log.info("Created!");
    }
}

/**
 * 线程安全的，无锁的
 *  通过私有的静态类，延迟了instance的创建
 */
@Slf4j
class LazyGoodSingleton {
    public static LazyGoodSingleton getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * 类的静态初始化是线程安全的，包括静态初始化块
     *  对于一个classloader, 静态初始化只进行一次
     */
    private static class SingletonHolder {
        private static LazyGoodSingleton instance = new LazyGoodSingleton();
    }

    private LazyGoodSingleton() {
        log.info("created");
    }
}

