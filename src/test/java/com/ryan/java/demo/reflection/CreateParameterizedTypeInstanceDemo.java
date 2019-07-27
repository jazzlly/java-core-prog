package com.ryan.java.demo.reflection;

import com.ryan.java.demo.reflection.utils.FooString;
import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

// https://stackoverflow.com/questions/75175/create-instance-of-generic-type-in-java
public class CreateParameterizedTypeInstanceDemo {
    @Test
    public void smoke() throws InstantiationException, IllegalAccessException {
        FooString fooString = new FooString();
        assertThat(fooString.getTypeParameterClass()).isEqualTo(String.class);
        assertThat(fooString.createGenericInstance() instanceof String);
    }

    /**
     * declared method：一个类申明的方法
     *
     * 包括 private, protected, package, public
     * 但是不包括继承的方法
     */
    @Test
    public void declaredMethodTest() {
        Arrays.asList(Number.class.getDeclaredMethods()).forEach(method -> {
            System.out.println(method);
        });
    }
}
