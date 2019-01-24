package com.imist.italker.common.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.imist.italker.common.callback.LifecycleCallbacks;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Application extends android.app.Application {

    private static Application instance;
    private static List<Activity> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        registerActivityLifecycleCallbacks(new LifecycleCallbacks(){
            @Override
            public void onActivityCreated(android.app.Activity activity, Bundle bundle) {
                activities.add(activity);
            }

            @Override
            public void onActivityDestroyed(android.app.Activity activity) {
                activities.remove(activity);
            }
        });
    }

    public void finishAll(){
        for (Activity activity : activities) {
            activity.finish();
        }
        //跳转到账号界面
        showAccountView(this);
    }

    //这里因为依赖顺序的关系，无法直接跳转到账号管理界面通过子类复写实现
    protected void showAccountView(Context context) {

    }

    /**
     * 外部获取单例
     *
     * @return
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * 获取当前app的缓存文件夹地址
     *
     * @return
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    public static File getPortraitTmpFile() {
        File dir = new File(getCacheDirFile(), "portrait");
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }

    public static File getAudioTmpFile(boolean isTmp) {
        File dir = new File(getCacheDirFile(), "audio");
        dir.mkdir();
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }
        File path = new File(getCacheDirFile(), isTmp ? "tmp.mp3" : SystemClock.uptimeMillis() + ".mp3");
        return path.getAbsoluteFile();
    }

    public static void showToast(final String msg) {
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里进行回调的时候就一定是主线程
                Toast.makeText(instance, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToast(@StringRes int msgId) {
        showToast(instance.getString(msgId));
    }
}
