package com.imist.italker.push.frags.main;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imist.italker.common.app.PresenterFragment;
import com.imist.italker.common.widget.EmptyView;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.common.widget.recycler.RecyclerAdapter;
import com.imist.italker.factory.model.db.Group;


import com.imist.italker.factory.presenter.group.GroupContract;
import com.imist.italker.factory.presenter.group.GroupsPresenter;
import com.imist.italker.push.R;
import com.imist.italker.push.activities.MessageActivity;

import butterknife.BindView;


public class GroupFragment extends PresenterFragment<GroupContract.Presenter>
        implements GroupContract.View {
    @BindView(R.id.empty)
    EmptyView mEmptyView;

    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    //适配器User，可以直接从数据库查询数据
    private RecyclerAdapter<Group> mAdapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        // 初始化Recycler
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        /**
         * 这里的DBflow一定得是 api引入，可传递的，不然该调用模块无法识别
         * 继承了BaseModel 的数据库实体类导致编译不通过，原因就在于DBflow也是编译时注解，
         * 一定得注意项目权限的问题；因此在使用编译时注解的时候一定得注意
         */
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<Group>() {
            @Override
            protected int getItemViewType(int position, Group group) {
                return R.layout.cell_group_list;
            }

            @Override
            protected ViewHolder<Group> onCreateViewHolder(View root, int viewType) {
                return new GroupFragment.ViewHolder(root);
            }
        });

        mAdapter.setListener(new RecyclerAdapter.AdapterListenerImpl<Group>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Group group) {
                MessageActivity.show(getContext(), group);
            }
        });
        // 初始化占位布局
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);

    }

    @Override
    protected void onFirstInit() {
        super.onFirstInit();
        //进行一次数据加载
        mPresenter.start();
    }

    @Override
    protected GroupContract.Presenter initPresenter() {
        return new GroupsPresenter(this);
    }

    @Override
    public RecyclerAdapter<Group> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    /**
     * ViewHolder 这里不可以私有，因为butterknife 是编译时注解，所以需要具备包访问权限；
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<Group> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @BindView(R.id.txt_name)
        TextView mName;

        @BindView(R.id.txt_desc)
        TextView mDesc;

        @BindView(R.id.txt_member)
        TextView mMember;


        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Group group) {
            //Glide.with(ContactFragment.this)绑定Fragment,随生命周期影响优化加载
            mPortraitView.setup(Glide.with(GroupFragment.this), group.getPicture());
            mName.setText(group.getName());
            mDesc.setText(group.getDesc());

            if (group.holder != null && group.holder instanceof String) {
                mMember.setText((String) group.holder);
            } else {
                mMember.setText("");
            }
        }
    }
}
