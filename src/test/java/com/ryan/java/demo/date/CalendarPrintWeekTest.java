package com.ryan.java.demo.date;

import org.junit.Test;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CalendarPrintWeekTest {

    @Test
    public void printCurrentWeekCalendar() {
        // Locale.setDefault(Locale.ENGLISH);
        // Locale.setDefault(Locale.FRANCE);
        // Locale.setDefault(Locale.JAPANESE);
        Locale.setDefault(Locale.ITALIAN);

        String[] header = new DateFormatSymbols().getShortWeekdays();
        for (String s : header) {
            if (s.length() == 0) {
                continue;
            }
            System.out.printf("%4s", s);
        }
        System.out.println();

        Calendar monthDay = new GregorianCalendar();
        int today = monthDay.get(Calendar.DAY_OF_MONTH);

        // 打印第一天的indent
        monthDay.set(Calendar.DAY_OF_MONTH, 1);
        final int firstDayOfWeek = monthDay.getFirstDayOfWeek(); // SUN or Mon
        int weekDay = monthDay.get(Calendar.DAY_OF_WEEK);  // 本月的第一天是星期几
        while (weekDay != firstDayOfWeek) {
            System.out.print("    "); // 4 space
            weekDay--;
        }

        final int maxMonthDays = monthDay.getMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxMonthDays; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append(" ");
            builder.append(i);
            if (monthDay.get(Calendar.DAY_OF_MONTH) == today) {
                builder.append("*");
            }

            while (builder.length() < 4) {
                builder.append(" ");
            }

            System.out.print(builder.toString());

            monthDay.add(Calendar.DAY_OF_MONTH, 1);
            if (monthDay.get(Calendar.DAY_OF_WEEK)  == firstDayOfWeek &&
                    i <= maxMonthDays) {
                System.out.println();
            }
        }
    }
}
