package com.ryan.java.demo.concurrent;

import org.junit.runner.notification.RunListener;

public class C014Visibility {

    static boolean ready;
    static int number;

    static Thread thread = new Thread() {
        @Override
        public void run() {
            while (true) {
                if (ready) {
                    System.out.println(number);
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static void main(String[] args) {
        thread.start();

        // 主线程的修改，对子线程可能不可见
        number = 10;
        ready = true;
    }
}

/**
 * Setter和getter的synchronized是有必要的
 */
class FooClass {
    Integer foo;

    public synchronized Integer getFoo() {
        return foo;
    }

    public synchronized void setFoo(Integer foo) {
        this.foo = foo;
    }
}
