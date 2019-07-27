package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Thread -> threadLocals : ThreadLocalMap
 *              ThreadLocalMap<ThreadLocal, T>
 *
 * 每个线程有一个ThreadLocalMap,
 *  Map的entity的key是ThreadLocal, value是ThreadLocal对应的参数对象
 *
 *  ThreadLocalMap类似一个WeakHashMap
 *      entry的key，也就是ThreadLocal，是一个WeakReference
 *      GC时，发现对象只有弱引用，会立即回收它。
 *      （软引用是内存不足的时候才会回收）
 *
 *  使用完threadLocal需要清除它。
 *      如果这个线程在一个线程池中，不清理，线程又给其他线程使用了。
 *      那threadLocal这部分内存就泄漏了
 *
 *      threadLocal.remove()
 *      or
 *      threadLocal = null;
 *
 *  ThreadLocal的用途：
 *      保存请求上下文， servletRequest
 *      保存事务上下文
 *
 *
 */
public class C72ThreadLocal {
    private static ThreadLocal<SimpleDateFormat> threadLocal =
            new ThreadLocal<>();

    private static ThreadLocal<Random> randomThreadLocal =
            ThreadLocal.withInitial(() -> new Random(System.currentTimeMillis()));

    public static class ParseDate implements Runnable {
        private final int i;

        public ParseDate(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            try {
                if (threadLocal.get() == null) {
                    threadLocal.set(new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss"));
                }
                Date date = threadLocal.get().parse(
                        "2015-8-23 23:10:" + (i%60));
                System.out.println(date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // 使用完threadLocal需要清除它。
            // 如果这个线程在一个线程池中，不清理，线程又给其他线程使用了。
            // 那threadLocal这部分内存就泄漏了
            threadLocal.remove();
        }
    }

    @Test
    public void smoke() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 2000; i++) {
            service.submit(new ParseDate(i));
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
    }
}
