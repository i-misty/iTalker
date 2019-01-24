package com.imist.italker.push;

import android.content.Context;

import com.igexin.sdk.PushManager;
import com.imist.italker.common.app.Application;
import com.imist.italker.factory.Factory;
import com.imist.italker.push.activities.AccountActivity;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 调用Factory进行初始化
        Factory.setup();
        // 推送进行初始化
        PushManager.getInstance().initialize(this);
    }

    //登录界面实现
    @Override
    protected void showAccountView(Context context) {
        AccountActivity.show(context);
    }
}
