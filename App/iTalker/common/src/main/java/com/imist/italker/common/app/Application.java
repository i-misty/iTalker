package com.imist.italker.common.app;

import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;

public class Application extends android.app.Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     *  外部获取单例
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

    public static void showToast(final String msg){
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里进行回调的时候就一定是主线程
                Toast.makeText(instance,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showToast(@StringRes int msgId){
        showToast(instance.getString(msgId));
    }
}
