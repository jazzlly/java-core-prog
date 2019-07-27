package com.ryan.java.demo.javadoc;

import com.ryan.java.demo.javadoc.p1.Bar;

import java.io.IOException;

// https://www.oracle.com/technetwork/java/javase/documentation/index-137868.html
// http://www.oracle.com/technetwork/java/javase/documentation/index-137483.html
/**
 * <a href="https://www.tutorialspoint.com/java/java_documentation.htm">java doc ref</a>
 *
 * @see Foo see class
 * @see Foo#fooMethod()  see method
 *
 * @see Bar see class in other package, need import the class
 * @see Bar#barMethod()
 */
public interface JavaDocExampleInterface {

    /** */
    public static final String CONST_VAL_1 = "ABC";

    /** */
    public static final String CONST_VAL_2 = "XYZ";

    /**
     * {@code <a>Just for a test</a>}
     *
     * {@link Foo#fooMethod()}
     *
     * {@linkplain Bar#barMethod()} like link, use plain text
     *
     * @param arg1
     * @param arg2
     * @return
     */
    public Object foo(String arg1, int arg2) throws IOException;
}
