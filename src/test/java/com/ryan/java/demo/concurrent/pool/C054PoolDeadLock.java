package com.ryan.java.demo.concurrent.pool;

import org.junit.Test;

import java.util.concurrent.*;

public class C054PoolDeadLock {
    ExecutorService exec = Executors.newSingleThreadExecutor();

    /**
     * get问题的点：
     *  1. 线程池是单线程的
     *  2. 提交给线程池的线程之间有相互依赖的关系
     *  3. 先提交的线程A依赖于后提交的线程B，C
     *      而线程池的容量仅仅只能运行A，B和C根本没有机会运行
     *      导致A一直等待B,C
     *
     *  如果任务相互有依赖，最好使用newCacheThreadExecutor
     *
     * @throws InterruptedException
     */
    @Test
    public void smoke() throws InterruptedException, ExecutionException {
        Future<?> future = exec.submit(new RenderPageTask());
        Thread.sleep(5000);

        future.cancel(true);
        try {
            System.out.println(future.get());
        } catch (CancellationException e) {
            e.printStackTrace();
        }
    }

    class LoadFileTask implements Callable<String> {
        private final String fileName;

        public LoadFileTask(String fileName) {
            this.fileName = fileName;
        }

        public String call() throws Exception {
            // Here's where we would actually read the file
            return "";
        }
    }

    class RenderPageTask implements Callable<String> {
        public String call() throws Exception {
            try {
                System.out.println("render task begin:");
                Future<String> header, footer;
                header = exec.submit(new LoadFileTask("header.html"));
                footer = exec.submit(new LoadFileTask("footer.html"));
                String page = renderBody();
                // Will deadlock -- task waiting for result of subtask

                System.out.println("render task waiting for load file task ...");
                // Render线程依赖于header和page线程
                // 而这时，线程池里面不能有新的线程运行header和page
                return header.get() + page + footer.get();
            } catch (InterruptedException e) {
                System.out.println("render task was interrupted!");
                throw e;
            }
        }

        private String renderBody() {
            // Here's where we would actually render the page
            return "";
        }
    }
}