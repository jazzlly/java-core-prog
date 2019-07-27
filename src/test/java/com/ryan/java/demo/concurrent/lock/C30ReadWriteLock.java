package com.ryan.java.demo.concurrent.lock;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁：
 *  读/读 操作可以并发进行
 *  写/写，写/读操作无法并发进行
 */
public class C30ReadWriteLock {
    private static final Queue<String> queue = new ArrayDeque();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    class ReadRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    readLock.lockInterruptibly();
                    try {
                        System.out.println(Thread.currentThread().getName() +
                                ", peak :" + queue.peek());
                        Thread.sleep(1_500);
                    } finally {
                        readLock.unlock();
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() +
                            ": lock or sleep was interrupted!");
                    break;
                }
            }
        }
    }

    class ProducerRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i <100_000; i++) {
                try {
                    Thread.sleep((new Random().nextInt(3) + 2) * 1_000);

                    writeLock.lockInterruptibly();
                    try {
                        System.out.println(Thread.currentThread().getName() +
                                ", producer: " + i);
                        queue.add(String.valueOf(i));
                    } finally {
                        writeLock.unlock();
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() +
                            ", producer interrupted!");
                    break;
                }
            }
        }
    }

    class ConsumerRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 100_000; i++) {
                try {
                    Thread.sleep((new Random().nextInt(3) + 4) * 1_000);

                    writeLock.lockInterruptibly();
                    String s = queue.poll();
                    writeLock.unlock();
                    // Thread.yield();
                    if (s == null) {
                        System.out.println(Thread.currentThread().getName() +
                                ", consumer got null");
                        continue;
                    }
                    System.out.println(Thread.currentThread().getName() +
                            ", consumer got :" + s);

                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() +
                            ", consumer interrupted!");
                    break;
                }
            }
        }
    }

    @Test
    public void smoke() throws InterruptedException {
        Thread reader1 = new Thread(new ReadRunnable(), "reader1");
        Thread reader2 = new Thread(new ReadRunnable(), "reader2");
        Thread producer = new Thread(new ProducerRunnable(), "producer");
        Thread consumer = new Thread(new ConsumerRunnable(), "consumer");

        reader1.start();
        reader2.start();
        producer.start();
        consumer.start();

        Thread.sleep(30_000);
        producer.interrupt();
        consumer.interrupt();
        reader1.interrupt();
        reader2.interrupt();

        reader1.join();
        reader2.join();
        producer.join();
        consumer.join();

    }
}

