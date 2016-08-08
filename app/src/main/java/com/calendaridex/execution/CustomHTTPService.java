package com.calendaridex.execution;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Navruz on 20.06.2016.
 */
public interface CustomHTTPService {

    @GET("events/gba")
    Call<JsonArray> sendEventsRequest(@Query("alias") String alias);

    @GET("ad-mob/app")
    Call<JsonObject> sendAdMobRequest(@Query("id") String alias);
}