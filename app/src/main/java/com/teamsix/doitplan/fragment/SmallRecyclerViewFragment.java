package com.teamsix.doitplan.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.ConnectServer;
import com.teamsix.doitplan.Plan;
import com.teamsix.doitplan.Pop2Activity;
import com.teamsix.doitplan.R;
import com.teamsix.doitplan.SmallRecyclerViewAdapter;
import com.teamsix.doitplan.background.AlarmUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;

import static android.app.Activity.RESULT_OK;
import static com.teamsix.doitplan.Plan.ifIntentToSting;
import static com.teamsix.doitplan.Plan.resultIntentToSting;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class SmallRecyclerViewFragment extends Fragment {

    private static final boolean GRID_LAYOUT = false;
    Intent ifInfo = null;
    Intent resultInfo = null;
    Plan plan;

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

        if (items.size() == 0) items.add(new Plan(-1, "나의 Plan이 없습니다.", "admin", 0));
        else {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).ifCode == Plan.IF_TIME && items.get(i).getIsWorkToInt() == 1) {
                    if (AlarmUtils.alarmPlanNo.indexOf(items.get(i).planNo) == -1)
                        AlarmUtils.getInstance().startAlarmUpdate(getContext(), items.get(i).planNo);
                }
            }
        }

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                List<Plan> newItem = ApplicationController.getAllPlanDB();
                if (newItem.size() == 0) newItem.add(new Plan(-1, "나의 Plan이 없습니다.", "admin", 0));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.e("onActivityResult", requestCode + "/" + resultCode);
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                ifInfo = data;
                int planNo = data.getIntExtra("planno", -1);
                plan = ApplicationController.getPlan(planNo);
                if (plan == null) return;
                Intent intent = new Intent(getContext(), Pop2Activity.class);
                intent.putExtra("Result", plan.resultCode);
                intent.putExtra("type", 2);
                intent.putExtra("planno", plan.planNo);
                intent.putExtras(plan.getResultIntent());
                getActivity().startActivityForResult(intent, 1001);
            }
        } else if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                resultInfo = data;
                AlertDialog.Builder ad = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.Theme_AppCompat_Dialog));

                ad.setTitle("타이틀 설정");       // 제목 설정
                ad.setMessage("해당 Plan의 이름을 정해주세요.");   // 내용 설정

                final EditText et = new EditText(getContext());
                et.setText(plan.title);
                ad.setView(et);

                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String msg = et.getText().toString();
                        String ifValue = ifIntentToSting(ifInfo);
                        String resultValue = resultIntentToSting(resultInfo);
                        int plano = data.getIntExtra("planno", -1);
                        Log.e("ifValue", ifValue);
                        Log.e("resultValue", resultValue);
                        @SuppressLint("StaticFieldLeak")
                        ConnectServer.ConnectServerDialogTask task = new ConnectServer.ConnectServerDialogTask(
                                getContext(),
                                "Plan수정중입니다...",
                                new FormBody.Builder()
                                        .add("email", ApplicationController.getEmailId())
                                        .add("planNo", plano + "")
                                        .add("ifValue", ifValue)
                                        .add("resultValue", resultValue)
                                        .add("msg", msg)
                                        .build(),
                                "modifyPlan.php") {
                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    if (jsonObject.getBoolean("result")) {
                                        Toast.makeText(getContext(), "성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getContext(), "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        task.execute();
                        dialog.dismiss();
                    }
                });

                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                ad.show();
            }
        }
    }
}