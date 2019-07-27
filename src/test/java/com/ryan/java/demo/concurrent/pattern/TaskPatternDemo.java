package com.ryan.java.demo.concurrent.pattern;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskPatternDemo {
}

/**
 * Sequential web server
 * fixme: performance is low ...
 *
 */
class SingleThreadWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            Socket connection = socket.accept();
            handleRequest(connection);
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}

/**
 * Web server that starts a new thread for each request
 * fixme:
 * 1. cpu cost for create and destroy thread is high
 * 2. memory cost for thread is high
 * 3. not stable OutOfMemoryException
 *
 *
 */
class ThreadPerTaskWebServer {
    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = new Runnable() {
                public void run() {
                    handleRequest(connection);
                }
            };
            new Thread(task).start();
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}

/**
 * TaskExecutionWebServer
 * Web server using a thread pool
 *
 */
class TaskExecutionWebServer {
    private static final int NTHREADS =
            Runtime.getRuntime().availableProcessors();
    private static final Executor exec
            = Executors.newFixedThreadPool(NTHREADS);

    public static void main(String[] args) throws IOException {
        ServerSocket socket = new ServerSocket(80);
        while (true) {
            final Socket connection = socket.accept();
            Runnable task = new Runnable() {
                public void run() {
                    handleRequest(connection);
                }
            };
            exec.execute(task);
        }
    }

    private static void handleRequest(Socket connection) {
        // request-handling logic here
    }
}


/**
 * ThreadPerTaskExecutor
 * Executor that starts a new thread for each task
 * fixme: not good, just an example
 */
class ThreadPerTaskExecutor implements Executor {
    public void execute(Runnable r) {
        new Thread(r).start();
    }
}

/**
 * WithinThreadExecutor
 * Executor that executes tasks synchronously in the calling thread
 *
 * fixme: back to single thread pattern
 */
class WithinThreadExecutor implements Executor {
    public void execute(Runnable r) {
        r.run();
    };
}

