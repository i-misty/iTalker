package com.imist.italker.common.app;


import android.app.ProgressDialog;
import android.content.DialogInterface;

import com.imist.italker.common.R;
import com.imist.italker.factory.presenter.BaseContract;

public abstract class PresenterToolbarActivity<Presenter extends BaseContract.Presenter>
        extends ToolbarActivity implements BaseContract.View<Presenter> {

    protected Presenter mPresenter;
    protected ProgressDialog mLoadingDialog;

    @Override
    protected void initBefore() {
        super.initBefore();
        //初始化 presenter;
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.destroy();
        }
    }

    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        hideLoading();
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
        }else {
            ProgressDialog dialog = mLoadingDialog;
            if (dialog == null){
                dialog = new ProgressDialog(this,R.style.AppTheme_Dialog_Alert_Light);
                dialog.setCanceledOnTouchOutside(false);
                //强制取消关闭界面
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });
                mLoadingDialog = dialog;
            }
            dialog.setMessage(getText(R.string.prompt_loading));
            dialog.show();
        }
    }

    protected void hideDialogLoading(){
        ProgressDialog dialog = mLoadingDialog;
        if (dialog != null){
            mLoadingDialog = null;
            dialog.dismiss();
        }
    }
    /**
     * 隐藏loading
     */
    protected void hideLoading() {
        //无论是否有占位布局都隐藏
       hideDialogLoading();
        if (mPlaceHolderView != null) {
            mPlaceHolderView.triggerOk();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        mPresenter = presenter;
    }
}
