package com.ryan.java8;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

public class CompleteFutureDemo {

    @Test
    public void smoke() throws InterruptedException {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        MyRunnable myRunnable = new MyRunnable(future);
        Thread thread = new Thread(myRunnable, "my runnable");
        thread.start();

        Thread.sleep(2_000);

        // 解除future.get()的阻塞
        future.complete(20);
        thread.join();
    }

    public static Integer calc(Integer integer) {
        try {
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return integer * integer;
    }

    @Test
    public void smoke2() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> calc(50));
        System.out.println(future.get());
    }
}

@Slf4j
class MyRunnable implements Runnable {

    final private CompletableFuture<Integer> future;

    public MyRunnable(CompletableFuture<Integer> future) {
        this.future = future;
    }

    @Override
    public void run() {
        try {
            Integer result = future.get() * future.get();
            log.info("result: " + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
