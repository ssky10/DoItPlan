package com.teamsix.doitplan.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.ConnectServer;
import com.teamsix.doitplan.MainActivity;
import com.teamsix.doitplan.Plan;
import com.teamsix.doitplan.R;
import com.teamsix.doitplan.TestRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class RecyclerViewFragment extends Fragment {


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public static RecyclerViewFragment newInstance() {
        return new RecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }//카드형태로화면에출력

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final List<Plan> items = new ArrayList<>();

        @SuppressLint("StaticFieldLeak")
        ConnectServer.GetRecommendPlanTask getPlanTask = new ConnectServer.GetRecommendPlanTask(RecyclerViewFragment.this.getContext()){
            @Override
            protected void onPostExecute(final ArrayList<Plan> success) {
                if (success.size() != 0) { //로그인에 성공할 경우
                    for(int i=0;i<success.size();i++){
                        asyncDialog.dismiss();
                        Log.e("GetRecommendPlanTask","title="+success.get(i).title);
                        items.add(new Plan(success.get(i).planNo,success.get(i).title, success.get(i).planner,success.get(i).likes));

                        mRecyclerView.setAdapter(new TestRecyclerViewAdapter(items));
                    }
                } else { //로그인에 실패할 경우
                    //mPasswordView.setError(jObject.getString("msg"));
                    //mPasswordView.requestFocus();
                }
            }
        };
        getPlanTask.execute();


        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setEnabled(false);

        //setup materialviewpager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());
    }
}