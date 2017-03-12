package com.example.archermind.coolweather.util;

import android.text.TextUtils;

import com.example.archermind.coolweather.db.City;
import com.example.archermind.coolweather.db.County;
import com.example.archermind.coolweather.db.Provice;
import com.example.archermind.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by archermind on 17-3-9.
 */

public class Utility {


    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");

            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 解析和处理服务器返回的省级数据
     * @param response
     * @return
     */
    public  static  boolean handleProviceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvices=new JSONArray(response);
                for(int i=0;i<allProvices.length();i++){
                    JSONObject proviceObject=allProvices.getJSONObject(i);
                    Provice provice=new Provice();
                    provice.setProviceName(proviceObject.getString("name"));
                    provice.setProviceCode(proviceObject.getInt("id"));
                    provice.save();

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }


    /**
     * 解析和处理服务器返回的市级数据
     * @param response
     * @return
     */
    public  static  boolean handleCityResponse(String response,int proviceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCitys=new JSONArray(response);
                for(int i=0;i<allCitys.length();i++){
                    JSONObject cityObject=allCitys.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProviceId(proviceId);
                    city.save();

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }


    /**
     * 解析和处理服务器返回的县级数据
     * @param response
     * @return
     */
    public  static  boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountys=new JSONArray(response);
                for(int i=0;i<allCountys.length();i++){
                    JSONObject countyObject=allCountys.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();

                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

}
