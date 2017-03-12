package com.example.archermind.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by archermind on 17-3-10.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
