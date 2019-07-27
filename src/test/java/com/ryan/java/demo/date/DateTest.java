package com.ryan.java.demo.date;

import org.junit.Test;

import java.text.DateFormatSymbols;
import java.util.*;

public class DateTest {
    @Test
    public void dateSmoke() {
        Date now = new Date();
        System.out.println(now.toString());
        System.out.println(now.getTime());
    }

    @Test
    public void calendarSmoke() {
        Calendar calendar = new GregorianCalendar();
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.MONTH));
        System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
        System.out.println(calendar.get(Calendar.HOUR));
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(calendar.get(Calendar.MINUTE));
        System.out.println(calendar.get(Calendar.SECOND));
        System.out.println(calendar.get(Calendar.MILLISECOND));
        System.out.println(calendar.get(Calendar.ZONE_OFFSET));
        System.out.println(calendar.get(Calendar.DST_OFFSET));
    }

    @Test
    public void calendarSetTest() {
        Calendar calendar = new GregorianCalendar(
                1978, Calendar.NOVEMBER, 4,
                23, 32, 32);
        calendar.set(Calendar.MILLISECOND, 123);
        System.out.println(calendar.toString());
        
        calendar.add(Calendar.MONTH, 3);
        System.out.println(calendar.toString());
    }

}
