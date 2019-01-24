package com.imist.italker.factory.presenter.group;

import com.imist.italker.factory.model.Author;
import com.imist.italker.factory.presenter.BaseContract;

public interface GroupCreateContract {
    interface Presenter extends BaseContract.Presenter {
        //创建
        void create(String name, String desc, String picture);

        //更改一个model的选中状态
        void changeSelect(ViewModel model, boolean isSelect);
    }

    interface View extends BaseContract.RecyclerView<Presenter, ViewModel> {

        void onCreateSuccessed();

    }

    class ViewModel {
        //用户信息
        public Author author;

        public boolean isSelect;

    }
}
