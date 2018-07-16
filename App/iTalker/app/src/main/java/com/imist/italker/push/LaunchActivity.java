package com.imist.italker.push;


import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.imist.italker.common.app.Activity;
import com.imist.italker.push.frags.assist.PermissionsFragment;

import net.qiujuer.genius.ui.compat.UiCompat;

public class LaunchActivity extends Activity {
    // Drawable
    private ColorDrawable mBgDrawable;


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 拿到跟布局
        View root = findViewById(R.id.activity_launch);
        // 获取颜色
        int color = UiCompat.getColor(getResources(), R.color.colorPrimary);
        // 创建一个Drawable
        ColorDrawable drawable = new ColorDrawable(color);
        // 设置给背景
        root.setBackground(drawable);
        mBgDrawable = drawable;

    }

    @Override
    protected void initData() {
        super.initData();

        // 动画进入到50%等待PushId获取到
       /* startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                // 检查等待状态
                waitPushReceiverId();
            }
        });*/
    }

    /**
     * 等待个推框架对我们的PushId设置好值
     */
   /* private void waitPushReceiverId() {
        if (Account.isLogin()) {
            // 已经登录情况下，判断是否绑定
            // 如果没有绑定则等待广播接收器进行绑定
            if (Account.isBind()) {
                skip();
                return;
            }
        } else {
            // 没有登录
            // 如果拿到了PushId, 没有登录是不能绑定PushId的
            if (!TextUtils.isEmpty(Account.getPushId())) {
                // 跳转
                skip();
                return;
            }
        }

        // 循环等待
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                }, 500);
    }*/


    /**
     * 在跳转之前需要把剩下的50%进行完成
     */
 /*   private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });
    }*/

    /**
     * 真实的跳转
     */
    private void reallySkip() {
        // 权限检测，跳转
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            // 检查跳转到主页还是登录
          /*  if (Account.isLogin()) {
                MainActivity.show(this);
            } else {
                AccountActivity.show(this);
            }*/
            finish();
        }
    }
}
