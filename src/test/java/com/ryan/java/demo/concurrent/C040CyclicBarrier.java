package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;

/**
 * Barrier.await = CountDownLatch.countDown + await
 *
 * await等待所有的线程都到齐了，才继续向下执行
 * await的技术到0之后，会重新开始计数
 *
 * 并发单元测试是非常好的使用场景
 *  参见PutTakeTest
 */
public class C040CyclicBarrier {

    static CyclicBarrier barrier = new CyclicBarrier(5,
            () -> System.out.println("barrier trippted"));
    // static CountDownLatch latch = new CountDownLatch(5);

    class Task implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ": begin ...");

            // 等待所有线程都到齐, 然后才开始任务
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + ": after await");

            // 执行任务 ...
            try {
                Thread.sleep((new Random().nextInt(10) + 1) * 1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": task done!");

            // 任务执行完成后，等所有线程都到齐再一起退出
            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": task end!");
        }
    }

    @Test
    public void smoke() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            service.submit(new Task());
        }

        Thread.sleep(10_000);
        service.shutdown();
        boolean isTerminated = false;
        do {
            isTerminated = service.awaitTermination(2, TimeUnit.SECONDS);
            System.out.println("waiting for terminating ...");
        } while (!isTerminated);
    }
}
