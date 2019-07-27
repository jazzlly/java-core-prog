package com.ryan.java.demo.object;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class ObjectBasicTest {

    // final 常用语primitive类型或immutable类型
    public static final int val = 123;
    public static final String name = "foo";

    public static long id = 0;
    private long myId = 0;

    public ObjectBasicTest() {
        log.debug("id: " + id);
        myId = id;
        log.debug("myId: " + myId);
        id++;
    }

    public long getMyId() {
        return myId;
    }

    public static void main(String[] args) {
        ObjectBasicTest test0 = new ObjectBasicTest();
        assertThat(test0.getMyId()).isEqualTo(0L);

        ObjectBasicTest test1 = new ObjectBasicTest();
        assertThat(test1.getMyId()).isEqualTo(1L);
    }

    /* Junit自身会创建一个对象
    @Test
    public void smoke() {
        System.out.println("begin:");
        ObjectBasicTest test0 = new ObjectBasicTest();
        System.out.println("new test0 done!");

        ObjectBasicTest test1 = new ObjectBasicTest();
        System.out.println("new test1 done!");

        System.out.println("test 0: " + test0.getMyId());
        System.out.println("test 1: " + test1.getMyId());

        assertThat(test0.getMyId()).isEqualTo(0L);
        assertThat(test1.getMyId()).isEqualTo(1L);
    }
    */
}
