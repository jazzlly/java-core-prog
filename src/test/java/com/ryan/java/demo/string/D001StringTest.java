package com.ryan.java.demo.string;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class D001StringTest {

	@Test
	public void methodTest() {

	    // 字符串是unicode的序列
        // 字符串是不可变的
	    assertThat("hello".substring(2)).isEqualTo("llo");

	    // endIndex is excluded [0, 2)
        assertThat("hello".substring(0, 2)).isEqualTo("he");

        assertThat("hello".startsWith("he")).isTrue();
        assertThat("hello".startsWith("el", 1)).isTrue();
        assertThat("hello".endsWith("llo")).isTrue();

        // replace all?
        assertThat("hello".replace("l", "x"))
                .isEqualTo("hexxo");

        // replace all?
        assertThat("wahaha".replace("ha", "ma"))
                .isEqualTo("wamama");

        // replace all
        assertThat("hello".replaceAll("l", "x"))
                .isEqualTo("hexxo");

        assertThat("hello".indexOf("e")).isEqualTo(1);
        assertThat("hello".indexOf("l")).isEqualTo(2);
        assertThat("hello".lastIndexOf("l")).isEqualTo(3);

        assertThat("Foo".toUpperCase()).isEqualTo("FOO");
        assertThat("Foo".toLowerCase()).isEqualTo("foo");

        assertThat("   trim   ".trim()).isEqualTo("trim");
	}

    @Test
    public void equalTest() {
	    String foo = "hello";
	    String bar = "he" + "llo";
	    String wah = "h" + "e" + "l" + "l" + "o";

	    assertThat(foo.equals(bar)).isTrue();
        assertThat(foo.equals(wah)).isTrue();

        // 不一定相等
	    assertThat(foo == bar).isTrue();
        assertThat(foo == wah).isTrue();
    }

    @Test
    public void empty() {
        String empty = "";

        assertThat(empty.length()).isEqualTo(0);
        assertThat(empty.equals("")).isTrue();
    }

    @Test
    public void abc() {
        Integer foo = 0234;
        System.out.println(foo);
    }
}
