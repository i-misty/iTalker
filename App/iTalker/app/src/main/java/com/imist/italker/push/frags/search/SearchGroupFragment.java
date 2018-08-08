package com.imist.italker.push.frags.search;


import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imist.italker.common.app.Fragment;
import com.imist.italker.common.app.PresenterFragment;
import com.imist.italker.common.widget.EmptyView;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.common.widget.recycler.RecyclerAdapter;
import com.imist.italker.factory.model.card.GroupCard;
import com.imist.italker.factory.model.card.UserCard;
import com.imist.italker.factory.presenter.contact.FollowContract;
import com.imist.italker.factory.presenter.contact.FollowPresenter;
import com.imist.italker.factory.presenter.search.SeachGroupPresenter;
import com.imist.italker.factory.presenter.search.SearchContract;
import com.imist.italker.push.R;
import com.imist.italker.push.activities.PersonalActivity;
import com.imist.italker.push.activities.SearchActivity;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索群的界面
 */
public class SearchGroupFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchContract.GroupView, SearchActivity.SearchFragment {

    private RecyclerAdapter<GroupCard> mAdapter;

    @BindView(R.id.empty)
    EmptyView mEmptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    public SearchGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<GroupCard>() {
            @Override
            protected int getItemViewType(int position, GroupCard groupCard) {
                //返回cell的布局id
                return R.layout.cell_search_group_list;
            }

            @Override
            protected ViewHolder<GroupCard> onCreateViewHolder(View root, int viewType) {
                return new SearchGroupFragment.ViewHolder(root);
            }
        });
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }
    @Override
    protected void initData() {
        super.initData();
        //发起首次搜索
        search("");
    }

    @Override
    public void search(String content) {
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SeachGroupPresenter(this);
    }

    @Override
    public void onSearchDone(List<GroupCard> userCards) {
        //请求成功的情况下返回数据
        mAdapter.replace(userCards);
        //如果有数据则ok,没有数据则显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }


    /**
     * 每一个cell的布局操作
     * //Viewholder是每一项的item
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<GroupCard> {//没有基类得全部实现契约下View的所有方法

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_join)
        ImageView mJoin;
        private FollowContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(GroupCard groupCard) {
            mPortraitView.setup(Glide.with(SearchGroupFragment.this), groupCard.getPicture());
            mName.setText(groupCard.getName());
            //加入时间判断是否加入群
            mJoin.setEnabled(groupCard.getJoinAt() == null);
        }

        @OnClick(R.id.im_join)
        void onJoinClick(){
            //进入创建者个人界面
            PersonalActivity.show(getContext(),mData.getOwnerId());
        }
    }
}
