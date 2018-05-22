package com.teamsix.doitplan;

import android.Manifest;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.teamsix.doitplan.background.AlarmBraodCastReciever;
import com.teamsix.doitplan.background.AlarmUtils;
import com.teamsix.doitplan.background.BetteryReceiver;
import com.teamsix.doitplan.background.BlockCallReceiver;
import com.teamsix.doitplan.background.CallStateReceiver;
import com.teamsix.doitplan.background.ClipboardService;
import com.teamsix.doitplan.background.Forecast;
import com.teamsix.doitplan.background.ForecastBraodCastReciever;
import com.teamsix.doitplan.background.GPStracker;
import com.teamsix.doitplan.fragment.NewRecyclerViewFragment;
import com.teamsix.doitplan.fragment.RecyclerViewFragment;
import com.teamsix.doitplan.fragment.SmallRecyclerViewFragment;

import java.sql.Date;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.teamsix.doitplan.background.ForecastBraodCastReciever.forecast;

public class MainActivity extends DrawerActivity {

    //뷰 연결
    @BindView(R.id.materialViewPager)
    MaterialViewPager mViewPager;//
    NewRecyclerViewFragment newRecyclerViewFragment;
    SmallRecyclerViewFragment smallRecyclerViewFragment;
    private final BroadcastReceiver mBloackCallReceiver = new BlockCallReceiver();
    private final BetteryReceiver mBetteryReceiver = new BetteryReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");
        ButterKnife.bind(this);

        //툴바 설정
        final Toolbar toolbar = mViewPager.getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        }


        //뷰설정
        mViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            //각 탭별 프래그먼트 반환
            @Override
            public Fragment getItem(int position) {
                switch (position % 3) {
                    case 0:
                        return RecyclerViewFragment.newInstance();//타인의Plan
                    case 1:
                        newRecyclerViewFragment = NewRecyclerViewFragment.newInstance();
                        return newRecyclerViewFragment;//새로운Plan
                    case 2:
                        smallRecyclerViewFragment = SmallRecyclerViewFragment.newInstance();
                        return smallRecyclerViewFragment;//나만의Plan
                    default:
                        return RecyclerViewFragment.newInstance();
                }
            }

            //총 탭의 개수
            @Override
            public int getCount() {
                return 3;
            }

            //각 탭의 이름
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position % 3) {
                    case 0:
                        return "타인의 Plan";
                    case 1:
                        return "새로운 Plan";
                    case 2:
                        return "나만의 Plan";
                }
                return "";
            }
        });

        //각 탭에 해당하는 색상 및 그림 연결
        mViewPager.setMaterialViewPagerListener(new MaterialViewPager.Listener() {
            @Override
            public HeaderDesign getHeaderDesign(int page) {
                switch (page) {
                    case 0:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,//타인의Plan배경
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                    case 1:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.blue,//새로운Plan
                                "http://www.hdiphonewallpapers.us/phone-wallpapers/540x960-1/540x960-mobile-wallpapers-hd-2218x5ox3.jpg");
                    case 2:
                        return HeaderDesign.fromColorResAndUrl(
                                R.color.cyan,//나만의Plan
                                "http://www.droid-life.com/wp-content/uploads/2014/10/lollipop-wallpapers10.jpg");
                }

                //execute others actions if needed (ex : modify your header logo)

                return null;
            }
        });

        mViewPager.getViewPager().setOffscreenPageLimit(mViewPager.getViewPager().getAdapter().getCount());
        mViewPager.getPagerTitleStrip().setViewPager(mViewPager.getViewPager());

        final View logo = findViewById(R.id.logo_white);
        if (logo != null) {
            logo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), ForecastBraodCastReciever.forecast.getSky()+"/"+ForecastBraodCastReciever.forecast.getState()+"/"+ForecastBraodCastReciever.forecast.getTemperature(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        PermissionUtils.requestPermission(this,123,Manifest.permission.ACCESS_FINE_LOCATION);

        //전화상태 확인 및 변경 서비스
        registerReceiver(mBloackCallReceiver , new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED));

        //베터리 상태 체크
        registerReceiver(mBetteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //날씨 상태 확인
        if (!ForecastBraodCastReciever.isLaunched) {
            AlarmUtils.getInstance().startForecastUpdate(this);
            if(GPStracker.lastLocation==null)
                forecast.getNowData(this,35.154483, 128.098444);
            else
                forecast.getNowData(this, GPStracker.lastLocation.getLatitude(), GPStracker.lastLocation.getLongitude());
        }

        //클립보드 서비스 시작
        if(!ClipboardService.isLaunched) {
            Intent mIntent = new Intent(getApplicationContext(), ClipboardService.class);
            startService(mIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("onActivityResult",requestCode+"");
        if(requestCode==1) {
            if (resultCode == RESULT_OK) {
                newRecyclerViewFragment.onActivityResult(requestCode, resultCode, data);
                super.onActivityResult(requestCode, resultCode, data);
            }
        }else if(requestCode == 1000 || requestCode == 1001){
            if (resultCode == RESULT_OK) {
                smallRecyclerViewFragment.onActivityResult(requestCode, resultCode, data);
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        Log.e("onActivityResult","MainActivity-end");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBloackCallReceiver);
        unregisterReceiver(mBetteryReceiver);
    }
}