package com.teamsix.doitplan;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;

public class Searchctivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchctivity);

        keyword = (EditText)findViewById(R.id.et_search);
        mRecyclerView = (RecyclerView)findViewById(R.id.search_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

    }

    public void onClick(View view){
        final List<Plan> items = new ArrayList<>();

        Log.e("onClick","start");

        @SuppressLint("StaticFieldLeak")
        ConnectServer.ConnectServerDialogTask getPlanTask = new ConnectServer.ConnectServerDialogTask(
                this,
                "검색중입니다...",
                new FormBody.Builder()
                        .add("email", ApplicationController.getEmailId())
                        .add("keyword", keyword.getText().toString())
                        .build(),
                "getSearchPlan.php"){
            @Override
            protected void onPostExecute(final String result) {
                super.onPostExecute(result);
                Log.e("result",result);
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


                } else { //로그인에 실패할 경우
                    plans.add(new Plan(-1,"일치하는 결과가\n없습니다.","admin",0));
                }
                mRecyclerView.setAdapter(new TestRecyclerViewAdapter(plans));
            }
        };
        getPlanTask.execute();
    }
}
