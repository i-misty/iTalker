package com.imist.italker.common.app;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;

public abstract class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindows();
        if(initArgs(getIntent().getExtras())){
            int layoutId = getContentLayoutId();
            setContentView(layoutId);
            initWidget();
            initData();
        }else {
            finish();
        }
    }

    /**
     * 初始化窗口
     */
    protected void initWindows(){

    }

    /**
     * 初始化参数
     * @param bundle 参数bundle
     * @return 初始化正确返回true，错误返回false
     */
    protected boolean initArgs(Bundle bundle){
        return true;
    }
    /**
     * 得到当前界面资源文件的id
     * @return
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化控件
     */
    protected void initWidget(){
        ButterKnife.bind(this);
    }

    /**
     * 初始化数据
     */
    protected void initData(){

    }

    @Override
    public boolean onSupportNavigateUp() {
        //当点击界面导航返回时 ，finish当前界面
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        @SuppressLint("RestrictedApi")
        List<android.support.v4.app.Fragment> fragments = getSupportFragmentManager().getFragments();
        //判断是否为null
        if(fragments != null && fragments.size() > 0){
            for (Fragment fragment : fragments){
                //判断是否是我们能够处理的Fragment类型
                if(fragment instanceof com.imist.italker.common.app.Fragment){
                    //判断是否拦截了返回按钮
                    if (((com.imist.italker.common.app.Fragment) fragment).onBackPressed()){
                        //如果有直接返回
                        return;
                    }
                }

            }
        }
        super.onBackPressed();
        finish();
    }
}
