package com.ryan.java.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class E030Examples {

    @Test
    public void swallowException_examples() {
        // 错误范例：
        try {
            // blabla ...
            Thread.sleep(1_000);
        } catch (Exception e) {
            // Ignore it
        }
        /* 两点错误：
            1. 忽略了异常
            2. 使用了通用的异常, 应该捕获特定的异常
        */

        // 正解：
        try {
            // blabla ...
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
            // e.printStackTrace();  // 输出到stderr也是不推荐的
            log.info("", e);        // 输出到日志系统中
        }
    }

    @Test
    public void throwEarly_example() {
        readFileOk(null);
    }

    void readFileKo(String filename) {
        // 最好在这里检查filename
        try {
            InputStream inputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void readFileOk(String filename) {
        // 应该尽早的检测到异常，并抛出
        Objects.requireNonNull(filename, "File name should not be null");

        try {
            InputStream inputStream = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            log.warn("", e);
        }
    }

    @Test
    public void catchLater_example() {

        /** 抓住异常后，如何处理
         * 首先考虑错误是否可恢复？
         * 对于不能恢复的错误，可以包装成RuntimeException
         *
         * 对于可以恢复的错误：
         * 1. 打印日志
         * 2. 如果不知道如何处理，可以封装成业务异常抛出到上层
         *      由更上层的业务，更适合的业务环节进行处理
         *
         *      比如说网络不可达异常，可以抛出到重试层
         *      由重试层进行重试，重试打到上限，抛出到UI层
         *      UI层最后提示用户，网络不可达
         */

        /**
         * try catch会对性能有影响，会影响jvm对代码的优化
         * 应该尽量包括少的代码，不要包括一个大的代码块
         *
         */
    }
}
