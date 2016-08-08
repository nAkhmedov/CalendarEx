package com.calendaridex.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.calendaridex.ApplicationLoader;
import com.calendaridex.R;
import com.calendaridex.adapter.EventAdapter;
import com.calendaridex.constants.ContextConstants;
import com.calendaridex.constants.ExtraNames;
import com.calendaridex.database.Event;
import com.calendaridex.database.EventDao;
import com.calendaridex.execution.CustomHTTPService;
import com.calendaridex.receiver.AlarmReceiver;
import com.calendaridex.util.AndroidUtil;
import com.calendaridex.util.CurrentDayDecorator;
import com.calendaridex.util.DispatchQueue;
import com.calendaridex.util.HolidayEventDecorator;
import com.calendaridex.util.UserEventDecorator;
import com.calendaridex.util.WeekendsDecorator;
import com.calendaridex.widget.CalendarWidget;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseCEActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int EDIT_EVENT_CODE = 102;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM  yyyy", ContextConstants.currentCountry);
    private SimpleDateFormat monthDateFormat = new SimpleDateFormat("MMMM  yyyy", ContextConstants.currentCountry);
    private SimpleDateFormat dateFormatParser = new SimpleDateFormat("yyyy-MM-dd", ContextConstants.currentCountry);

    private DateFormatSymbols symbols = new DateFormatSymbols(ContextConstants.currentCountry);
    private Calendar calendar = new GregorianCalendar(ContextConstants.currentCountry);

    private EventAdapter mAdapter;

    private MaterialCalendarView calendarView;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private CalendarDay selectedDate;
    private CustomHTTPService http;

    private List<Event> allEvents;
    private RelativeLayout adContainer;

    /*For AdMob*/
    private boolean displayInterAdds = true;
    private Date currentMonthDate;

    public static volatile DispatchQueue globalQueue = new DispatchQueue("MainActivityBgThread");
    private MenuItem editEventMenu;
    private RelativeLayout contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendEventData();
        sendAdMobData();

        getSupportActionBar().setTitle(dateFormat.format(AndroidUtil.getCurrentDate()));

        adContainer = (RelativeLayout) findViewById(R.id.adMobView);
        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        contentView = (RelativeLayout) findViewById(R.id.top_panel);
        calendarView = (MaterialCalendarView) findViewById(R.id.calendar_view);
        RecyclerView eventsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_events);
        final TextView monthTitleView = (TextView) findViewById(R.id.top_month_title);
        ImageView leftArrowView = (ImageView) findViewById(R.id.left_arrow);
        ImageView rightArrowView = (ImageView) findViewById(R.id.right_arrow);

        monthTitleView.setText(monthDateFormat.format(new Date()));
        leftArrowView.setOnClickListener(this);
        rightArrowView.setOnClickListener(this);

        RecyclerView.LayoutManager mlayoutManager = new LinearLayoutManager(this);
        eventsRecyclerView.setLayoutManager(mlayoutManager);
        mAdapter = new EventAdapter(MainActivity.this);
        eventsRecyclerView.setAdapter(mAdapter);

        currentMonthDate = new Date();
        calendarView.state().edit()
                .setFirstDayOfWeek(calendar.getFirstDayOfWeek())
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        calendarView.setTopbarVisible(false);
        calendarView.setCurrentDate(currentMonthDate);
        calendarView.setWeekDayTextAppearance(R.style.WhiteColorStyle);
        calendarView.setDateTextAppearance(R.style.WhiteColorStyle);
//        calendarView.setHeaderTextAppearance(R.style.WhiteTitleColorStyle);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                showEventsByDate(date.getDate());
                selectedDate = date;
            }
        });

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                currentMonthDate = date.getDate();
                monthTitleView.setText(monthDateFormat.format(currentMonthDate));
                if (mInterstitialAd.isLoaded() && displayInterAdds) {
                    mInterstitialAd.show();
                    displayInterAdds = false;
                } else {
                    requestNewInterstitial();
                }
                showEventsByDate(currentMonthDate);
                selectedDate = null;
            }
        });


        String[] dayNames = symbols.getShortWeekdays();
        dayNames = AndroidUtil.removeExtraValues(dayNames);
        calendarView.setWeekDayLabels(dayNames);

//        String[] monthNames = symbols.getShortMonths();
//        calendarView.setTitleMonths(monthNames);

        showEventsByDate(AndroidUtil.getCurrentDate());

        renderAdminEvents();
        renderUserEvents();
        calendarView.addDecorator(new WeekendsDecorator(MainActivity.this));

        notifyEventsUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        editEventMenu = menu.findItem(R.id.edit_event);
        List<Event> userEvents = ApplicationLoader.getApplication(this)
                .getDaoSession()
                .getEventDao()
                .queryBuilder()
                .where(EventDao.Properties.AdminEvent.eq(false))
                .list();
        editEventMenu.setVisible(!userEvents.isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_event: {
                if (selectedDate != null) {
                    showAddEventDialog(selectedDate);
                } else {
                    Snackbar.make(contentView, getString(R.string.select_date), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            }
            case R.id.edit_event: {
                Intent intent = new Intent(MainActivity.this, EditEventActivity.class);
                startActivityForResult(intent, EDIT_EVENT_CODE);
                break;
            }
            case R.id.settings: {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, EDIT_EVENT_CODE);
                break;
            }
        }
        return true;
    }

    private void showEventsByDate(final Date date) {
        globalQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                Pair<Date, Date> range = AndroidUtil.getDailyDateRange(date);
                QueryBuilder qb = ApplicationLoader.getApplication(MainActivity.this)
                        .getDaoSession()
                        .getEventDao()
                        .queryBuilder();
                allEvents = qb.whereOr(qb.and(EventDao.Properties.StartDate.le(range.first),
                        EventDao.Properties.EndDate.ge(range.first)),
                        qb.and(EventDao.Properties.StartDate.le(range.second),
                                EventDao.Properties.EndDate.ge(range.second)))
                        .orderAsc(EventDao.Properties.StartDate)
                        .build()
                        .list();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.swapData(allEvents);
                    }
                });
            }
        });
    }

    private void sendAdMobData() {
        globalQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                //        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();//If need to logging, just uncomment
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(interceptor)
                        .build();

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ContextConstants.APP_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();

                http = retrofit.create(CustomHTTPService.class);

                Call<JsonObject> call = http.sendAdMobRequest(ContextConstants.currentCountry.getCountry());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        JsonObject adMobObject = response.body();
                        initializeAdMob(adMobObject);
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    private void initializeAdMob(JsonObject adMobObject) {
        MobileAds.initialize(getApplicationContext(), adMobObject.get("app_id").getAsString());

        boolean isBannerAdsEnabled = adMobObject.get("banner_status").getAsString().equals("1");
        boolean isIntertialAdsEnabled = adMobObject.get("inertial_status").getAsString().equals("1");
        if (isBannerAdsEnabled) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mAdView = new AdView(MainActivity.this);
            mAdView.setAdSize(AdSize.BANNER);
            mAdView.setAdUnitId(adMobObject.get("banner_ad_id").getAsString());
            adContainer.addView(mAdView);
            mAdView.loadAd(adRequest);
        }

        if (isIntertialAdsEnabled) {
            mInterstitialAd.setAdUnitId(adMobObject.get("inertial_ads_id").getAsString());
            requestNewInterstitial();
        }
    }

    private void sendEventData() {
//        showDialog();
        globalQueue.postRunnable(new Runnable() {
            @Override
            public void run() {
                final SharedPreferences sharedPref = getSharedPreferences(
                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                long lastUpdateTime = sharedPref.getLong("last_update_time", 0);
                if (lastUpdateTime == 0 || AndroidUtil.isMoreThanSelectedDays(new Date(lastUpdateTime), ContextConstants.UPDATE_PERIOD)) {
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();//If need to logging, just uncomment
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    OkHttpClient client = new OkHttpClient.Builder()
//                    .addInterceptor(interceptor)
                            .build();

                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ContextConstants.APP_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .client(client)
                            .build();

                    http = retrofit.create(CustomHTTPService.class);

                    Call<JsonArray> call = http.sendEventsRequest(ContextConstants.currentCountry.getCountry());
                    call.enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                            JsonArray eventsArray = response.body();
                            try {
                                parseJsonArray(eventsArray);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putLong("last_update_time", new Date().getTime());
                                editor.apply();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonArray> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                } else {
                    Log.i("TAG", "Next download event after 30 day from " + new Date(lastUpdateTime).toString());
                }
            }
        });
    }

    private void parseJsonArray(JsonArray eventsArray) throws ParseException {
        for (JsonElement jsonElement: eventsArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Event event = new Event();
            event.setTitle(jsonObject.get("title").getAsString());
            event.setStartDate(dateFormatParser.parse(jsonObject.get("start_date").getAsString()));
            event.setEndDate(dateFormatParser.parse(jsonObject.get("end_date").getAsString()));
            event.setAdminEvent(true);

            EventDao eventDao = ApplicationLoader.getApplication(MainActivity.this)
                    .getDaoSession()
                    .getEventDao();
            if (eventDao.queryBuilder().where(
                    EventDao.Properties.StartDate.eq(event.getStartDate()),
                    EventDao.Properties.Title.eq(event.getTitle()))
                        .buildCount().count() == 0) {
                eventDao.insert(event);
            }

            allEvents.add(event);
        }
        showEventsByDate(AndroidUtil.getCurrentDate());
        renderAdminEvents();
//        dismissDialog();
    }

    private void renderAdminEvents() {
        QueryBuilder queryBuilder = ApplicationLoader.getApplication(this)
                .getDaoSession()
                .getEventDao()
                .queryBuilder();
        List<Event> adminEvents = queryBuilder
                .where(EventDao.Properties.AdminEvent.eq(true))
                .list();
        calendarView.addDecorators(new HolidayEventDecorator(this, adminEvents));
        boolean holidayExist = queryBuilder
                .where(EventDao.Properties.AdminEvent.eq(true),
                        EventDao.Properties.StartDate.eq(AndroidUtil.getCurrentDate()))
                .buildCount().count() != 0;

        calendarView.addDecorators(new CurrentDayDecorator(this, holidayExist));
    }

    private void renderUserEvent(Event event) {
        List<Event> userEvent = new ArrayList<>(1);
        userEvent.add(event);
        UserEventDecorator userDec = new UserEventDecorator(this, userEvent);
        calendarView.addDecorator(userDec);
    }

    private void renderUserEvents() {
        List<Event> userEvents = ApplicationLoader.getApplication(this)
                .getDaoSession()
                .getEventDao()
                .queryBuilder()
                .where(EventDao.Properties.AdminEvent.eq(false))
                .list();
        UserEventDecorator userDec = new UserEventDecorator(this, userEvents);
        calendarView.addDecorator(userDec);
        if (editEventMenu != null) {
            editEventMenu.setVisible(!userEvents.isEmpty());
        }
    }

    private void showAddEventDialog(final CalendarDay dateClicked) {
        View customView = getLayoutInflater().inflate(R.layout.add_event_dialog, null, false);
        final EditText input = (EditText) customView.findViewById(R.id.event_name);
        final CheckBox checkBoxView = (CheckBox) customView.findViewById(R.id.set_alarm);
        final String[] selectedTime = {""};

        final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(false)
                .setTitle(getResources().getString(R.string.create_event))
                .setView(customView)
                .setPositiveButton(getString(R.string.ok), null)
                .setNegativeButton(getString(R.string.action_cancel), null)
                .create();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (selectedTime[0].isEmpty()) {
                            addUserEvent(dateClicked, input.getText().toString(), null);
                        } else {
                            addUserEvent(dateClicked, input.getText().toString(), selectedTime[0]);

                        }
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
                    mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            selectedTime[0] = selectedHour + ":" + selectedMinute;
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.show();
                    mTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            checkBoxView.setChecked(false);
                        }
                    });
                }
            }
        });
    }

    private void addUserEvent(CalendarDay dateClicked, String eventData, String alarmTime) {
        Event userEvent = new Event();
        userEvent.setStartDate(dateClicked.getDate());
        userEvent.setEndDate(dateClicked.getDate());
        userEvent.setAdminEvent(false);
        userEvent.setTitle(eventData);
        if (alarmTime != null) {
            userEvent.setAlarmTime(alarmTime);
            Calendar alarmCalendar = Calendar.getInstance();
            alarmCalendar.setTime(dateClicked.getDate());
            alarmCalendar.set(Calendar.MILLISECOND, 0);
            alarmCalendar.set(Calendar.SECOND, 0);
            String[] hours = alarmTime.split(":");
            alarmCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours[0]));
            alarmCalendar.set(Calendar.MINUTE, Integer.parseInt(hours[1]));
            addAlarm(eventData, alarmCalendar);
        }
        allEvents.add(userEvent);
        ApplicationLoader.getApplication(MainActivity.this)
                .getDaoSession()
                .getEventDao()
                .insert(userEvent);
        mAdapter.swapData(allEvents);
        renderUserEvent(userEvent);
        editEventMenu.setVisible(true);
        updateAllWidgets();
    }

    private void addAlarm(String eventData, Calendar alarmCalendar) {
        Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
        intent.putExtra(ExtraNames.ALARM_MESSAGE, eventData);
        // In reality, you would want to have a static variable for the request code instead of 192837
        PendingIntent sender = PendingIntent.getBroadcast(this, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), sender);
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_arrow: {
                calendarView.goToPrevious();
                break;
            }
            case R.id.right_arrow: {
                calendarView.goToNext();
                break;
            }
        }
    }

    private void requestNewInterstitial() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded() && mInterstitialAd.getAdUnitId() != null) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    private void notifyEventsUser() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        long lastNotifiedTime = sharedPref.getLong("last_notified_time", 0);
//        show events on notification bar once a day
        if (DateUtils.isToday(lastNotifiedTime)) {
            return;
        }
        Pair<Date, Date> range = AndroidUtil.getDailyDateRange(AndroidUtil.getCurrentDate());
        QueryBuilder<Event> qb = ApplicationLoader.getApplication(MainActivity.this)
                .getDaoSession()
                .getEventDao()
                .queryBuilder();
//        qb.whereOr(qb.and(EventDao.Properties.StartDate.le(range.first),
//                        EventDao.Properties.EndDate.ge(range.first)),
//                qb.and(EventDao.Properties.StartDate.le(range.second),
//                        EventDao.Properties.EndDate.ge(range.second)));
        qb.whereOr(EventDao.Properties.StartDate.between(range.first, range.second),
                EventDao.Properties.EndDate.between(range.first, range.second));
        List<Event> eventsList = qb.build().list();
        Log.i(TAG, "eventsList size= " + eventsList.size());

        boolean isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        int icon = (isLollipop) ? R.drawable.call_ntfy : R.drawable.calendar_icon;
        for (int i = 0; i < eventsList.size(); i++) {
            Event event = eventsList.get(i);
            String text = event.getTitle();
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(text)
                    .setSmallIcon(icon)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(text))
                    .setWhen(System.currentTimeMillis());

            Intent nIntent = getPackageManager().
                    getLaunchIntentForPackage(ApplicationLoader.getAppContext().getPackageName());
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, nIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
            Notification note = notificationBuilder.build();

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(i, note);
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("last_notified_time", new Date().getTime());
        editor.apply();
    }

    private void updateAllWidgets() {
        AppWidgetManager man = AppWidgetManager.getInstance(MainActivity.this);
        int[] ids = man.getAppWidgetIds(
                new ComponentName(MainActivity.this, CalendarWidget.class));
        Intent intent = new Intent(this,CalendarWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_EVENT_CODE) {
            if (resultCode == RESULT_OK) {
                updateMainUiForUserEvent();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateMainUiForUserEvent() {
        showEventsByDate(currentMonthDate);
        updateDecorators();
    }

    private void updateDecorators() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                calendarView.removeDecorators();
                renderAdminEvents();
                renderUserEvents();
            }
        }).run();
    }
}
