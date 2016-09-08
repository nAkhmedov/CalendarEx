package com.calendaridex.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * Created by Navruz on 01.08.2016.
 */
public class UserAlarmEventDotSpan implements LineBackgroundSpan {

    private float radius;
    private int color;

    public UserAlarmEventDotSpan(int radius, int color) {
        this.radius = radius;
        this.color = color;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline,
                               int bottom, CharSequence text, int start, int end, int lnum) {
        int oldColor = paint.getColor();
        if (color != 0) {
            paint.setColor(color);
        }

        canvas.drawCircle((left + right) / 3 + 45, bottom + radius, radius, paint);
        paint.setColor(oldColor);
    }
}
