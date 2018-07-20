package com.imist.italker.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.imist.italker.common.widget.convention.PlaceHolderView;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class Fragment extends android.support.v4.app.Fragment {
    protected View mRoot;
    protected Unbinder mRootUnBinder;
    protected PlaceHolderView mPlaceHolderView;

    //标识第一次初始化数据
    protected boolean mIsFirstInitData = true;

    //被添加到Activity时最先调用
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initArgs(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mRoot == null){
            int layId = getContentLayoutId();
            //初始化当前的根布局,但是不在创建时添加到 container里面
            View root = inflater.inflate(layId,container,false);
            initWidget(root);
            mRoot = root;
        }else {
            if (mRoot.getParent() != null){
                ((ViewGroup)mRoot.getParent()).removeView(mRoot);
            }
        }
        //return super.onCreateView(inflater, container, savedInstanceState);
        return mRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstInitData){
            mIsFirstInitData = false;
            onFirstInit();
        }
        //当view创建完成之后初始化数据
        initData();
    }

    /**
     * 得到当前界面资源文件的id
     * @return
     */
    protected abstract int getContentLayoutId();

    /**
     * 初始化参数
     * @param bundle 参数bundle
     */
    protected void initArgs(Bundle bundle){

    }
    /**
     * 初始化控件
     * @param root
     */
    protected void initWidget(View root){
        mRootUnBinder = ButterKnife.bind(this,root);
    }

    /**
     * 初始化数据
     */
    protected void initData(){

    }
    /**
     * 初始化首次数据
     */
    protected void onFirstInit(){

    }

    /**
     * 返回按键出发时调用
     * @return 返回true 代表我已经处理，activity不用自己finish
     */
    public boolean onBackPressed(){
        return false;
    }

    /**
     * 设置占位布局
     * @param placeHolderView
     */
    public void setPlaceHolderView(PlaceHolderView placeHolderView){
        this.mPlaceHolderView = placeHolderView;
    }
}
