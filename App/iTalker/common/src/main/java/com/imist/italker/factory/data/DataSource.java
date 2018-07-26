package com.imist.italker.factory.data;

import android.support.annotation.StringRes;

/**
 * 数据源接口的定义
 */
public interface DataSource<T> {
    /**
     * 同时包括了成功与失败的回调接口
     * @param <T>
     */
    interface Callback<T> extends SuccessCallback<T>, FailedCallback {

    }

    /**
     * 只关注成功的接口
     *
     * @param <T>
     */
    interface SuccessCallback<T> {
        //数据加载成功
        void onDataLoaded(T data);
    }

    /**
     * 只关注失败的接口
     */
    interface FailedCallback {
        //数据加载失败
        void onDataNotAvailable(@StringRes int strRes);
    }

    /**
     * 销毁操作
     */
    void dispose();
}
