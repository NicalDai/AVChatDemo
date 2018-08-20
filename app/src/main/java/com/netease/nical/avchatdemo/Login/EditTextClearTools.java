package com.netease.nical.avchatdemo.Login;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Nical on 2018.07.23
 */

public class EditTextClearTools {

    public static void addClearListener(final EditText et , final ImageView iv ,final CustomBoolean b){

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(before == 0 && count == 32){
                    b.setB(false);  //场景是从文件中直接获取到了MD5的密码结果，这时密码不需要MD5
                }else {
                    b.setB(true);   //其他的场景，可以判断为手动输入的密码，如果Appkey是demo的需要MD5
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果有输入内容长度大于0那么显示clear按钮
                String str = s + "" ;
                if (s.length() > 0){
                    iv.setVisibility(View.VISIBLE);
                }else{
                    iv.setVisibility(View.INVISIBLE);
                }
            }
        });


        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });
    }
}
