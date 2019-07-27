package com.ryan.java.demo.string;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by jiangrui on 2018/9/9.
 */
public class D004CharLengthTest {
    @Test
    public void lengthTest() throws Exception {

        // String是unicode的序列

        // 一个代码单元是一个unicode字符
        // 代码单元组成了代码点

        // 两个代码单元组成了一个代码点
        String specialChar = "\ud800\udc00";  // 𐀀

        // String specialChar = "\ud800"; // 这个输出一个？

        System.out.println("The special char is : " + specialChar);
        assertThat(specialChar).isEqualTo("\uD800\uDC00");

        // length()返回代码单元的长度
            System.out.println("The length of special char is : " + specialChar.length());
        assertThat(specialChar.length()).isEqualTo(2);

        // codePointCount返回代码点的数量
        System.out.println("The length of code point of  special char is : " +
                specialChar.codePointCount(0, specialChar.length()));
        assertThat(specialChar.codePointCount(0, specialChar.length())).isEqualTo(1);
    }
}
