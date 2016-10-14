package com.calendaridex.util;

import android.util.Pair;

import com.calendaridex.ApplicationLoader;
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
        String[] hours = userEvent.getAlarmTime().split(":");
        int hour = Integer.parseInt(hours[0]);
        int minute = Integer.parseInt(hours[1]);
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
        alarmCalendar.set(Calendar.MINUTE, minute);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long alarmTime = alarmCalendar.getTimeInMillis();
        Calendar currentDayCalendar = Calendar.getInstance();
        int selectedDay = alarmCalendar.get(Calendar.DAY_OF_YEAR);
        int currentDay = currentDayCalendar.get(Calendar.DAY_OF_YEAR);
        if (alarmTime < currentTime && currentDay == selectedDay) {
            alarmCalendar.add(Calendar.DAY_OF_MONTH, 1);
            Calendar customCalendar = Calendar.getInstance();
            customCalendar.setTime(userEvent.getStartDate());
            customCalendar.add(Calendar.DAY_OF_MONTH, 1);
            userEvent.setStartDate(customCalendar.getTime());
            userEvent.setEndDate(customCalendar.getTime());
            ApplicationLoader.getApplication(ApplicationLoader.getAppContext())
                    .getDaoSession()
                    .getEventDao()
                    .update(userEvent);
        }

        return alarmCalendar;
    }

    public static Date getIncrementYear(int periodYear) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.add(Calendar.YEAR, periodYear);
        return calendar.getTime();
    }
}
