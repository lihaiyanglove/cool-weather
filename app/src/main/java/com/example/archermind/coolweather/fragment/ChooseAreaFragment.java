package com.example.archermind.coolweather.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.archermind.coolweather.MainActivity;
import com.example.archermind.coolweather.R;
import com.example.archermind.coolweather.WeatherActivity;
import com.example.archermind.coolweather.db.City;
import com.example.archermind.coolweather.db.County;
import com.example.archermind.coolweather.db.Provice;
import com.example.archermind.coolweather.util.HttpUtil;
import com.example.archermind.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by archermind on 17-3-10.
 */

public class ChooseAreaFragment extends Fragment {


    public static final int LEVEL_PROVICE =0;
    public static final int LEVEL_CITY =1;
    public static final int LEVEL_COUNTY =2;


    private ProgressDialog progressDialog;

    private TextView titleText;
    private Button backButton;
    private ListView listView;


    private ArrayAdapter<String> adapter;

    private List<String> dataList=new ArrayList<>();

    private List<Provice> proviceList;

    private List<City> cityList;

    private List<County> countyList;

    private Provice selectedProviced;

    private City selectedCity;

    private int currentLevel;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.choose_area,container,false);
        titleText = ((TextView) view.findViewById(R.id.title_text));
        backButton = ((Button) view.findViewById(R.id.back_button));
        listView = ((ListView) view.findViewById(R.id.list_view));

        adapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVICE){
                    selectedProviced=proviceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId=countyList.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity){
                        Intent intent=new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof  WeatherActivity){
                        WeatherActivity activity=(WeatherActivity)getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }

                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvices();
                }
            }
        });


        queryProvices();
    }

    private void queryProvices(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        proviceList=DataSupport.findAll(Provice.class);
        if(proviceList.size()>0){
            dataList.clear();
            for (Provice provice : proviceList){
                dataList.add(provice.getProviceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVICE;
        }else{
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"provice");
        }
    }

    private void queryCities(){
        titleText.setText(selectedProviced.getProviceName());
        backButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("proviceid = ?",String.valueOf(selectedProviced.getId())).find(City.class);
        if(cityList.size()>0){
            dataList.clear();
            for (City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int proviceCode=selectedProviced.getProviceCode();
            String address="http://guolin.tech/api/china/"+proviceCode;
            queryFromServer(address,"city");
        }
    }


    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for (County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            int proviceCode=selectedProviced.getProviceCode();
            int cityCode=selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+proviceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }


    private void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText=response.body().string();
                boolean result = false;
                if("provice".equals(type)){
                    result= Utility.handleProviceResponse(responseText);
                }else if("city".equals(type)){
                    result= Utility.handleCityResponse(responseText,selectedProviced.getId());
                }else if("county".equals(type)){
                    result= Utility.handleCountyResponse(responseText,selectedCity.getId());
                }


                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("provice".equals(type)){
                                queryProvices();
                            }else if("city".equals(type)){
                               queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    private void showProgressDialog() {
        if(progressDialog == null){
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();

    }


    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
