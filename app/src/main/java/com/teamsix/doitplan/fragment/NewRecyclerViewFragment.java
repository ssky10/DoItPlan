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
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.github.florent37.materialviewpager.header.MaterialViewPagerHeaderDecorator;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.ConnectServer;
import com.teamsix.doitplan.LoginActivity;
import com.teamsix.doitplan.MainActivity;
import com.teamsix.doitplan.NewRecyclerViewAdapter;
import com.teamsix.doitplan.Plan;
import com.teamsix.doitplan.R;
import com.teamsix.doitplan.IfListActivity;
import com.teamsix.doitplan.ResultListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
public class NewRecyclerViewFragment extends Fragment {

    private static final boolean GRID_LAYOUT = false;
    private static final int ITEM_COUNT = 1;

    NewRecyclerViewAdapter newRecyclerViewAdapter;
    Intent ifInfo = null;
    Intent resultInfo = null;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    public static NewRecyclerViewFragment newInstance() {
        return new NewRecyclerViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final List<Intent> items = new ArrayList<>();

        items.add(new Intent(getActivity(), IfListActivity.class));
        items.add(new Intent(getActivity(), ResultListActivity.class));

        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setEnabled(false);


        //setup materialviewpager
        if (GRID_LAYOUT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
        mRecyclerView.setHasFixedSize(true);

        //Use this now
        mRecyclerView.addItemDecoration(new MaterialViewPagerHeaderDecorator());

        newRecyclerViewAdapter = new NewRecyclerViewAdapter(items);
        mRecyclerView.setAdapter(newRecyclerViewAdapter);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult","NewRecyclerViewFragment-start");
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
                String type = data.getStringExtra("type");
                Log.e("type", type);
                if (type.equals("if")) {
                    newRecyclerViewAdapter.setIfStr(true, Plan.IF_STR[data.getIntExtra("if", 0)]);
                    ifInfo = data;
                } else if (type.equals("Result")) {
                    newRecyclerViewAdapter.setResultStr(true, Plan.RESULT_STR[data.getIntExtra("Result", 0)]);
                    resultInfo = data;
                    AlertDialog.Builder ad = new AlertDialog.Builder(new ContextThemeWrapper(NewRecyclerViewFragment.this.getContext(), R.style.Theme_AppCompat_Dialog));

                    ad.setTitle("타이틀 설정");       // 제목 설정
                    ad.setMessage("해당 Plan의 이름을 정해주세요.");   // 내용 설정

                    final EditText et = new EditText(NewRecyclerViewFragment.this.getContext());
                    et.setText(Plan.IF_STRLONG[ifInfo.getIntExtra("if", 0)] + Plan.RESULT_STRLONG[resultInfo.getIntExtra("Result", 0)]);
                    ad.setView(et);

                    ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            @SuppressLint("StaticFieldLeak")
                            String msg = et.getText().toString();
                            int ifCode = ifInfo.getIntExtra("if", 0);
                            String ifValue = ifIntentToSting(ifInfo);
                            String resultValue = resultIntentToSting(resultInfo);
                            Log.e("ifValue",ifValue);
                            Log.e("resultValue",resultValue);
                            int resultCode = resultInfo.getIntExtra("Result", 0);
                            @SuppressLint("StaticFieldLeak")
                            ConnectServer.ConnectServerDialogTask task = new ConnectServer.ConnectServerDialogTask(
                                    NewRecyclerViewFragment.this.getContext(),
                                    "Plan등록중입니다...",
                                    new FormBody.Builder()
                                            .add("email", ApplicationController.getEmailId())
                                            .add("ifCode", ifCode+"")
                                            .add("resultCode", resultCode+"")
                                            .add("ifValue",ifValue)
                                            .add("resultValue",resultValue)
                                            .add("msg",msg)
                                            .build(),
                                    "newPlan.php") {
                                @Override
                                protected void onPostExecute(String result) {
                                    super.onPostExecute(result);
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        if (jsonObject.getBoolean("result")) {
                                            Toast.makeText(getContext(), "성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                            newRecyclerViewAdapter.setDefault();
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
                Log.e("onActivityResult", "NewRecyclerViewFragment-end");
            }
        }
    }
}