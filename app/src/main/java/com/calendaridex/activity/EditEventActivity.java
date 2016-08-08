package com.calendaridex.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.calendaridex.ApplicationLoader;
import com.calendaridex.R;
import com.calendaridex.adapter.EditEventAdapter;
import com.calendaridex.database.Event;
import com.calendaridex.database.EventDao;

import java.util.List;

/**
 * Created by Navruz on 29.06.2016.
 */
public class EditEventActivity extends BaseCEActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_event);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.editEventList);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        List<Event> eventList = ApplicationLoader.getApplication(this)
                .getDaoSession()
                .getEventDao()
                .queryBuilder()
                .where(EventDao.Properties.AdminEvent.eq(false))
                .orderAsc(EventDao.Properties.StartDate)
                .list();
        EditEventAdapter eventListAdapter = new EditEventAdapter(this);
        recyclerView.setAdapter(eventListAdapter);
        eventListAdapter.swapData(eventList);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
