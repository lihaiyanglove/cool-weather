package com.example.archermind.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by archermind on 17-3-10.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String into;
    }
}
