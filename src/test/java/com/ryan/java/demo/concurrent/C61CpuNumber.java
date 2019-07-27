package com.ryan.java.demo.concurrent;

import org.junit.Test;

public class C61CpuNumber {

    @Test
    public void smoke() {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }
}
