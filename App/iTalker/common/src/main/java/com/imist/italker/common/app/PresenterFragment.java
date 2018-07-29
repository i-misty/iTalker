package com.imist.italker.common.app;

import android.content.Context;

import com.imist.italker.factory.presenter.BaseContract;

public abstract class PresenterFragment<Presenter extends BaseContract.Presenter> extends Fragment
        implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //在界面onAttach 之后就触发初始化的Presenter
        initPresenter();
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        // 显示错误
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerError(str);
        } else {
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }
}
