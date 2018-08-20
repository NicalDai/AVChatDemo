package com.netease.nical.avchatdemo;

import android.app.Application;
import android.content.Context;

import com.netease.nical.avchatdemo.AVChatInit.AVUserinfoProvider;
import com.netease.nim.avchatkit.AVChatKit;
import com.netease.nim.avchatkit.config.AVChatOptions;
import com.netease.nim.avchatkit.model.ITeamDataProvider;
import com.netease.nim.avchatkit.model.IUserInfoProvider;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.netease.nimlib.sdk.util.NIMUtil;

public class MainApplicaiton extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NIMClient.init(this,null,null);
        if(NIMUtil.isMainProcess(this)){
            initAVChatKit();
        }
    }

    /**
     * 初始化AVChatkit
     */
    private void initAVChatKit() {

        AVChatKit.setContext(getApplicationContext());

        AVChatOptions avChatOptions = new AVChatOptions(){
            @Override
            public void logout(Context context) {
//                MainActivity.logout(context, true);
            }
        };
        avChatOptions.entranceActivity = MainActivity.class;
        avChatOptions.notificationIconRes = R.mipmap.ic_stat_notify_msg;
        AVChatKit.init(avChatOptions);

        // 初始化日志系统
//        LogHelper.init();
        // 设置用户相关资料提供者
        AVChatKit.setUserInfoProvider(new IUserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                UserInfo userInfo =  AVUserinfoProvider.getInstance().getUserinfo(account);
                if (userInfo == null) {
                    //如果本地获取不到这个人的用户资料
                }
                return userInfo;
            }

            @Override
            public String getUserDisplayName(String account) {
                UserInfo userInfo =  AVUserinfoProvider.getInstance().getUserinfo(account);
                if (userInfo == null) {
                    return "未知用户";
                }else {
                    return userInfo.getName();
                }
            }
        });

        /**
         * 群组信息提供者
        */

        AVChatKit.setTeamDataProvider(new ITeamDataProvider() {
            @Override
            public String getDisplayNameWithoutMe(String teamId, String account) {
                return account; //开发者可以自行用户名展示逻辑
            }

            @Override
            public String getTeamMemberDisplayName(String teamId, String account) {
                return account; //开发者可以自行用户名展示逻辑
            }
        });

        }
}
