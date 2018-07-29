package com.imist.italker.factory.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * 数据库数据于网络数据比较
 */

/**
 * Realm 查询的数据比较大可以直接显示，但是不可以比较，callback回调显示数据不可以跨线程
 * 而比较是比较耗时的操作，取决于数据量和比较的逻辑
 */
public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>> extends DiffUtil.Callback {
    private List<T> mOldList, mNewList;

    public DiffUiDataCallback(List<T> mOldList, List<T> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    //旧的数据大小
    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    //新的数据大小
    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    //比较两个类是否就是同一个东西，比如id相同的东西；
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldList.get(oldItemPosition);
        T beanNew = mNewList.get(newItemPosition);

        return beanNew.isSame(beanOld);
    }

    //在经过相等判断后，进一步判断是否有数据更改；
    //比如，同一个用户的两个不同实例，其中name字段不同;
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldList.get(oldItemPosition);
        T beanNew = mNewList.get(newItemPosition);
        return beanNew.isUiContentSame(beanOld);
    }

    //进行比较的数据类型;
    //泛型的目的，和相同的数据类型，数据model进行比较
    public interface UiDataDiffer<T> {
        //传递一个旧的数据，判断是否标识为同一数据
        boolean isSame(T old);

        //和你旧的数据进行对比，内容是否相同
        boolean isUiContentSame(T old);
    }
}
