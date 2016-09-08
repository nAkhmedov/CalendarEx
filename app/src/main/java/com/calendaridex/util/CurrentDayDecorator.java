package com.calendaridex.util;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.StyleSpan;

import com.calendaridex.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

/**
 * Created by Navruz on 17.06.2016.
 */
public class CurrentDayDecorator implements DayViewDecorator {

    private Drawable drawable;

    CalendarDay currentDay = CalendarDay.from(new Date());

    public CurrentDayDecorator(Activity context, boolean isHolidayExist) {
//        int drawableRes = (isHolidayExist) ? R.drawable.custom_current_day_selector : R.drawable.current_day_selector;
        int drawableRes = R.drawable.current_day_selector;
        drawable = ContextCompat.getDrawable(context, drawableRes);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(currentDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new RelativeSizeSpan(1.5f));
    }
}
