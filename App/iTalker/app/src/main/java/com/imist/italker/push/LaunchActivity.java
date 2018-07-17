package com.imist.italker.push;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import com.imist.italker.common.app.Activity;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.push.activities.AccountActivity;
import com.imist.italker.push.activities.MainActivity;
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
        // 拿到根布局
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
        //开始动画进入到50%
        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                //检查等待
                waitPushReceiverId();
            }
        });
    }

    /**
     * 等待个推框架对我们的PushId设置好值
     */
    private void waitPushReceiverId() {
        if (Account.isLogin()){
            //已经登陆的情况下，判断是否绑定
            if (Account.isBind()){
                skip();
                return;
            }
        }else {
            //没有登陆,
            //如果拿到了PushId,没有登陆是不能绑定PushId的
            if (!TextUtils.isEmpty(Account.getPushId())){
                skip();
            }
        }
        if (!TextUtils.isEmpty(Account.getPushId())) {
            //拿到了就进行跳转
            skip();
            return;
        }
        getWindow().getDecorView()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        waitPushReceiverId();
                    }
                }, 500);

    }

    private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                realSkip();
            }
        });
    }

    /**
     * 跳转的逻辑
     */
    private void realSkip() {
        //权限检测
        if (PermissionsFragment.haveAll(this, getSupportFragmentManager())) {
            //检查跳转到主页还是跳转到登陆
            if (Account.isLogin()){
                MainActivity.show(this);
            }else {
                AccountActivity.show(this);
            }
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startAnim(float endProgress, final Runnable endCallback) {
        //获取一个最终的颜色
        //int finalColor = Resource.Color.WHITE;
        int finalColor = UiCompat.getColor(getResources(), R.color.white);
        ArgbEvaluator evaluator = new ArgbEvaluator();
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);

        valueAnimator.setDuration(1500);
        //设置开始结束值
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //结束时触发
                endCallback.run();
            }
        });
        valueAnimator.start();
    }

    private final Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };

}
