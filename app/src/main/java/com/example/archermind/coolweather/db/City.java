package com.example.archermind.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by archermind on 17-3-9.
 */

public class City extends DataSupport {

    private int id;

    private String cityName;

    private int cityCode;

    private int ProviceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProviceId() {
        return ProviceId;
    }

    public void setProviceId(int proviceId) {
        ProviceId = proviceId;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
