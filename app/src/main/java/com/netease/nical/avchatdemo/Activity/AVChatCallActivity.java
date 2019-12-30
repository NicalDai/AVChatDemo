package com.netease.nical.avchatdemo.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.netease.nical.avchatdemo.R;
import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.activity.AVChatActivity;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

import java.util.Arrays;
import java.util.List;

public class AVChatCallActivity extends AppCompatActivity {


    private Button StartRTSButton;  //发起白板会话的按钮
    private EditText toAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startcall);
        getSupportActionBar().hide(); //去除actionbar
        //初始化界面
        initView();

        StartRTSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toAccount = toAccountText.getText().toString();
                if(!toAccount.isEmpty()){
                    NimUserInfo user = NIMClient.getService(UserService.class).getUserInfo(toAccount);
                    if(user == null){
                        String[] array = {toAccount};
                        List<String> accounts = Arrays.asList(array);
                        NIMClient.getService(UserService.class).fetchUserInfo(accounts)
                                .setCallback(new RequestCallback<List<NimUserInfo>>() {
                                    @Override
                                    public void onSuccess(List<NimUserInfo> nimUserInfos) {
                                        if(!nimUserInfos.isEmpty()){
                                            NimUserInfo nimUserInfo = nimUserInfos.get(0);
                                            Log.d("RTSActivity", "onSuccess: 获取到"+nimUserInfo.getAccount()+"的云端资料");
                                            AVChatKit.outgoingCall(AVChatCallActivity.this,nimUserInfo.getAccount(),nimUserInfo.getName(),2, AVChatActivity.FROM_INTERNAL);
                                        }else {
                                            Toast.makeText(AVChatCallActivity.this, "请检查对方账号是否存在！", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailed(int i) {
                                        Log.e("RTSActivity", "onFailed: 获取云端好友资料失败,错误码："+i );
                                    }

                                    @Override
                                    public void onException(Throwable throwable) {

                                    }
                                });
                    }else {
                        AVChatKit.outgoingCall(AVChatCallActivity.this,user.getAccount(),user.getName(),2, AVChatActivity.FROM_INTERNAL);
                    }
                }
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView(){
        StartRTSButton = findViewById(R.id.startrts);
        toAccountText = findViewById(R.id.toaccountid);
    }


    /**
     * 点击空白位置 隐藏软键盘
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}
