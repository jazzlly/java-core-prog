package com.ryan.java.demo.date;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class DateFormatTest {

    @Test
    public void smoke() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(simpleDateFormat.format(System.currentTimeMillis()));

        Date before = simpleDateFormat.parse("2017-09-20 13:23:04.0");
        Date after = simpleDateFormat.parse("2020-08-1 10:13:4.0"); // 月份只用写一位也行
        assertThat(before).isBefore(new Date());
        assertThat(after).isAfter(new Date());
    }

    /** 时间和日期的模式:
     G 年代标志符
     yyyy: 年
     MM: 月
     dd: 日
     hh: 时 在上午或下午 (1~12)
     HH: 时 在一天中 (0~23)
     mm: 分
     ss: 秒
     S: 毫秒
     E: 星期
     D: 一年中的第几天
     F: 一月中第几个星期几
     w: 一年中第几个星期
     W: 一月中第几个星期
     a: 上午 / 下午 标记符
     k: 时 在一天中 (1~24)
     K: 时 在上午或下午 (0~11)
     z: 时区
     */
    @Test
    public void patternTest() {
        // https://blog.csdn.net/qq_27093465/article/details/53034427

        StringBuilder sb = new StringBuilder();
        sb.append("yyyy年MM月dd日 HH:mm:ss")
                .append(" a")
                .append(" E")
                .append(" 一年中的第D天")
                .append(" 一月中的第F个星期(不靠谱)")
                .append(" 一年中的第w个星期")
                .append(" 一月中的第W个星期(靠谱)")
                .append(" Z")
                .append(" z")
                .append(" G");

        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(sb.toString());
        Date date = new Date();// 获取当前时间
        System.out.println("现在时间：" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
    }
}
