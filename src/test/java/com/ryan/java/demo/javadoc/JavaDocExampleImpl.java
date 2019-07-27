package com.ryan.java.demo.javadoc;

import java.io.IOException;
import java.util.Collections;

/**
 * {@inheritDoc}
 */
public class JavaDocExampleImpl implements JavaDocExampleInterface {
    /**
     * {@value JavaDocExampleInterface#CONST_VAL_1}
     */
    public String filed;

    /**
     * Blabla ...
     *
     * @param arg1
     * @param arg2
     * @return
     * @throws IOException
     *
     * @deprecated blabla ...
     */
    public Object foo(String arg1, int arg2) throws IOException {
        throw new IOException("abc");
    }

    public static void main(String[] args) {
        // 好的例子
        Collections collections;
    }
}
