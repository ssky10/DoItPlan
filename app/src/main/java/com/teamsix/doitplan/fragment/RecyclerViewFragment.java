package com.teamsix.doitplan.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.ConnectServer;
import com.teamsix.doitplan.Plan;
import com.teamsix.doitplan.R;
import com.teamsix.doitplan.TestRecyclerViewAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import okhttp3.FormBody;

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
        ConnectServer.ConnectServerDialogTask getPlanTask = new ConnectServer.ConnectServerDialogTask(
                RecyclerViewFragment.this.getContext(),
                "로딩중입니다...",
                new FormBody.Builder()
                        .add("email", ApplicationController.getEmailId())
                        .build(),
                "getRecommendPlan.php"){
            @Override
            protected void onPostExecute(final String result) {
                super.onPostExecute(result);
                ArrayList<Plan> plans = new ArrayList<>();
                try {
                    JSONArray jObjects = new JSONArray(result);
                    for (int i = 0; i < jObjects.length(); i++) {
                        JSONObject plan = jObjects.getJSONObject(i);
                        Plan p = new Plan(plan.getInt("plan_no"), plan.getString("msg"), plan.getString("nickname"), plan.getInt("likes_num"));
                        p.isLike = plan.getBoolean("islike");
                        Log.e("islike", String.valueOf(p.isLike));
                        plans.add(p);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (plans.size() != 0) { //로그인에 성공할 경우
                    for(int i=0;i<plans.size();i++)
                        Log.e("GetRecommendPlanTask","title="+plans.get(i).title);
                        //items.add(new Plan(success.get(i).planNo,success.get(i).title, success.get(i).planner,success.get(i).likes));
                        mRecyclerView.setAdapter(new TestRecyclerViewAdapter(plans));
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