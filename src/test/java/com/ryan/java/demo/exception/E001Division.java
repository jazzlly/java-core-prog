package com.ryan.java.demo.exception;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class E001Division {

    @Test(expected = ArithmeticException.class)
    public void divideZero() throws Exception {
        int abc = 50 / 0;
    }

    @Test
    public void divideZeroDouble() throws Exception {
        Double foo = 50.0 / 0.0;
        assertThat(Double.isInfinite(foo)).isTrue();
        assertThat(foo).isEqualTo(Double.POSITIVE_INFINITY);

        assertThat(Double.isNaN(foo)).isFalse();
        // System.out.println(foo);
    }
}
