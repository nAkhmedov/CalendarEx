package com.calendaridex.adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.calendaridex.ApplicationLoader;
import com.calendaridex.R;
import com.calendaridex.activity.EditEventActivity;
import com.calendaridex.constants.ContextConstants;
import com.calendaridex.constants.ExtraNames;
import com.calendaridex.database.Event;
import com.calendaridex.receiver.AlarmReceiver;
import com.calendaridex.util.AndroidUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Navruz on 29.06.2016.
 */
public class EditEventAdapter extends RecyclerView.Adapter<EditEventAdapter.ViewHolder> {

    private EditEventActivity mActivity;
    private static List<Event> eventList;
    private LayoutInflater inflater;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", ContextConstants.currentCountry);

    public EditEventAdapter(EditEventActivity mActivity) {
        this.mActivity = mActivity;
        inflater = LayoutInflater.from(mActivity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View emailView = inflater.inflate(R.layout.edit_event_list_item, parent, false);

        return new ViewHolder(emailView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Event event = eventList.get(position);
        StringBuilder builder = new StringBuilder(1000);
        builder.append(dateFormat.format(event.getStartDate()));
        if (event.getAlarmTime() != null) {
            builder.append(" ")
                    .append(event.getAlarmTime());
        }
        builder.append(": ")
                .append(event.getTitle());
        holder.eventTitle.setText(builder.toString());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void swapData(List<Event> eventList) {
        EditEventAdapter.eventList = eventList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView eventTitle;
        public ImageView editBtn;
        public ImageView deleteBtn;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            eventTitle       = (TextView) itemView.findViewById(R.id.event_title);
            editBtn    = (ImageView) itemView.findViewById(R.id.edit_btn);
            deleteBtn    = (ImageView) itemView.findViewById(R.id.delete_btn);

            deleteBtn.setOnClickListener(this);
            editBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.delete_btn: {
                    new AlertDialog.Builder(mActivity)
                            .setTitle(mActivity.getResources().getString(R.string.delete_header))
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteUserEvent(getAdapterPosition());
                                }
                            })
                            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();

                    break;
                }
                case R.id.edit_btn: {
                    editUserEvent(getAdapterPosition());
                    break;

                }
            }
        }

        private void editUserEvent(final int adapterPosition) {
            final Event event = eventList.get(adapterPosition);
            final String[] selectedTime = new String[1];
            selectedTime[0] = event.getAlarmTime();
            View customView = mActivity.getLayoutInflater().inflate(R.layout.add_event_dialog, null, false);
            final EditText input = (EditText) customView.findViewById(R.id.event_name);
            final CheckBox checkBoxView = (CheckBox) customView.findViewById(R.id.set_alarm);
            final Spinner spinner = (Spinner) customView.findViewById(R.id.spinner);
            spinner.setSelection(event.getAlarmRepeatPosition());

            checkBoxView.setChecked(event.getAlarmTime() != null);
            input.setText(event.getTitle());
            final AlertDialog alertDialog = new AlertDialog.Builder(mActivity)
                    .setCancelable(false)
                    .setTitle(mActivity.getResources().getString(R.string.edit_event))
                    .setView(customView)
                    .setPositiveButton(mActivity.getString(R.string.ok), null)
                    .setNegativeButton(mActivity.getString(R.string.action_cancel), null)
                    .create();

            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            alertDialog.show();

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String newTitle = input.getText().toString().trim();
                            String alarmTime = selectedTime[0];
                            if (newTitle.equals(event.getTitle()) && spinner.getSelectedItemPosition() == event.getAlarmRepeatPosition() &&
                                    ((alarmTime == null && event.getAlarmTime() == null) ||
                                            (alarmTime != null && alarmTime.equals(event.getAlarmTime()))) ) {
                                alertDialog.dismiss();
                                return;
                            }
                            event.setTitle(newTitle);
                            if (spinner.getSelectedItemPosition() != event.getAlarmRepeatPosition()) {
                                event.setAlarmRepeatPosition(spinner.getSelectedItemPosition());
                                if (event.getAlarmRepeatPosition() == ContextConstants.ACTIVE_REPEAT_COUNT) {
                                    for (int i = 1; i < 6; i++) {
                                        final Event userYearlyEvent = new Event();
                                        userYearlyEvent.setStartDate(AndroidUtil.getIncrementYear(i));
                                        userYearlyEvent.setEndDate(AndroidUtil.getIncrementYear(i));
                                        userYearlyEvent.setAdminEvent(false);
                                        userYearlyEvent.setTitle(event.getTitle());
                                        userYearlyEvent.setAlarmTime(event.getAlarmTime());
                                        userYearlyEvent.setAlarmRepeatPosition(event.getAlarmRepeatPosition());
                                        ApplicationLoader.getApplication(mActivity)
                                                .getDaoSession()
                                                .getEventDao()
                                                .insert(userYearlyEvent);
                                        addAlarm(userYearlyEvent);
                                        eventList.add(userYearlyEvent);
                                    }
                                }
                            }
                            if (alarmTime != null) {
                                event.setAlarmTime(alarmTime);
                                addAlarm(event);
                            } else {
                                dismissAlarm(event);
                                event.setAlarmTime(null);
                            }
                            ApplicationLoader.getApplication(mActivity)
                                    .getDaoSession()
                                    .getEventDao()
                                    .update(event);

                            notifyDataSetChanged();
                            alertDialog.dismiss();
                        }
                    }
            );

            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    }
            );

            checkBoxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Calendar mCurrentTime = Calendar.getInstance();
                        int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mCurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                String hours = selectedHour + "";
                                String minutes = selectedMinute + "";
                                if(selectedHour < 10) {
                                    hours = "0"+ selectedHour;
                                }
                                if(selectedMinute < 10)
                                    minutes = "0"+selectedMinute;
                                selectedTime[0] = hours + ":" + minutes;
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.show();
                        mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                checkBoxView.setChecked(false);
                            }
                        });
                    } else {
                        selectedTime[0] = null;
                    }
                }
            });
        }

        private void deleteUserEvent(int adapterPosition) {
            Event event = eventList.get(adapterPosition);
            ApplicationLoader.getApplication(mActivity)
                    .getDaoSession()
                    .getEventDao()
                    .delete(event);
            eventList.remove(adapterPosition);
            dismissAlarm(event);
            notifyItemRemoved(adapterPosition);
        }
    }

    private void addAlarm(Event userEvent) {

        if (userEvent.getAlarmTime() == null) {
            return;
        }

        Intent intent = new Intent(mActivity, AlarmReceiver.class);
        intent.putExtra(ExtraNames.ALARM_MESSAGE, userEvent.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity, userEvent.getId().intValue(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);
        Calendar alarmCalendar = AndroidUtil.getAlarmTime(userEvent);
        //0-doesn't repeat, 1-every day, 2-every month, 3-every year
        switch (userEvent.getAlarmRepeatPosition()) {
            case 0: {
                am.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
                break;
            }
            case 1: {
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                break;
            }
            case 2: {
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30, pendingIntent);
                break;
            }
            case 3: {
                am.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 365, pendingIntent);
                break;
            }
        }
    }

    private void dismissAlarm(Event userEvent) {
        if (userEvent.getAlarmTime() == null) {
            return;
        }
        Intent intent = new Intent(mActivity, AlarmReceiver.class);
        intent.putExtra(ExtraNames.ALARM_MESSAGE, userEvent.getTitle());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mActivity, userEvent.getId().intValue(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) mActivity.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
    }
}