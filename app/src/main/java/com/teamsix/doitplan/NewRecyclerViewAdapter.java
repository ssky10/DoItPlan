package com.teamsix.doitplan;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * 새로운 플랜을 위한 RecyclerViewAdapter
 */
public class NewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Intent> contents; //클릭이벤트를 위해서 Intent값 저장

    static final int TYPE_HEADER = 0;


    /**
     * 생성자 - 매개변수로 인텐트 리스트 가짐
     * */
    public NewRecyclerViewAdapter(List<Intent> contents) {
        this.contents = contents;
    }

    /**
     * 각 리스트를 구분하는 정보 전달
     * */
    @Override
    public int getItemViewType(int position) {
        return TYPE_HEADER;
    }

    /**
     * 총 리스트의 개수 반환
     * */
    @Override
    public int getItemCount() {
        return 1;
    }

    /**
     * 각각의 리스트를 생성
     * */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = null;

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card_newplan, parent, false);
        TextView textview = (TextView)view.findViewById(R.id.textView2);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.getContext().startActivity(contents.get(0)); //프래그먼트 상에서 인텐트 이동
            }
        });

        TextView textview1 = (TextView)view.findViewById(R.id.textView2);
        textview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.getContext().startActivity(contents.get(1)); //프래그먼트 상에서 인텐트 이동
            }
        });
        return new RecyclerView.ViewHolder(view) {
        };
    }//newplan레이아웃

    /**
     * 전체 리스트를 RecyclerView와 연결
     * */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                break;
        }
    }
}