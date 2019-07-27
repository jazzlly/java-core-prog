package com.ryan.java.demo.concurrent.test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Producer-consumer test program for BoundedBuffer
 */
public class BoundedBufferProducerConsumerTest {
    protected static final ExecutorService pool = Executors.newCachedThreadPool();

    // 生产者和消费者一起启动，一起停止
    protected CyclicBarrier barrier;
    protected final BoundedBuffer<Integer> bb;

    // 生产者的数据次数
    protected final int nTrials;

    // 多少对生产者，消费者
    protected final int nPairs;

    // 数据的校验和
    protected final AtomicInteger putSum = new AtomicInteger(0);
    protected final AtomicInteger takeSum = new AtomicInteger(0);

    public static void main(String[] args) throws Exception {
        new BoundedBufferProducerConsumerTest(10, 10, 100000).test(); // sample parameters
        pool.shutdown();
    }

    public BoundedBufferProducerConsumerTest(int capacity, int npairs, int ntrials) {
        this.bb = new BoundedBuffer<>(capacity);
        this.nTrials = ntrials;
        this.nPairs = npairs;
        this.barrier = new CyclicBarrier(npairs * 2 + 1);
    }

    void test() {
        try {
            for (int i = 0; i < nPairs; i++) {
                pool.execute(new Producer());
                pool.execute(new Consumer());
            }
            barrier.await(); // wait for all threads to be ready
            barrier.await(); // wait for all threads to finish
            assertThat(putSum.get()).isEqualTo(takeSum.get());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 中等品质的随机数生成函数
    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

    class Producer implements Runnable {
        public void run() {
            try {
                // 种子是this.hashCode ^ nanoTime()
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                barrier.await();  // 等待所有生产者，消费者开始

                for (int i = nTrials; i > 0; --i) {
                    bb.put(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.getAndAdd(sum);

                barrier.await(); // 等待所有生产者，消费者结束
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; --i) {
                    sum += bb.take();
                }
                takeSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}


