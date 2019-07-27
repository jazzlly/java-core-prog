package com.ryan.java.demo.concurrent;

import lombok.extern.slf4j.Slf4j;
import net.jcip.annotations.GuardedBy;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * FutureTask implement RunnableFuture
 *      -|> Runnable
 *      -|> Future
 *      contain a Callable
 *
 *  FutureTask == Runnable + Future and Callable
 *
 *  implemented by LockSupport.park()/unpark()
 */
public class C096FutureTask {
    @Test
    public void smoke() throws InterruptedException {
        FutureTask<String> futureTask = new FutureTask<>(new FooCallable());

        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(futureTask);

        try {
            System.out.println(futureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);

    }
}

@Slf4j
class FooCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            builder.append(i + ":" + UUID.randomUUID().toString()).append(",");
            log.info("building string ...");
            Thread.sleep(1000);
        }
        return builder.toString();
    }
}

class Preloader {
    // 长时间的数据库操作
    ProductInfo loadProductInfo() throws DataLoadException {
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return new ProductInfo() {
        };
    }

    private final FutureTask<ProductInfo> future =
            new FutureTask<>(() -> loadProductInfo());
    private final Thread thread = new Thread(future);

    public void start() { thread.start(); }

    public ProductInfo get()
            throws DataLoadException, InterruptedException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw (DataLoadException) cause;
        }
    }

    interface ProductInfo {
    }
}

class DataLoadException extends Exception { }


/**
 * 并发性能太差
 *
 * @param <A>
 * @param <V>
 */
class Memoizer1 <A, V> implements Computable<A, V> {
    @GuardedBy("this") private final Map<A, V> cache = new HashMap<A, V>();
    private final Computable<A, V> c;

    public Memoizer1(Computable<A, V> c) {
        this.c = c;
    }

    /**
     * 如果有一个线程在计算中，所有线程都要等他
     *
     * @param arg
     * @return
     * @throws InterruptedException
     */
    public synchronized V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}

class Memoizer2 <A, V> implements Computable<A, V> {
    private final Map<A, V> cache = new ConcurrentHashMap<A, V>();
    private final Computable<A, V> c;

    public Memoizer2(Computable<A, V> c) {
        this.c = c;
    }

    public V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if (result == null) {
            result = c.compute(arg); // 对于两个相同的arg, 可能会同时进入计算, 小缺点
            cache.put(arg, result);
        }
        return result;
    }
}

class Memoizer3 <A, V> implements Computable<A, V> {
    private final Map<A, Future<V>> cache
            = new ConcurrentHashMap<A, Future<V>>();
    private final Computable<A, V> c;

    public Memoizer3(Computable<A, V> c) {
        this.c = c;
    }

    public V compute(final A arg) throws InterruptedException {
        Future<V> f = cache.get(arg);
        if (f == null) {
            Callable<V> eval = new Callable<V>() {
                public V call() throws InterruptedException {
                    return c.compute(arg);
                }
            };
            FutureTask<V> ft = new FutureTask<V>(eval);
            f = ft;
            cache.put(arg, ft); // 两个线程，arg相同，也能同时来到这儿
                                // 后面又执行了两次
            ft.run(); // call to c.compute happens here
        }
        try {
            return f.get();
        } catch (ExecutionException e) {
            // throw LaunderThrowable.launderThrowable(e.getCause());
            throw new RuntimeException(e);
        }
    }
}

/**
 * 关键点：
 *  1， 使用了ConcurrentHashMap作为容器
 *  2. 使用了FutureTask作为了Map的value
 *  3. 使用了原子方法putIfAbsent, concurrentMap的方法
 *
 * @param <A>
 * @param <V>
 */
class Memoizer <A, V> implements Computable<A, V> {
    private final ConcurrentMap<A, Future<V>> cache
            = new ConcurrentHashMap<A, Future<V>>();
    private final Computable<A, V> c;

    public Memoizer(Computable<A, V> c) {
        this.c = c;
    }

    public V compute(final A arg) throws InterruptedException {
        while (true) {
            Future<V> f = cache.get(arg);
            if (f == null) {
                Callable<V> eval = new Callable<V>() {
                    public V call() throws InterruptedException {
                        return c.compute(arg);
                    }
                };
                FutureTask<V> ft = new FutureTask<V>(eval);
                f = cache.putIfAbsent(arg, ft);  // 两个线程, arg相同，同时来到这儿
                                                 // 只有一个线程的返回值是空
                if (f == null) {
                    f = ft;      // 第一个线程进来了
                    ft.run();    // 第一个线程执行具体的运算
                } else {
                    // 第二个线程拿到了第一个线程创建的futureTask
                    // 不是第二个线程自己创建的futureTask
                }
            }
            try {
                return f.get(); // 第一个线程直接获取到值
                                // 第二个线程在这里等待第一个线程的futureTask执行完毕
            } catch (CancellationException e) {
                cache.remove(arg, f);
            } catch (ExecutionException e) {
                throw LaunderThrowable.launderThrowable(e.getCause());
            }
        }
    }
}

interface Computable <A, V> {
    V compute(A arg) throws InterruptedException;
}

class ExpensiveFunction
        implements Computable<String, BigInteger> {
    public BigInteger compute(String arg) {
        // after deep thought...

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new BigInteger(arg);
    }
}


class LaunderThrowable {
    /**
     * Coerce an unchecked Throwable to a RuntimeException
     * <p/>
     * If the Throwable is an Error, throw it; if it is a
     * RuntimeException return it, otherwise throw IllegalStateException
     */
    public static RuntimeException launderThrowable(Throwable t) {
        if (t instanceof RuntimeException)
            return (RuntimeException) t;
        else if (t instanceof Error)
            throw (Error) t;
        else
            throw new IllegalStateException("Not unchecked", t);
    }
}