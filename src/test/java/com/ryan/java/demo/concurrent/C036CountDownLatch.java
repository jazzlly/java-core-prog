package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 主线程等待所有子线程的工作都完成了，再继续执行
 */
public class C036CountDownLatch {

    public static CountDownLatch countDownLatch = new CountDownLatch(5);

    class Task implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() +
                    ": task begin ...");
            try {
                Thread.sleep((new Random().nextInt(10) + 1) * 1_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() +
                    ": task end!");

            // 上面sleep模拟子线程工作，完成工作后进行countDown
            countDownLatch.countDown();
        }
    }

    @Test
    public void smoke() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executorService.submit(new Task());
        }

        // 主线程等待所有子线程完成工作
        System.out.println("main thread: begin await ...");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        boolean terminated = false;
        do {
            try {
                terminated = executorService.awaitTermination(
                        2, TimeUnit.SECONDS);
                System.out.println(terminated);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!terminated);
    }
}
