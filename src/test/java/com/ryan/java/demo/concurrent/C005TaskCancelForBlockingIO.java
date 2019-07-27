package com.ryan.java.demo.concurrent;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1. java.io中的同步socket i/o, 读写socket
 *      inputstream, outputstream读写方法不响应中断
 *      但是通过关闭底层socket, 可以让读写线程抛出SocketException
 *
 * 2. java.nio中的同步socket i/o。
 *      2.1 中断一个等待InterruptibleChannel线程，会导致抛出
 *          ClosedByInterruptedException异常，并关闭链路
 *          其他等待这个Channel的线程也会收到ClosedByInterruptedException
 *      2.2 关闭一个InterruptibleChanel, 导致多个阻塞在上面的线程收到
 *          AsynchronousCloseException
 *
 *      大多数标准的Channels都实现了InterruptibleChannel
 *
 *  3. Selector的异步i/o
 *      如果线程阻塞于Selector.select(), close()方法会导致其抛出
 *      ClosedSelectorException, 而提前返回
 *
 *  4. 锁
 *      最好使用lockInterruptibly方法，该方法能够响应中断
 */
public class C005TaskCancelForBlockingIO {
}

/**
 * Encapsulating nonstandard cancellation in a Thread by overriding interrupt
 */
class ReaderThread extends Thread {
    private static final int BUFSZ = 512;
    private final Socket socket;
    private final InputStream in;

    public ReaderThread(Socket socket) throws IOException {
        this.socket = socket;
        this.in = socket.getInputStream();
    }

    /**
     * Socket.read()不响应中断，而响应close()
     * 则在Thread.interrupt()方法中添加close()
     */
    public void interrupt() {
        try {
            socket.close();
        } catch (IOException ignored) {
        } finally {
            super.interrupt(); // 设置线程的中断状态
        }
    }

    public void run() {
        try {
            byte[] buf = new byte[BUFSZ];
            while (true) {
                int count = in.read(buf);
                if (count < 0)
                    break;
                else if (count > 0)
                    processBuffer(buf, count);
            }
        } catch (IOException e) { /* Allow thread to exit */
        }
    }

    public void processBuffer(byte[] buf, int count) {
    }
}

/**
 * Adding reliable cancellation to LogWriter
 *
 * 1. 通过synchronized保护内部状态变量
 *      isShutdown, reservations
 *      可以通过AtomicInteger实现？
 *
 * 2. reservations可以让所有入队的消息都消费完
 *
 * 3. 通过interrupt中断线程
 */
class LogService {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;
    @GuardedBy("this") private boolean isShutdown;
    @GuardedBy("this") private int reservations;

    public LogService(Writer writer) {
        this.queue = new LinkedBlockingQueue<String>();
        this.loggerThread = new LoggerThread();
        this.writer = new PrintWriter(writer);
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        synchronized (this) {
            isShutdown = true;
        }
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        synchronized (this) {
            if (isShutdown)
                throw new IllegalStateException(/*...*/);
            ++reservations;
        }
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        public void run() {
            try {
                while (true) {
                    try {
                        synchronized (LogService.this) {
                            if (isShutdown && reservations == 0)
                                break;
                        }
                        String msg = queue.take();
                        synchronized (LogService.this) {
                            --reservations;
                        }
                        writer.println(msg);
                    } catch (InterruptedException e) { /* retry */
                    }
                }
            } finally {
                writer.close();
            }
        }
    }
}

class LogService2 {
    private final BlockingQueue<String> queue;
    private final LoggerThread loggerThread;
    private final PrintWriter writer;

    // isShutdown仅仅被一个logger线程写，被生产者线程读
    // 通过volatile保证可见性即可
    private volatile boolean isShutdown;
    private AtomicInteger reservations;

    public LogService2(Writer writer) {
        this.queue = new LinkedBlockingQueue<String>();
        this.loggerThread = new LoggerThread();
        this.writer = new PrintWriter(writer);
    }

    public void start() {
        loggerThread.start();
    }

    public void stop() {
        loggerThread.interrupt();
    }

    public void log(String msg) throws InterruptedException {
        if (isShutdown)
            throw new IllegalStateException(/*...*/);

        reservations.incrementAndGet();
        queue.put(msg);
    }

    private class LoggerThread extends Thread {
        public void run() {
            try {
                while (true) {
                    try {
                        if (isShutdown && reservations.get() == 0)
                            break;

                        String msg = queue.take();
                        reservations.decrementAndGet();

                        writer.println(msg);
                    } catch (InterruptedException e) {
                        isShutdown = true;
                    }
                }
            } finally {  // finally很重要
                writer.close();
            }
        }
    }
}
/**
 * Encapsulating nonstandard cancellation in a task with newTaskFor
 */
abstract class SocketUsingTask <T> implements CancellableTask<T> {
    @GuardedBy("this") private Socket socket;

    protected synchronized void setSocket(Socket s) {
        socket = s;
    }

    public synchronized void cancel() {
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ignored) {
        }
    }

    public RunnableFuture<T> newTask() {
        return new FutureTask<T>(this) {
            public boolean cancel(boolean mayInterruptIfRunning) {
                try {
                    SocketUsingTask.this.cancel();
                } finally {
                    return super.cancel(mayInterruptIfRunning);
                }
            }
        };
    }
}


interface CancellableTask <T> extends Callable<T> {
    void cancel();

    RunnableFuture<T> newTask();
}


@ThreadSafe
class CancellingExecutor extends ThreadPoolExecutor {
    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public CancellingExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof CancellableTask)
            return ((CancellableTask<T>) callable).newTask();
        else
            return super.newTaskFor(callable);
    }
}
