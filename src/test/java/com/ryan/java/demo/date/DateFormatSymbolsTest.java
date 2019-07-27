package com.ryan.java.demo.date;

import org.junit.Test;

import java.text.DateFormatSymbols;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class DateFormatSymbolsTest {

    @Test
    public void somke() {
        Locale.setDefault(Locale.ENGLISH);
        DateFormatSymbols symbols = new DateFormatSymbols();

        assertThat(Arrays.toString(symbols.getShortWeekdays()))
                .isEqualTo("[, Sun, Mon, Tue, Wed, Thu, Fri, Sat]");

        assertThat(Arrays.toString(symbols.getAmPmStrings()))
                .isEqualTo("[AM, PM]");

        assertThat(Arrays.toString(symbols.getEras()))
                .isEqualTo("[BC, AD]");

        assertThat(Arrays.toString(symbols.getMonths()))
                .isEqualTo("[January, February, March, April, May, June, July, August, September, October, November, December, ]");

        assertThat(Arrays.toString(symbols.getShortMonths()))
                .isEqualTo("[Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec, ]");

        assertThat(Arrays.toString(symbols.getWeekdays()))
                .isEqualTo("[, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday]");

        for (String[] zones : symbols.getZoneStrings()) {
            System.out.println(Arrays.toString(zones));
        }

        System.out.println(symbols.getLocalPatternChars());
    }

    @Test
    public void somkeChina() {
        Locale.setDefault(Locale.CHINA);
        DateFormatSymbols symbols = new DateFormatSymbols();

        assertThat(Arrays.toString(symbols.getShortWeekdays()))
                .isEqualTo("[, 星期日, 星期一, 星期二, 星期三, 星期四, 星期五, 星期六]");

        assertThat(Arrays.toString(symbols.getAmPmStrings()))
                .isEqualTo("[上午, 下午]");

        assertThat(Arrays.toString(symbols.getEras()))
                .isEqualTo("[公元前, 公元]");

        assertThat(Arrays.toString(symbols.getMonths()))
                .isEqualTo("[一月, 二月, 三月, 四月, 五月, 六月, 七月, 八月, 九月, 十月, 十一月, 十二月, ]");

        assertThat(Arrays.toString(symbols.getShortMonths()))
                .isEqualTo("[一月, 二月, 三月, 四月, 五月, 六月, 七月, 八月, 九月, 十月, 十一月, 十二月, ]");

        assertThat(Arrays.toString(symbols.getWeekdays()))
                .isEqualTo("[, 星期日, 星期一, 星期二, 星期三, 星期四, 星期五, 星期六]");

        for (String[] zones : symbols.getZoneStrings()) {
            System.out.println(Arrays.toString(zones));
        }

        System.out.println(symbols.getLocalPatternChars());
    }
}


