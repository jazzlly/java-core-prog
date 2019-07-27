package com.ryan.java.demo.concurrent;

import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 构造函数是线程安全的吗？
 *      不是
 *      如果构造函数访问了共享的资源，则共享资源需要被保护
 *
 * X. 如果构造函数中访问静态变量的话，必须同步这个静态变量
 * X. 如果在构造函数中把this传递给其他函数，或线程，则可能出问题。需要同步this
 * X. 子类方法不继承父类synchronize的特性
 *      子类方法需要考虑同步的事情
 *      最好父类方法将同步的逻辑都设置成final
 *
 * 1. 在构造函数一开始，this就是可用的了。
 * 2. 构造函数和普通函数一样，并不是默认被synchronized 的，有可能出现同步问题。
 * 4. 访问非静态成员变量不会有同步问题
 */
public class C0013ConstructorThreadSafe {


    @Test
    public void unsafeCounter() throws InterruptedException {

        List<UnSafeCounter> simpleCounters = Collections.synchronizedList(new ArrayList<>());
        ExecutorService service = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 1000; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    simpleCounters.add(new UnSafeCounter());
                }
            });
        }

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println(simpleCounters.size());
        System.out.println(UnSafeCounter.getCount());
    }

    @Test
    public void safeCounter() throws InterruptedException {

        List<SafeCounter> counters = Collections.synchronizedList(new ArrayList<>());
        ExecutorService service = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 1000; i++) {
            service.submit(new Runnable() {
                @Override
                public void run() {
                    counters.add(new SafeCounter());
                }
            });
        }

        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println(counters.size());
        System.out.println(SafeCounter.getCounter());
    }
}

@NotThreadSafe
class UnSafeCounter {

    // 静态的共享资源
    private static int count;

    public UnSafeCounter() {
        count++;
    }

    public static int getCount() {
        return count;
    }
}

@ThreadSafe
class SafeCounter {
    private static AtomicInteger counter = new AtomicInteger(0);

    public SafeCounter() {
        counter.incrementAndGet();
    }

    public static int getCounter() {
        return counter.get();
    }
}

