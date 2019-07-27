package com.ryan.java.demo.array;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MainArgsTest {
    // 需要在idea的run->edit configurations里面设置
    public static void main(String[] args) {
        for (String arg : args) {
            System.out.println(arg);
        }
        System.out.println(System.getenv("env1"));
        System.out.println(System.getProperty("foo"));
    }
}
