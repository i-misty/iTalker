package com.imist.italker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imist.italker.common.app.PresenterToolbarActivity;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.common.widget.recycler.RecyclerAdapter;
import com.imist.italker.factory.model.db.view.MemberUserModel;
import com.imist.italker.factory.presenter.group.GroupMemberContract;
import com.imist.italker.factory.presenter.group.GroupMemberPresenter;
import com.imist.italker.push.R;
import com.imist.italker.push.frags.group.GroupMemberAddFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class GroupMemberActivity extends PresenterToolbarActivity<GroupMemberContract.Presenter>
        implements GroupMemberContract.View ,GroupMemberAddFragment.Callback {
    private static final String KEY_GROUP_ID = "KEY_GROUP_ID";
    private static final String KEY_GROUP_ADMIN = "KEY_GROUP_ADMIN";


    @BindView(R.id.recycler)
    RecyclerView mRecycler;

    private RecyclerAdapter<MemberUserModel> mAdapter;

    private String mGroupID;
    private boolean isAdmin;

    public static void show(Context context,String groupId){
        show(context,groupId,false);
    }
    public static void showAdmin(Context context,String groupId){
        show(context,groupId,true);
    }

    public static void show(Context context,String groupId,boolean isAdmin){
        if (TextUtils.isEmpty(groupId))
            return;
        Intent intent = new Intent(context, GroupMemberActivity.class);
        intent.putExtra(KEY_GROUP_ID,groupId);
        intent.putExtra(KEY_GROUP_ADMIN,isAdmin);
        context.startActivity(intent);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        this.mGroupID = bundle.getString(KEY_GROUP_ID);
        this.isAdmin = bundle.getBoolean(KEY_GROUP_ADMIN);
        return !TextUtils.isEmpty(this.mGroupID);
    }

    @Override
    public String getGroupId() {
        return mGroupID;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_group_member;
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle(R.string.title_member_list);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter = new RecyclerAdapter<MemberUserModel>() {
            @Override
            protected int getItemViewType(int position, MemberUserModel model) {
                //这里复用创建群聊的布局
                return R.layout.cell_group_create_contact;
            }

            @Override
            protected ViewHolder<MemberUserModel> onCreateViewHolder(View root, int viewType) {
                //创建一个内部类的ViewHolder
                return new GroupMemberActivity.ViewHolder(root);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        // 开始数据刷新
        mPresenter.refresh();

        // 显示管理员界面，添加成员
        if (isAdmin) {
            new GroupMemberAddFragment()
                    .show(getSupportFragmentManager(), GroupMemberAddFragment.class.getName());
        }
    }

    @Override
    public RecyclerAdapter<MemberUserModel> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 隐藏Loading就好
        hideLoading();
    }

    @Override
    protected GroupMemberContract.Presenter initPresenter() {
        return new GroupMemberPresenter(this);
    }


    @Override
    public void hideLoading() {
        super.hideLoading();
    }

    @Override
    public void refreshMembers() {
        // 重新加载成员信息
        if (mPresenter != null)
            mPresenter.refresh();
    }

    class ViewHolder extends RecyclerAdapter.ViewHolder<MemberUserModel> {
        @BindView(R.id.im_portrait)
        PortraitView mPprtrait;
        @BindView(R.id.txt_name)
        TextView mName;

        ViewHolder(View itemView) {
            super(itemView);
            //创建的时候就直接隐藏，防止重复操作
            itemView.findViewById(R.id.cb_select).setVisibility(View.GONE);
        }

        @Override
        protected void onBind(MemberUserModel model) {
            mPprtrait.setup(Glide.with(GroupMemberActivity.this),model.portrait);
            mName.setText(model.name);
        }
        @OnClick(R.id.im_portrait)
        void onPortraitClick(){
            PersonalActivity.show(GroupMemberActivity.this,mData.userId);
        }
    }
}
