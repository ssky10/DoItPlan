package com.teamsix.doitplan.background;

import android.annotation.SuppressLint;
import android.content.Context;

import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.ConnectServer;
import com.teamsix.doitplan.GetIfResult;
import com.teamsix.doitplan.Plan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class Forecast {
    private String date;
    private String time;
    private double lat;
    private double lon;
    private int T1H;
    private int RN1;
    private int SKY;
    private int REH;
    private int PTY;
    private int LGT;
    private int VEC;
    private int WSD;
    private int oldSky = 0;
    private int oldState = -1;

    public int getTemperature(){
        return T1H;
    }

    public int getRain(){
        return RN1;
    }

    public String getSky(){
        switch(SKY){
            case 1 : return "맑음";
            case 2 : return "구름조금";
            case 3 : return "구름많음";
            case 4 : return "흐림";
            default: return "값없음";
        }
    }

    public boolean isSunny(){
        if(SKY == oldSky) return false;
        if(SKY == 1) return true;
        return false;
    }

    public boolean isCloud(){
        if(SKY==oldSky) return false;
        if(SKY == 4) return true;
        return false;
    }

    public boolean isRain(){
        if(PTY == oldState) return false;
        if(PTY == 0) return false;
        return true;
    }

    public int getHumidity(){
        return REH;
    }

    public String getState(){
        switch(PTY){
            case 0 : return "없음";
            case 1 : return "비";
            case 2 : return "진눈깨비";
            case 3 : return "눈";
            default: return "값없음";
        }
    }

    public boolean getThunder(){
        if(LGT==0) return false;
        else return true;
    }


    public void getNowData(Context context, double lat, double lon){
        long now = System.currentTimeMillis();
        // 현재시간을 date 변수에 저장한다.
        Date date = new Date(now);

        SimpleDateFormat sdfNow = new SimpleDateFormat("m");
        String nowTime = sdfNow.format(date);

        if(Integer.parseInt(nowTime)<45)  now = now - (60*60*1000);
        date = new Date(now);

        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
        sdfNow = new SimpleDateFormat("yyyyMMdd");
        // nowDate 변수에 값을 저장한다.
        String nowDate = sdfNow.format(date);

        sdfNow = new SimpleDateFormat("HH00");
        nowTime = sdfNow.format(date);

        this.getData(context, lat,lon,nowDate,nowTime);
    }



    public void getData(final Context context, double lat, double lon, String date, String time){
        this.lat = lat; this.lon = lon; this.date = date; this.time = time;
        oldSky = SKY; oldState = PTY;
        @SuppressLint("StaticFieldLeak")
        ConnectServer.GetForecastTask task = new ConnectServer.GetForecastTask(lat,lon,date,time){
            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                try {
                    JSONObject response = jsonObject.getJSONObject("response");
                    JSONObject body = response.getJSONObject("body");
                    JSONObject items = body.getJSONObject("items");
                    JSONArray itemArray = items.getJSONArray("item");
                    JSONObject itme;
                    for(int i=0;i<itemArray.length();i++){
                        itme = itemArray.getJSONObject(i);
                        String category = itme.getString("category");
                        if(category.equals("T1H")){
                            T1H = itme.getInt("obsrValue");
                        }else if(category.equals("RN1")){
                            RN1 = itme.getInt("obsrValue");
                        }else if(category.equals("SKY")){
                            SKY = itme.getInt("obsrValue");
                        }else if(category.equals("REH")){
                            REH = itme.getInt("obsrValue");
                        }else if(category.equals("PTY")){
                            PTY = itme.getInt("obsrValue");
                        }else if(category.equals("LGT")){
                            LGT = itme.getInt("obsrValue");
                        }else if(category.equals("VEC")){
                            VEC = itme.getInt("obsrValue");
                        }else if(category.equals("WSD")){
                            WSD = itme.getInt("obsrValue");
                        }
                    }

                    List<Plan> list = GetIfResult.getBoolean(ApplicationController.getIfPlanDB(Plan.IF_WEATHER),getSky(),getState(),getHumidity()+"");
                    if(list.size()==0) return;
                    for(int j=0;j<list.size();j++){
                        GetIfResult.doitResult(list.get(j).resultCode,list.get(j).resultValue,context);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        task.execute();
    }

}
