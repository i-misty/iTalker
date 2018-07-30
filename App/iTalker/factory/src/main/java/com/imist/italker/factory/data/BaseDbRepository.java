package com.imist.italker.factory.data;

import android.support.annotation.NonNull;

import com.imist.italker.factory.data.helper.DbHelper;
import com.imist.italker.factory.model.db.BaseDbModel;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.utils.CollectionUtil;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;


import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements DbDataSource<Data>,
        DbHelper.ChangedListener<Data>,
        QueryTransaction.QueryResultListCallback<Data> {

    //和presenter交互的回调
    private SuccessCallback<List<Data>> callback;

    private final List<Data> dataList = new LinkedList<>();//当前缓存的数据

    private Class<Data> dataClass; //当前泛型对应的真实Class信息

    @SuppressWarnings("unchecked")
    public BaseDbRepository() {
        //那当前类的泛型数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SuccessCallback<List<Data>> callback) {
        this.callback = callback;
        //进行数据库监听
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        //取消监听销毁数据
        this.callback = null;
        DbHelper.removeChangeListener(dataClass, this);
        dataList.clear();
    }

    //数据库统一通知的地方 ：增加/更改
    @Override
    public void onDataSave(Data[] list) {
        boolean isChanged = false;
        for (Data data : list) {
            //是关注的人但是不是我自己
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        //有数据变更，则进行界面刷新
        if (isChanged) {
            notifyDataChange();
        }
    }

    //数据库统一通知的地方 ：删除
    @Override
    public void onDataDelete(Data[] list) {
        //再删除情况下不用进行过滤判断
        boolean isChanged = false;
        //数据库删除的操作
        for (Data data : list) {
            if (dataList.remove(data))
                isChanged = true;
        }

        // 有数据变更，则进行界面刷新
        if (isChanged)
            notifyDataChange();

    }

    //DbFlow框架通知的回调
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        //数据库加载数据成功
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();
            return;
        }
        //转变为数组
        Data[] users = CollectionUtil.toArray(tResult, dataClass);
        //回到数据集更新的操作中
        onDataSave(users);
    }

    //插入或者更新
    private void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {
            replace(index, data);
        } else {
            insert(data);
        }
    }

    //更新操作，更新某个坐标下的数据
    private void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    private void insert(Data data) {
        dataList.add(data);
    }

    private int indexOf(Data newData) {
        int index = -1;
        for (Data data : dataList) {
            index++;
            if (data.isSame(newData)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * 检查一个User 是否是我需要关注的数据
     *
     * @param data
     * @return true 是我需要关注的数据
     */
    protected abstract boolean isRequired(Data data);

    /**
     * 添加数据库的监听操作
     */
    protected void registerDbChangedListener() {
        DbHelper.addChangeListener(dataClass, this);
    }

    //通知界面刷新的方法
    private void notifyDataChange() {
        SuccessCallback<List<Data>> callback = this.callback;
        if (callback != null) {
            callback.onDataLoaded(dataList);
        }
    }
}
