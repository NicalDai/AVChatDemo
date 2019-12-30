package com.netease.nical.avchatdemo.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.netease.nical.avchatdemo.DemoCache;
import com.netease.nical.avchatdemo.Login.CustomBoolean;
import com.netease.nical.avchatdemo.Login.DataSaveToLocal;
import com.netease.nical.avchatdemo.Login.EditTextClearTools;
import com.netease.nical.avchatdemo.MD5.MD5;
import com.netease.nical.avchatdemo.NimSDKOptionConfig;
import com.netease.nical.avchatdemo.R;
import com.netease.nical.avchatdemo.permission.MPermission;
import com.netease.nical.avchatdemo.permission.annotation.OnMPermissionDenied;
import com.netease.nical.avchatdemo.permission.annotation.OnMPermissionGranted;
import com.netease.nical.avchatdemo.permission.annotation.OnMPermissionNeverAskAgain;
import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.constant.TeamTypeEnum;
import com.netease.nimlib.sdk.team.constant.VerifyTypeEnum;
import com.netease.nimlib.sdk.team.model.CreateTeamResult;

import java.io.Serializable;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private Button Login_OK;    //登陆按钮
    private EditText account;   //账号输入框
    private EditText password;  //密码输入框
    private static final int BASIC_PERMISSION_REQUEST_CODE = 100;
    private String token;
    private String Appkey;
    private CheckBox needRecordPasswordCB;
    private Boolean needRecordPassword = false;
    private DataSaveToLocal dataSaveToLocal = new DataSaveToLocal();
    private CustomBoolean needMD5 = new CustomBoolean(false); //token是否需要MD5（仅仅针对demo的appkey而言），如果是本地文件读取的，就不需要MD5
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide(); //去除actionbar
        Appkey = getAppKey();
        filePath = NimSDKOptionConfig.getAppCacheDir(getApplicationContext())+"logininfo.txt";
        //权限信息
        authorityManage();
        //初始化界面
        initView();
        //获取基本权限（相机，麦克风等）
        requestBasicPermission();

        //保存按钮点击事件
        needRecordPasswordCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    needRecordPassword = true;
                }else {
                    needRecordPassword = false;
                }
            }
        });

        Login_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String accid = account.getText().toString();
                //判断是否为demo的appkey，是就加MD5，不是就直接透传
                if(Appkey.equals("45c6af3c98409b18a84451215d0bdd6e") && needMD5.getB()){

                    token = MD5.getStringMD5(password.getText().toString());
                }else {
                    token = password.getText().toString();
                }
                //实例化logininfo，执行登陆
                LoginInfo loginInfo = new LoginInfo(accid,token);
                doLogin(loginInfo);
            }
        });

    }

    private LoginInfo getLoginInfo(){
        String loginData = dataSaveToLocal.readDataToLocal(NimSDKOptionConfig.getAppCacheDir(getApplicationContext())+"logininfo.txt");
        if(!loginData.isEmpty()){
            int index1 = loginData.indexOf(",");
            String accid = loginData.substring(0,index1);
            String token = loginData.substring(index1+1,loginData.length());
            LoginInfo loginInfo = new LoginInfo(accid,token);
            return loginInfo;
        }else {
            return null;
        }
    }

    /**
     * 界面初始化
     */
    private void initView(){

        Login_OK = (Button) findViewById(R.id.Login_OK);
        account = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);

        ImageView unameClear = (ImageView) findViewById(R.id.iv_unameClear);
        ImageView pwdClear = (ImageView) findViewById(R.id.iv_pwdClear);

        //配置点击按钮清除EditText的内容
        EditTextClearTools.addClearListener(account,unameClear,needMD5);
        EditTextClearTools.addClearListener(password,pwdClear,needMD5);

        //从本地获取之前存储的logininfo
        LoginInfo loginInfo =  getLoginInfo();

        if(loginInfo != null){
            needMD5.setB(false);    //如果是demo 的appkey，从本地文件中获取的密码，已经是md5之后的结果。
            account.setText(loginInfo.getAccount());
            password.setText(loginInfo.getToken());
        }
        //记住密码的勾选框
        needRecordPasswordCB = findViewById(R.id.cb_checkbox);
    }

    /**
     * 权限申请
     */
    private void authorityManage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }else {
                Toast.makeText(this, "权限已申请", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 基本权限管理
     */
    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private void requestBasicPermission() {
        MPermission.printMPermissionResult(true, this, BASIC_PERMISSIONS);
        MPermission.with(LoginActivity.this)
                .setRequestCode(BASIC_PERMISSION_REQUEST_CODE)
                .permissions(BASIC_PERMISSIONS)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionSuccess() {
        try {
            Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }

    @OnMPermissionDenied(BASIC_PERMISSION_REQUEST_CODE)
    @OnMPermissionNeverAskAgain(BASIC_PERMISSION_REQUEST_CODE)
    public void onBasicPermissionFailed() {
        try {
            Toast.makeText(this, "未全部授权，部分功能可能无法正常运行！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MPermission.printMPermissionResult(false, this, BASIC_PERMISSIONS);
    }


    /**
     * 执行登陆
     * @param loginInfo
     */
    private void doLogin(LoginInfo loginInfo){
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();
                DemoCache.setContext(getApplicationContext());
                AVChatKit.setAccount(loginInfo.getAccount());
                //判断是否需要本地存储密码(CheckBox打钩)
                if (needRecordPassword){
                    dataSaveToLocal.saveDataToLocal(loginInfo.getAccount()+","+loginInfo.getToken(),filePath);
                }else {//删除文件
                    dataSaveToLocal.deleteFile(filePath);
                }
                //跳转到拨打界面
                Intent intent = new Intent(LoginActivity.this,AVChatCallActivity.class);
                startActivity(intent);
                finish();


            }

            @Override
            public void onFailed(int i) {
                Toast.makeText(LoginActivity.this, "登陆失败，错误码："+i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onException(Throwable throwable) {
                Toast.makeText(LoginActivity.this, "登陆异常，" + throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        NIMClient.getService(AuthService.class).login(loginInfo).setCallback(callback);
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


    /**
     * 取出清单文件的appkey，用作MD5比较
     * @return
     */
    public String getAppKey() {
        String keyString = "";
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            keyString = appInfo.metaData.getString("com.netease.nim.appKey");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return keyString;
    }

    private void createTeam(){
        // 群组类型
        TeamTypeEnum type = TeamTypeEnum.Advanced;
// 创建时可以预设群组的一些相关属性，如果是普通群，仅群名有效。
// fields 中，key 为数据字段，value 对对应的值，该值类型必须和 field 中定义的 fieldType 一致
        HashMap<TeamFieldEnum, Serializable> fields = new HashMap<TeamFieldEnum, Serializable>();
        fields.put(TeamFieldEnum.Name, "ceshi");
//        fields.put(TeamFieldEnum.Introduce, teamIntroduce);
        fields.put(TeamFieldEnum.VerifyType, VerifyTypeEnum.Free);
        NIMClient.getService(TeamService.class).createTeam(fields, type, "", null)
                .setCallback(new RequestCallback<CreateTeamResult>() {
                    @Override
                    public void onSuccess(CreateTeamResult createTeamResult) {
                        Log.d("创建群组", "onSuccess: 成功");
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.d("创建群组", "onFailed: " + i);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        Log.d("创建群组", "onException: "+ throwable.toString());
                    }
                });
    }
}
