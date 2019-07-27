package com.ryan.java.demo.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * 相对于Runnable， 能返回线程执行的结果
 *  实现call()方法
 *  封装到一个FutureTask中
 *
 *  使用Callable是一个好的习惯
 *      Callable::call()返回值
 *      Callable::call()抛出异常
 *
 *  fixme:
 *      Runnable can NOT return value
 *      Runnable can NOT thrown checked exception
 *
 */
@Slf4j
public class C003CallableAndFuture {

    @Test
    public void callable() throws ExecutionException, InterruptedException {
        DemoCallable callable = new DemoCallable();

        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(callable);
        log.info("get future :" + future.get());

        service.shutdown();
        service.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Test
    public void futureTask() throws InterruptedException, ExecutionException {
        DemoCallable demo = new DemoCallable();
        FutureTask<Integer> task = new FutureTask<>(demo);

        Thread thread = new Thread(task);
        thread.start();

        System.out.println("begin join ...");
        System.out.println(task.get());
        thread.join();
        System.out.println("after join!");
    }

    /**
     * Cancel a timeout task using future
     */
    @Test
    public void cancelTask() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future future = service.submit(new DemoCallable());

        try {
            future.get(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            System.out.println("future get timeout, cancel future ");
            future.cancel(true);
        }

        service.shutdown();
        try {
            service.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟一个批量下载图片的流程，只要是图片就绪了，就立即处理图片
     *
     * CompletionService的实现是
     *  ExecutorService + BlockingQueue(Linked，无界队列)
     *      封装了一个QueueFuture(implement FutureTask)
     *          override了done方法，当任务完成后，将自己加入到blockingQueue中
     *      在内部将Callable或Runnable+V封装成QueueFuture
     */
    @Test
    public void completionService() {
        CompletionService<Integer> completionService =
                new ExecutorCompletionService<>(Executors.newCachedThreadPool());
        final Random random = new Random();

        for (int i = 0; i < 20; i++) {
            completionService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int downloadTime = random.nextInt(10) + 1;
                    System.out.println(Thread.currentThread().getName() +
                            ": downloading resource, " + downloadTime);
                    Thread.sleep(downloadTime * 1000);
                    return downloadTime;
                }
            });
        }

        for (int i = 0; i < 20; i++) {
            Future<Integer> future = null;
            try {
                future = completionService.take();
            } catch (InterruptedException e) {
                // main thread was interrupted!
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            }

            try {
                System.out.println("main get resource and handling: " + future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                break;
            } catch (ExecutionException e) {
                // 仅仅是一次执行异常，忽略之
                e.printStackTrace();
            }
        }
    }
}

class DemoCallable implements Callable<Integer> {

    @Override
    public Integer call() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Thread 1 output ...");

            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Thread status is interrupted, quit!");
                break;
            }

            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted!");
            }
        }
        return 123;
    }

}

abstract class FutureRenderer {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    void renderPage(CharSequence source) {
        final List<ImageInfo> imageInfos = scanForImageInfo(source);
        Callable<List<ImageData>> task =
                new Callable<List<ImageData>>() {
                    public List<ImageData> call() {
                        List<ImageData> result = new ArrayList<ImageData>();
                        for (ImageInfo imageInfo : imageInfos)
                            result.add(imageInfo.downloadImage());
                        return result;
                    }
                };

        Future<List<ImageData>> future = executor.submit(task);
        renderText(source);

        try {
            List<ImageData> imageData = future.get();
            for (ImageData data : imageData)
                renderImage(data);
        } catch (InterruptedException e) {
            // Re-assert the thread's interrupted status
            Thread.currentThread().interrupt();
            // We don't need the result, so cancel the task too
            future.cancel(true);
        } catch (ExecutionException e) {
           // throw launderThrowable(e.getCause());
            // todo:
        }
    }

    interface ImageData {
    }

    interface ImageInfo {
        ImageData downloadImage();
    }

    abstract void renderText(CharSequence s);

    abstract List<ImageInfo> scanForImageInfo(CharSequence s);

    abstract void renderImage(ImageData i);
}

/**
 * Using CompletionService to render page elements as they become available
 */
abstract class Renderer {
    private final ExecutorService executor;

    Renderer(ExecutorService executor) {
        this.executor = executor;
    }

    void renderPage(CharSequence source) {
        final List<ImageInfo> info = scanForImageInfo(source);
        CompletionService<ImageData> completionService =
                new ExecutorCompletionService<ImageData>(executor);
        for (final ImageInfo imageInfo : info)
            completionService.submit(new Callable<ImageData>() {
                public ImageData call() {
                    return imageInfo.downloadImage();
                }
            });

        renderText(source);

        try {
            for (int t = 0, n = info.size(); t < n; t++) {
                Future<ImageData> f = completionService.take();
                ImageData imageData = f.get();
                renderImage(imageData);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw LaunderThrowable.launderThrowable(e.getCause());
        }
    }

    interface ImageData {
    }

    interface ImageInfo {
        ImageData downloadImage();
    }

    abstract void renderText(CharSequence s);

    abstract List<ImageInfo> scanForImageInfo(CharSequence s);

    abstract void renderImage(ImageData i);

}

