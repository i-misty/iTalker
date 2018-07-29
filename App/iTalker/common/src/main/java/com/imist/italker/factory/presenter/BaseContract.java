package com.imist.italker.factory.presenter;

import android.support.annotation.StringRes;

import com.imist.italker.common.widget.recycler.RecyclerAdapter;

/**
 * MVP 模式中公共的基本契约
 *
 * @param
 */
public interface BaseContract {
    //基本的界面职责
    interface View<T extends BaseContract.Presenter> {
        // 公共的：显示一个字符串错误
        void showError(@StringRes int str);

        // 公共的：显示进度条
        void showLoading();

        // 支持设置一个Presenter
        void setPresenter(T presenter);
    }

    //基本的Presenter职责
    interface Presenter {
        // 共用的开始触发
        void start();

        // 共用的销毁触发
        void destroy();
    }

    interface RecyclerView<T extends Presenter, ViewMode> extends View<T> {

        //这样拿到整个用户列表，刷新整条列表会导致界面卡顿，闪烁，不适用于界面长期驻留的情况
        //void  onDone(List<User> users);

        //拿到适配器进行自主的触发
        RecyclerAdapter<ViewMode> getRecyclerAdapter();

        //当适配器数据改变的时候触发
        void onAdapterDataChanged();
    }
}
