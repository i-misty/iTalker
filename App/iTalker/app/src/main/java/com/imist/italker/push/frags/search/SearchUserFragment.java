package com.imist.italker.push.frags.search;



import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imist.italker.common.app.PresenterFragment;
import com.imist.italker.common.widget.EmptyView;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.common.widget.recycler.RecyclerAdapter;
import com.imist.italker.factory.model.card.UserCard;
import com.imist.italker.factory.presenter.user.SearchContract;
import com.imist.italker.factory.presenter.user.SearchUserPresenter;
import com.imist.italker.push.R;
import com.imist.italker.push.activities.SearchActivity;

import java.util.List;

import butterknife.BindView;

/**
 * 搜索用户界面，实现SearchActivity.SearchFragment接口
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter>
        implements SearchContract.UserView ,SearchActivity.SearchFragment{

    @BindView(R.id.empty)
    EmptyView mEmptyView;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<UserCard> mAdapter;

    public SearchUserFragment() {
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        //初始化Recycler
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {
            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                //返回cell的布局id
                return R.layout.cell_search_list;
            }
            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });
        mEmptyView.bind(mRecycler);
        setPlaceHolderView(mEmptyView);
    }
    @Override
    protected void initData() {
        super.initData();
        //发起首次搜索
        //search("");
    }
    @Override
    public void search(String content) {
        //Activity -> Fragment ->Presenter ->Net
        mPresenter.search(content);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        return new SearchUserPresenter(this);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        //请求成功的情况下返回数据
        mAdapter.replace(userCards);
        //如果有数据则ok,没有数据则显示空布局
        mPlaceHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    /**
     * 每一个cell的布局操作
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>{

        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_follow)
        ImageView mFollow;
        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(UserCard userCard) {
            Glide.with(SearchUserFragment.this)
                    .load(userCard.getPortrait())
                    .centerCrop()
                    .into(mPortraitView);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }
    }
}
