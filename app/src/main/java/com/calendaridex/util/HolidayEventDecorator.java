package com.calendaridex.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.calendaridex.R;
import com.calendaridex.database.Event;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.List;

/**
 * Created by Navruz on 17.06.2016.
 */
public class HolidayEventDecorator implements DayViewDecorator {

    private final Drawable drawable;
    private final List<Event> events;
//    private HashSet<CalendarDay> dates;

//    public HolidayEventDecorator(Activity context, HashSet<CalendarDay> calendarDays) {
//        this.dates = calendarDays;
//        this.drawable = ContextCompat.getDrawable(context, R.drawable.event_day_selector);
//    }

    public HolidayEventDecorator(Activity context, List<Event> adminEvents) {
        this.events = adminEvents;
        this.drawable = ContextCompat.getDrawable(context, R.drawable.event_day_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
//        return dates.contains(day);
        for (Event event: events) {
            if(day.isInRange(CalendarDay.from(event.getStartDate()),
                    CalendarDay.from(event.getEndDate()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
//        view.setSelectionDrawable(drawable);
        view.addSpan(new AdminEventDotSpan(10, R.color.colorPrimaryDark));
    }
}
