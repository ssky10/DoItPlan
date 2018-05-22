package com.teamsix.doitplan.kakao;

import android.util.Log;

import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class KakaoSelfSend {

    public void requestSendMemo(String msg) {
        Log.e("KakaoSelfSend","start");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("''yy년 MM월 dd일 E요일");
        KakaoTalkMessageBuilder builder = new KakaoTalkMessageBuilder();
        builder.addParam("msg", msg+"\n"+sdf.format(date));
        builder.addParam("title", "DoitPlan에서 보낸 메시지 입니다.");

        KakaoTalkService.getInstance().requestSendMemo(new KakaoTalkResponseCallback<Boolean>() {
                                                           @Override
                                                           public void onSuccess(Boolean result) {
                                                               Logger.d("send message to my chatroom : " + result);
                                                           }
                                                       }
                , "9537" // templateId
                , builder.build());
    }

    public class KakaoTalkMessageBuilder {
        public Map<String, String> messageParams = new HashMap<String, String>();

        public KakaoTalkMessageBuilder addParam(String key, String value) {
            messageParams.put("${" + key + "}", value);
            return this;
        }

        public Map<String, String> build() {
            return messageParams;
        }
    }

    private abstract class KakaoTalkResponseCallback<T> extends TalkResponseCallback<T> {
        @Override
        public void onNotKakaoTalkUser() {
            Logger.w("not a KakaoTalk user");
        }

        @Override
        public void onFailure(ErrorResult errorResult) {
            Logger.e("failure : " + errorResult);
        }

        @Override
        public void onSessionClosed(ErrorResult errorResult) {
            //redirectLoginActivity();
        }

        @Override
        public void onNotSignedUp() {
            //redirectSignupActivity();
        }
    }
}
