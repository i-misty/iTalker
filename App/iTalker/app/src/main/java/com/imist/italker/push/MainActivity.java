package com.imist.italker.push;


import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.imist.italker.common.app.Activity;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.push.helper.NavHelper;

import butterknife.BindView;
import butterknife.OnClick;


public class MainActivity extends Activity
        implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.onTabChangeListener<Integer> {
    @BindView(R.id.appbar)
    View mLayAppbar;
    @BindView(R.id.im_portrait)
    PortraitView mPortrait;
    @BindView(R.id.txt_title)
    TextView mTittle;
    @BindView(R.id.lay_container)
    FrameLayout mConTainner;
    @BindView(R.id.navigation)
    BottomNavigationView mNavigationView;

    private NavHelper<Integer> mNavhelper;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mNavhelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(), this);

        //添加对底部按钮点击的监听
        mNavigationView.setOnNavigationItemSelectedListener(this);
        Glide.with(this).load(R.drawable.bg_src_morning)
                .centerCrop()
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick() {

    }

    @OnClick(R.id.btn_action)
    void onActionClick() {

    }

    /**
     * 当底部按钮被点击时触发
     *
     * @param item
     * @return 返回true代表已经处理
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        return mNavhelper.performClickMenu(item.getItemId());
    }


    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        //从额外字段中取出id
        mTittle.setText(newTab.extra);
    }
}
