package com.netease.nical.avchatdemo.AVChatInit;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;

public class AVUserinfoProvider {

    private NimUserInfo nimUserInfo;
    private String TAG = "AVUserinfoProvider";

    /**
     * 白板用户资料提供者初始化方法
     * @return
     */
    public static AVUserinfoProvider getInstance(){
        AVUserinfoProvider avUserinfoProvider= new AVUserinfoProvider();
        return avUserinfoProvider;
    }

    /**
     * 获取指定的用户资料，如果本地拿不到，就去拿云端的。
     * @param account
     * @return
     */
    public NimUserInfo getUserinfo(String account){
        //先拿本地数据库的用户信息
        nimUserInfo = NIMClient.getService(UserService.class).getUserInfo(account);
        //拿了再说，拿不到去拿云端的
        return nimUserInfo;
    }
}
