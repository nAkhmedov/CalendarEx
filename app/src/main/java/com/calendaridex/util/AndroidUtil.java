package com.calendaridex.util;

import android.util.Pair;

import com.calendaridex.database.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Navruz on 17.06.2016.
 */
public class AndroidUtil {

    public static String[] removeExtraValues(String[] dayNames) {
        List<String> list = new ArrayList<>();

        for(String s : dayNames) {
            if(s != null && s.length() > 0) {
                list.add(s);
            }
        }

        return  list.toArray(new String[list.size()]);
    }

    public static Pair<Date, Date> getMonthlyDateRange(Date date) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);
        startCalendar.set(Calendar.DAY_OF_MONTH,
                startCalendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(date);
        endCalendar.set(Calendar.DAY_OF_MONTH,
                endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new Pair<>(startCalendar.getTime(), endCalendar.getTime());
    }

    public static Pair<Date, Date> get2DayDateRange(Date date) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(date);
        endCalendar.add(Calendar.DAY_OF_MONTH, 1);
        return new Pair<>(startCalendar.getTime(), endCalendar.getTime());
    }

    public static boolean isMoreThanSelectedDays(Date thatDay, long givenDay) {
        Calendar thatDayCalendar = Calendar.getInstance();
        thatDayCalendar.setTime(thatDay);
        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - thatDayCalendar.getTimeInMillis(); //result in millis
        long days = diff / (24 * 60 * 60 * 1000);
        return days >= givenDay;
    }

    public static Date getCurrentDate() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        return today.getTime();
    }

    public static Pair<Date, Date> getDailyDateRange(Date date) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(date);
        endCalendar.add(Calendar.DAY_OF_MONTH, 0);
        return new Pair<>(startCalendar.getTime(), endCalendar.getTime());
    }

    public static Calendar getAlarmTime(Event userEvent) {
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTime(userEvent.getStartDate());
        alarmCalendar.set(Calendar.MILLISECOND, 0);
        alarmCalendar.set(Calendar.SECOND, 0);
        String[] hours = userEvent.getAlarmTime().split(":");
        alarmCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours[0]));
        alarmCalendar.set(Calendar.MINUTE, Integer.parseInt(hours[1]));

        return alarmCalendar;
    }
}
