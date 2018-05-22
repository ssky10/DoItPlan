package com.teamsix.doitplan;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.FormBody;

/**
 * 타인의 플랜을 위한 RecyclerViewAdapter
 */
public class TestRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Plan> contents; //각 Plan에 대한 정보 전달

    static final int TYPE_HEADER = 0;
    static final int TYPE_CELL = 1;

    /**
     * 생성자 - 매개변수로 인텐트 리스트 가짐
     */
    public TestRecyclerViewAdapter(List<Plan> contents) {
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
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card_big, parent, false);

        TextView textview = (TextView)view.findViewById(R.id.card_btitle);
        TextView text_view = (TextView)view.findViewById(R.id.btextView);
        final TextView tvLikes = (TextView)view.findViewById(R.id.tvLikes);
        final ImageButton btn_duplicate = (ImageButton)view.findViewById(R.id.bbutton3);
        final ImageButton btn_likes = (ImageButton)view.findViewById(R.id.bbutton2);

        textview.setText(contents.get(viewType).title);

        text_view.setText("Planner "+contents.get(viewType).planner);

        tvLikes.setText(contents.get(viewType).likes+"");


        if(contents.get(viewType).planNo==-1){
            btn_duplicate.setVisibility(View.GONE);
            btn_likes.setVisibility(View.GONE);
            text_view.setVisibility(View.GONE);
            tvLikes.setVisibility(View.GONE);
        }

        Log.e("islike", String.valueOf(contents.get(viewType).isLike));
        if(contents.get(viewType).isLike)
            btn_likes.setColorFilter(Color.parseColor("#4267b2"));
        else
            btn_likes.setColorFilter(Color.parseColor("#FF767676"));

        btn_likes.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                new ConnectServer.ConnectServerTask(new FormBody.Builder()
                        .add("email", ApplicationController.getEmailId())
                        .add("plan_ID",String.valueOf(contents.get(viewType).planNo))
                        .build(),"likeToggle.php"){
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.getBoolean("result")){
                                btn_likes.setColorFilter(Color.parseColor("#4267b2"));
                            }else{
                                btn_likes.setColorFilter(Color.parseColor("#FF767676"));
                            }
                            tvLikes.setText(jsonObject.getInt("count")+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }
        });


        btn_duplicate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                new ConnectServer.ConnectServerTask(new FormBody.Builder()
                        .add("email", ApplicationController.getEmailId())
                        .add("plan_no",String.valueOf(contents.get(viewType).planNo))
                        .build(),"duplicatePlan.php"){
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        Log.e("btn_duplicate",result);
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if(jsonObject.getBoolean("result")){
                                //Toast.makeText(parent.getContext(),"성공",Toast.LENGTH_LONG).show();
                            }else{
                                //Toast.makeText(parent.getContext(),"실패",Toast.LENGTH_LONG).show();
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