package com.teamsix.doitplan.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.teamsix.doitplan.ApplicationController;
import com.teamsix.doitplan.Plan;
import com.teamsix.doitplan.background.AlarmUtils;
import com.teamsix.doitplan.background.SendNotification;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private int notificationID = 0;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());


// Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if(remoteMessage.getData().get("type").equals("noti")){
                SendNotification.sendNotification(this,remoteMessage.getData().get("title"),remoteMessage.getData().get("message"));
            }else if(remoteMessage.getData().get("type").equals("newplan")){
                JSONObject planJson = null;
                try {
                    planJson = new JSONObject(remoteMessage.getData().get("message"));
                    Plan plan = new Plan();
                    plan.planNo = planJson.getInt("plan_no");
                    plan.title = planJson.getString("msg");
                    plan.ifCode = planJson.getInt("if_code");
                    plan.ifValue = planJson.getString("if_value");
                    plan.resultCode = planJson.getInt("that_code");
                    plan.resultValue = planJson.getString("that_value");
                    plan.setIsShareFormInt(planJson.getInt("is_share"));
                    plan.likes = planJson.getInt("likes_num");
                    plan.setIsWorkFormInt(1);
                    ApplicationController.writePlanDB(plan);
                    SendNotification.sendNotification(this,"새로운 Plan 추가",remoteMessage.getData().get("title")+"가 추가되었습니다.");
                    if(plan.ifCode == Plan.IF_TIME){
                        AlarmUtils.getInstance().startAlarmUpdate(getApplicationContext(),plan.planNo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(remoteMessage.getData().get("type").equals("delplan")){
                JSONObject planJson = null;
                try {
                    planJson = new JSONObject(remoteMessage.getData().get("message"));
                    ApplicationController.deletePlanDB(planJson.getInt("plan_no"));
                    SendNotification.sendNotification(this,"Plan 삭제",remoteMessage.getData().get("title")+"가 삭제되었습니다.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

// Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            SendNotification.sendNotification(this,remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
    }

}