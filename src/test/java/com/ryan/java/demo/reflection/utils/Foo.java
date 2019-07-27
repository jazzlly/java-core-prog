package com.ryan.java.demo.reflection.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class Foo<T> {
    public Class<T> getTypeParameterClass()
    {
        Class thiz = getClass();
        System.out.println("this.class.toString: " + thiz.toString());
        System.out.println("this.class.toGenericString(): " + thiz.toGenericString());

        Type type = getClass().getGenericSuperclass();
        System.out.println("this.class.getGenericSuperclass(): " + getClass().getGenericSuperclass());

        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }

    public T createGenericInstance()
            throws IllegalAccessException, InstantiationException {
        Class<T> clazz = getTypeParameterClass();
        return clazz.newInstance();
    }
}
