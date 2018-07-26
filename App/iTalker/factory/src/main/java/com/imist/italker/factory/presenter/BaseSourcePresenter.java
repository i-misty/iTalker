package com.imist.italker.factory.presenter;


import com.imist.italker.factory.data.DataSource;
import com.imist.italker.factory.data.DbDataSource;

import java.util.List;

/**
 * 基础仓库源的presenter
 */
public abstract class BaseSourcePresenter<Data,ViewModel ,
        Source extends DbDataSource<Data>,
        View extends BaseContract.RecyclerView>
        extends BaseRecyclerPresenter<ViewModel ,View>
implements DataSource.SuccessCallback<List<Data>>{
    protected Source mSource;
    public BaseSourcePresenter(Source source ,View view) {
        super(view);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        mSource.load(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        mSource.dispose();
        mSource = null;
    }
}
