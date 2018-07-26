package com.imist.italker.factory.model.db;


import com.imist.italker.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 我们App中基础的一个BaseDbModel
 * 继承了数据库框架DbFlow中的基础类
 * 同时定义了我们需要的方法
 */
public abstract class BaseDbModel<Model> extends BaseModel
        implements DiffUiDataCallback.UiDataDiffer<Model> {
}
