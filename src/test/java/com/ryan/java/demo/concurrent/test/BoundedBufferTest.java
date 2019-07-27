package com.ryan.java.demo.concurrent.test;

import net.jcip.annotations.*;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * 如何进行并发测试
 */
public class BoundedBufferTest {
    @Test
    public void isEmptyWhenConstruct() {
        BoundedBuffer<String> buffer = new BoundedBuffer<>();
        assertThat(buffer.isEmpty()).isTrue();
        assertThat(buffer.isFull()).isFalse();
    }

    @Test
    public void isFullWhenFull() {
        BoundedBuffer<String> buffer = new BoundedBuffer<>(10);
        for (int i = 0; i < 10; i++) {
            try {
                buffer.put(UUID.randomUUID().toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assertThat(buffer.isEmpty()).isFalse();
        assertThat(buffer.isFull()).isTrue();
    }

    /**
     * 如何测试阻塞：新建一个线程，在线程中调用阻塞方法
     * @throws InterruptedException
     */
    @Test
    public void takeBlockWhenEmpty() throws InterruptedException {
        BoundedBuffer<String> buffer = new BoundedBuffer<>();

        Thread thread = new Thread(() -> {
            try {
                String unused = buffer.take();
                fail("Should not get here!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread.start();
        Thread.sleep(3000);
        thread.interrupt();
        thread.join();

        assertThat(thread.isAlive()).isFalse();
    }
}

/**
 * Bounded buffer using condition queues
 */
@ThreadSafe
class BoundedBuffer <V> extends BaseBoundedBuffer<V> {
    // CONDITION PREDICATE: not-full (!isFull())
    // CONDITION PREDICATE: not-empty (!isEmpty())
    public BoundedBuffer() {
        this(100);
    }

    public BoundedBuffer(int size) {
        super(size);
    }

    // BLOCKS-UNTIL: not-full
    public synchronized void put(V v) throws InterruptedException {
        while (isFull())    // 必须使用while, 考虑多个put的线程同时被唤醒
            wait();     // wait()必须在 synchronized范围内
        doPut(v);
        notifyAll();    // notifyAll()必须在 synchronized范围内
    }

    // BLOCKS-UNTIL: not-empty
    public synchronized V take() throws InterruptedException {
        while (isEmpty())   // 必须使用while, 考虑多个put的线程同时被唤醒
            wait();         // wait()必须在syncrhonized范围内
        V v = doTake();
        notifyAll();        // notifyAll()必须在synchronized范围内
        return v;
    }

    // BLOCKS-UNTIL: not-full
    // Alternate form of put() using conditional notification
    public synchronized void alternatePut(V v) throws InterruptedException {
        while (isFull())
            wait();
        boolean wasEmpty = isEmpty();
        doPut(v);

        // 如果没有get等待，就不用notify
        if (wasEmpty)
            notifyAll();
    }
}

/**
 * Bounded buffer using explicit condition variables
 */
@ThreadSafe
class ConditionBoundedBuffer <T> {
    protected final Lock lock = new ReentrantLock();
    // CONDITION PREDICATE: notFull (count < items.length)
    private final Condition notFull = lock.newCondition();
    // CONDITION PREDICATE: notEmpty (count > 0)
    private final Condition notEmpty = lock.newCondition();
    private static final int BUFFER_SIZE = 100;
    @GuardedBy("lock") private final T[] items = (T[]) new Object[BUFFER_SIZE];
    @GuardedBy("lock") private int tail, head, count;

    // BLOCKS-UNTIL: notFull
    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length)
                notFull.await();
            items[tail] = x;
            if (++tail == items.length)
                tail = 0;
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    // BLOCKS-UNTIL: notEmpty
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0)
                notEmpty.await();
            T x = items[head];
            items[head] = null;
            if (++head == items.length)
                head = 0;
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}

/**
 * Base class for bounded buffer implementations
 */
@ThreadSafe
abstract class BaseBoundedBuffer <V> {
    @GuardedBy("this") private final V[] buf;
    @GuardedBy("this") private int tail;
    @GuardedBy("this") private int head;
    @GuardedBy("this") private int count;

    protected BaseBoundedBuffer(int capacity) {
        this.buf = (V[]) new Object[capacity];
    }

    protected synchronized final void doPut(V v) {
        buf[tail] = v;
        if (++tail == buf.length)
            tail = 0;
        ++count;
    }

    protected synchronized final V doTake() {
        V v = buf[head];
        buf[head] = null;
        if (++head == buf.length)
            head = 0;
        --count;
        return v;
    }

    public synchronized final boolean isFull() {
        return count == buf.length;
    }

    public synchronized final boolean isEmpty() {
        return count == 0;
    }
}

