package com.ryan.java.demo.stream;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

import static org.assertj.core.api.Assertions.assertThat;

public class StreamReduceDemo {

    @Test
    public void smoke() {
        List<Integer> integers = Arrays.asList(1,1,1,1);
        int sum = integers.stream().reduce(0, (x, y) -> x + y);
        assertThat(sum).isEqualTo(integers.size());
    }
}
