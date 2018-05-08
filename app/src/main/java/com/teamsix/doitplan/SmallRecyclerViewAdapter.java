package com.teamsix.doitplan;

import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

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
        textview.setText(contents.get(viewType).title);

        TextView text_view = (TextView) view.findViewById(R.id.stextView);
        text_view.setText(contents.get(viewType).planner);

        Switch swWork = (Switch) view.findViewById(R.id.switch1);
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

        final ImageButton btnShare = (ImageButton)view.findViewById(R.id.sbutton2);
        if(contents.get(viewType).isShare){
            btnShare.setColorFilter(Color.parseColor("#4267b2"));
        }
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contents.get(viewType).isShare){
                    contents.get(viewType).isShare = false;
                    btnShare.setColorFilter(Color.parseColor("#FF767676"));
                }else{
                    contents.get(viewType).isShare = true;
                    btnShare.setColorFilter(Color.parseColor("#4267b2"));
                }
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