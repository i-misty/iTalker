package com.imist.italker.factory.data;

import java.util.List;

/**
 * 基础的数据库数据源接口定义
 *
 * @param <Data>
 */
public interface DbDataSource<Data> extends DataSource {
    /**
     * 基础的数据库数据源加载方法
     *
     * @param callback 传递一个callback回调一般回调到presenter
     */
    void load(SuccessCallback<List<Data>> callback);
}
