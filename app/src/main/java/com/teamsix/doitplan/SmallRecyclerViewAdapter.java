package com.teamsix.doitplan;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;

/**
 * 나만의 플랜을 위한 RecyclerViewAdapter
 */
public class SmallRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Plan> contents; //각 Plan에 대한 정보 전달

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    /**
     * 생성자 - 매개변수로 인텐트 리스트 가짐
     */
    public SmallRecyclerViewAdapter(List<Plan> contents) {
        this.contents = contents;
    }

    /**
     * 각 리스트를 구분하는 정보 전달
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * 총 리스트의 개수 반환
     */
    @Override
    public int getItemCount() {
        return contents.size();
    }

    /**
     * 각각의 리스트를 생성
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card_small, parent, false);

        TextView textview = (TextView) view.findViewById(R.id.card_stitle);
        Switch swWork = (Switch) view.findViewById(R.id.switch1);
        final ImageButton btnShare = (ImageButton)view.findViewById(R.id.sbutton2);
        final ImageButton btnModify = (ImageButton)view.findViewById(R.id.sbutton3);

        textview.setText(contents.get(viewType).title);

        if(contents.get(viewType).planNo==-1){
            swWork.setVisibility(View.GONE);
            btnShare.setVisibility(View.GONE);
            btnModify.setVisibility(View.GONE);
        }


        swWork.setChecked(contents.get(viewType).isWork);
        swWork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ApplicationController.setWork(contents.get(viewType).planNo,1);
                }else{
                    ApplicationController.setWork(contents.get(viewType).planNo,0);
                }
            }
        });


        if(contents.get(viewType).isShare){
            btnShare.setColorFilter(Color.parseColor("#4267b2"));
        }
        btnShare.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                new ConnectServer.ConnectServerTask(new FormBody.Builder()
                        .add("email", ApplicationController.getEmailId())
                        .add("plan_ID",String.valueOf(contents.get(viewType).planNo))
                        .build(),"shareToggle.php"){
                    @Override
                    protected void onPostExecute(JSONObject result) {
                        Log.e("shareToggle",result.toString());
                        super.onPostExecute(result);
                        try {
                            if(result.getBoolean("result")){
                                contents.get(viewType).isShare = true;
                                btnShare.setColorFilter(Color.parseColor("#4267b2"));
                            }else{
                                contents.get(viewType).isShare = false;
                                btnShare.setColorFilter(Color.parseColor("#FF767676"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }
        });


        return new RecyclerView.ViewHolder(view) {
        };
    }//plan 나열 레이아웃

    /**
     * 전체 리스트를 RecyclerView와 연결
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                break;
            case TYPE_CELL:
                break;
        }
    }//plan 0,1,2,...숫자
}