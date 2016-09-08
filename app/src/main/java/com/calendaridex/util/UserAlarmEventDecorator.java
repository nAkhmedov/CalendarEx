package com.calendaridex.util;

import android.support.v4.content.ContextCompat;

import com.calendaridex.R;
import com.calendaridex.activity.MainActivity;
import com.calendaridex.database.Event;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

/**
 * Created by Navruz on 08.08.2016.
 */
public class UserAlarmEventDecorator implements DayViewDecorator {

    private int color;
//    private HashSet<CalendarDay> eventDates;
    private List<Event> userEvents;

    public UserAlarmEventDecorator(MainActivity context, List<Event> events) {
        this.color = ContextCompat.getColor(context, R.color.yellow);
        this.userEvents = events;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
//        return eventDates.contains(day);
        for (Event event: userEvents) {
            if(day.isInRange(CalendarDay.from(event.getStartDate()),
                    CalendarDay.from(event.getStartDate()))) {
                if (event.getAlarmTime() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new UserAlarmEventDotSpan(10, color));
    }
}