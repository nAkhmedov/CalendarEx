package com.calendaridex.util;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import com.calendaridex.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

/**
 * Created by Navruz on 17.06.2016.
 */
public class WeekendsDecorator implements DayViewDecorator {

    private final int color;

    public WeekendsDecorator(Activity context) {
        color = ContextCompat.getColor(context, R.color.colorPrimaryDark);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        int days = day.getCalendar().get(Calendar.DAY_OF_WEEK);
        if(days == Calendar.SATURDAY || days == Calendar.SUNDAY) {
            return true;
        }
        return false;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(color));

    }
}
