package com.ryan.java.demo.concurrent;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch可以使多个线程完成了各自的任务后，等待到同一个时间点
 *  如当所有依赖的资源都初始化完成后，活动才继续执行
 *  如游戏中，所有的玩家都准备完毕后，游戏才开始执行
 */
public class C35CountDownLatch {
    @Test
    public void smoke() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            service.submit(new CountDownRunnable());
        }

        CountDownRunnable.latch.await();
        System.out.println("main thread go ...");
        service.shutdown();
    }
}

class CountDownRunnable implements Runnable {

    public static CountDownLatch latch = new CountDownLatch(5);

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": do sth ...");
        try {
            Thread.sleep((new Random().nextInt(10) + 1) * 1_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        latch.countDown();
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + ": interrupted!");
        }
        System.out.println(Thread.currentThread().getName() + ": continue ...");
    }
}

/**
 * 模拟一场短跑比赛
 */
class TestHarness {
    public long timeTasks(int nThreads, final Runnable task)
            throws InterruptedException {

        // 起点，开枪裁判
        final CountDownLatch startGate = new CountDownLatch(1);
        // 终点，计时裁判
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread(() -> {
                try {
                    // 等待开枪
                    startGate.await();

                    try {
                        // 起跑
                        task.run();
                    } finally {
                        // 终点撞线
                        endGate.countDown();
                    }
                } catch (InterruptedException ignored) {
                }
            });

            // 运动员就位, 预备...
            t.start();
        }

        long start = System.nanoTime();

        startGate.countDown();  // 起点，裁判开枪

        endGate.await();        // 终点，计时裁判等待所有人结束比赛

        long end = System.nanoTime();
        return end - start;
    }
}

