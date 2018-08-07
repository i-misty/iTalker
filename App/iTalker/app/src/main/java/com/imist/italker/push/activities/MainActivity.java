package com.imist.italker.push.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.imist.italker.common.app.Activity;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.assist.PermissionsFragment;
import com.imist.italker.push.frags.main.ActiveFragment;
import com.imist.italker.push.frags.main.ContactFragment;
import com.imist.italker.push.frags.main.GroupFragment;
import com.imist.italker.push.helper.NavHelper;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;

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

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavhelper;

    public static void show(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (Account.isComplete()) {
            //用户信息完全则走正常的流程
            return super.initArgs(bundle);
        } else {
            //
            UserActivity.show(this);
            return false;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        mNavhelper = new NavHelper<>(this, R.id.lay_container, getSupportFragmentManager(), this);
        mNavhelper.add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));


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
        PermissionsFragment.haveAll(this, getSupportFragmentManager());
    }

    @Override
    protected void initData() {
        super.initData();
        Menu menu = mNavigationView.getMenu();
        // 触发首次点击
        menu.performIdentifierAction(R.id.action_home, 0);

        //初始化头像加载
        mPortrait.setup(Glide.with(this), Account.getUser());
    }

    @OnClick(R.id.im_portrait)
    void OnPortraitClick() {
        PersonalActivity.show(this, Account.getUserId());
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        int type = Objects.equals(mNavhelper.getCurrentTab().extra, R.string.title_group) ?
                SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        SearchActivity.show(this, type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick() {
        //浮动按钮点击时，判断当前界面时群还是联系人的界面
        //如果是群，这打开创建群的界面
        if (Objects.equals(mNavhelper.getCurrentTab().extra, R.string.title_group)) {
            //打开群创建界面
            GroupCreateActivity.show(this);
        } else {
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }

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
        // 对悬浮按钮进行显示功能
        float transY = 0;
        float rotation = 0;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            //主界面隐藏
            transY = Ui.dipToPx(getResources(), 76);
        } else {
            //群界面
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                mAction.setImageResource(R.drawable.ic_group_add);
                rotation = -360;
            } else {
                //联系人界面
                mAction.setImageResource(R.drawable.ic_contact_add);
                rotation = 360;
            }
        }
        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(480)
                .start();

    }
}
