package com.calendaridex.service;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.widget.RemoteViewsService;

import com.calendaridex.ApplicationLoader;
import com.calendaridex.database.Event;
import com.calendaridex.database.EventDao;
import com.calendaridex.util.AndroidUtil;
import com.calendaridex.widget.WidgetViewsFactory;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by Navruz on 27.06.2016.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        List<Event> eventList = getEventsByDate(this);
        return new WidgetViewsFactory(this.getApplicationContext(),
                eventList);

    }

    private List<Event> getEventsByDate(Context context) {
        Date date = AndroidUtil.getCurrentDate();
        Pair<Date, Date> range = AndroidUtil.getDailyDateRange(date);
        QueryBuilder<Event> qb = ApplicationLoader.getApplication(context)
                .getDaoSession()
                .getEventDao()
                .queryBuilder();
        qb.whereOr(qb.and(EventDao.Properties.StartDate.le(range.first),
                        EventDao.Properties.EndDate.ge(range.first)),
                qb.and(EventDao.Properties.StartDate.le(range.second),
                        EventDao.Properties.EndDate.ge(range.second)));
//        qb.whereOr(EventDao.Properties.StartDate.between(range.first, range.second),
//                EventDao.Properties.EndDate.between(range.first, range.second));
        return qb.build().list();
    }
}
