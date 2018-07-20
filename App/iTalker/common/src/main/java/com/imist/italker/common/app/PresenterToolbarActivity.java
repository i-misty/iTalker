package com.imist.italker.common.app;


import com.imist.italker.factory.presenter.BaseContract;

public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;

    @Override
    protected void initBefore() {
        super.initBefore();
        //初始化 presenter;
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.destroy();
        }
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        // 显示错误
        if (mPlaceHolderView != null){
            mPlaceHolderView.triggerError(str);
        }else {
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (mPlaceHolderView != null){
            mPlaceHolderView.triggerLoading();
        }
    }

    /**
     * 隐藏loading
     */
    protected void hideLoading(){
        if (mPlaceHolderView != null){
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }
}
