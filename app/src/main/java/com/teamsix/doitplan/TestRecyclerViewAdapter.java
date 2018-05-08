package com.teamsix.doitplan;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card_big, parent, false);

        TextView textview = (TextView)view.findViewById(R.id.card_btitle);
        textview.setText(contents.get(viewType).title);

        TextView text_view = (TextView)view.findViewById(R.id.btextView);
        text_view.setText("Planner "+contents.get(viewType).planner);



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