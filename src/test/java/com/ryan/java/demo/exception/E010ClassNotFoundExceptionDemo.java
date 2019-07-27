package com.ryan.java.demo.exception;

import org.junit.Test;

public class E010ClassNotFoundExceptionDemo {

    // 在classpath中没有找到class。如动态加载类 Class.forName("abc")
    @Test(expected = ClassNotFoundException.class)
    public void givenNoDrivers_whenLoadDriverClass_thenClassNotFoundException()
            throws ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }
}
