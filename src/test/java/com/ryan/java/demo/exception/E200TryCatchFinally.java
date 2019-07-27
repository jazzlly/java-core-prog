package com.ryan.java.demo.exception;

import org.junit.Test;

public class E200TryCatchFinally {

    // 在catch中抛出了异常，finally也是会被执行的
    @Test
    public void testTryFinal() {
        try {
            System.out.println("try");
            throw new IllegalStateException("exception in try");
        } catch (IllegalStateException e) {
            System.out.println("catch");
            throw e;
        } finally {
            System.out.println("finally!");
        }
    }
}
