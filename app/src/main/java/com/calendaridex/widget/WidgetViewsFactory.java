/***
 Copyright (c) 2008-2012 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Advanced Android Development_
 http://commonsware.com/AdvAndroid
 */


package com.calendaridex.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.calendaridex.R;
import com.calendaridex.database.Event;

import java.util.List;

public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final List<Event> items;
    private Context ctxt=null;

    public WidgetViewsFactory(Context ctxt, List<Event> events) {
        this.items = events;
        this.ctxt=ctxt;
    }

    @Override
    public void onCreate() {
        // no-op
    }

    @Override
    public void onDestroy() {
        // no-op
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctxt.getPackageName(),
                R.layout.widget_list_item);

        Event event = items.get(position);
        row.setTextViewText(R.id.text1, event.getTitle());
        if (event.getAdminEvent()) {
            row.setTextColor(R.id.text1, ContextCompat.getColor(ctxt, R.color.colorPrimaryDark));
        } else {
            row.setTextColor(R.id.text1, ContextCompat.getColor(ctxt, android.R.color.holo_green_dark));

        }

//        Intent i=new Intent();
//        Bundle extras=new Bundle();
//
//        extras.putString(CalendarWidget.EXTRA_WORD, event.getTitle());
//        i.putExtras(extras);
//        row.setOnClickFillInIntent(android.R.id.text1, i);

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return(1);
    }

    @Override
    public long getItemId(int position) {
        return(position);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {

    }
}