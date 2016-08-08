package com.calendaridex.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calendaridex.R;
import com.calendaridex.constants.ContextConstants;
import com.calendaridex.database.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Navruz on 17.06.2016.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private final Context context;
    private List<Event> itemList = new ArrayList<>(100);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM", ContextConstants.currentCountry);
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", ContextConstants.currentCountry);

    public EventAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event itemValue = itemList.get(position);
        if (itemValue.getAdminEvent()) {
            holder.eventView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            holder.eventView.setText(itemValue.getTitle());
            if (dateFormat.format(itemValue.getStartDate()).equals(
                    dateFormat.format(itemValue.getEndDate()))) {
                holder.dateView.setText(dateFormat.format(itemValue.getStartDate()) + ":" );

            } else {
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(itemValue.getStartDate());
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(itemValue.getEndDate());
                String startMonthName =  monthFormat.format(startCal.getTime());
                String endMonthName =  monthFormat.format(endCal.getTime());
                if (startMonthName.equals(endMonthName)) {
                    holder.dateView.setText(startCal.get(Calendar.DAY_OF_MONTH) + " - " +
                            endCal.get(Calendar.DAY_OF_MONTH) + " " + endMonthName + ":");
                } else {
                    holder.dateView.setText(startCal.get(Calendar.DAY_OF_MONTH) + " " + startMonthName + " - " +
                            endCal.get(Calendar.DAY_OF_MONTH) + " " + endMonthName + ":");
                }
            }
        } else {
            holder.eventView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
            holder.dateView.setText(dateFormat.format(itemValue.getStartDate()) + ":");
            holder.eventView.setText(itemValue.getTitle());
        }
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void swapData(List<Event> eventList) {
        this.itemList = eventList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateView;
        public TextView eventView;

        public ViewHolder(View itemView) {
            super(itemView);
            dateView = (TextView) itemView.findViewById(R.id.date_view);
            eventView = (TextView) itemView.findViewById(R.id.name_view);
        }
    }
}