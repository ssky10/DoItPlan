package com.teamsix.doitplan.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.Plan;
import com.teamsix.doitplan.R;
import com.teamsix.doitplan.SmallRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class SmallRecyclerViewFragment extends Fragment {

    private static final boolean GRID_LAYOUT = false;
    private static final int ITEM_COUNT = 10;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public static SmallRecyclerViewFragment newInstance() {
        return new SmallRecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }//카드형태로화면에출력

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        List<Plan> items = ApplicationController.getAllPlanDB();

        if(items.size() == 0) items.add(new Plan(-1,"나의 Plan이 없습니다.","admin",0));

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                List<Plan> newItem = ApplicationController.getAllPlanDB();
                mRecyclerView.setAdapter(new SmallRecyclerViewAdapter(newItem));

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });


        //setup materialviewpager
        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }//카드가한줄에하나씩
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
        mRecyclerView.setAdapter(new SmallRecyclerViewAdapter(items));
    }
}