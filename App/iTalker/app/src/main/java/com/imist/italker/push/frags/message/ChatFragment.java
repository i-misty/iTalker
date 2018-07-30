package com.imist.italker.push.frags.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.imist.italker.common.app.PresenterFragment;
import com.imist.italker.common.widget.PortraitView;
import com.imist.italker.common.widget.adapter.TextWatcherAdapter;
import com.imist.italker.common.widget.recycler.RecyclerAdapter;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.User;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.factory.presenter.message.ChatContact;
import com.imist.italker.push.R;
import com.imist.italker.push.activities.MessageActivity;

import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.widget.Loading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContact.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContact.View<InitModel> {


    protected String mReceiverId;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    View mSubmit;

    protected Adapter mAdapter;


    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        initToolbar();
        initAppbar();
        initEditContent();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initData() {
        super.initData();
        //开始进行初始化操作
        mPresenter.start();
    }

    /**
     * 初始化toolbar
     */
    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    //给界面的appbar 设置一个监听，得到关闭与打开到时候的进度
    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    /**
     * 初始化编辑框监听
     */
    private void initEditContent() {
        mContent.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                mSubmit.setActivated(needSendMsg);

            }
        });
    }

    //这里可以实现也可以交给子类实现
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }

    @OnClick(R.id.btn_face)
    void onFaceClick() {

    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {

    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            //发送
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {

    }

    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {

    }

    /**
     * 内容的适配器
     */
    private class Adapter extends RecyclerAdapter<Message> {

        @Override
        protected int getItemViewType(int position, Message message) {

            //这里的消息中sender虽然为懒加载，但是主键是不为空的，不需要load(),但是获取内容需要load()
            //我发送的在右边，收到的在左边
            boolean isRignt = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {
                //文字内容；
                case Message.TYPE_STR: {
                    return isRignt ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                }
                //语音内容；
                case Message.TYPE_AUDIO: {
                    return isRignt ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                }
                //图片内容；
                case Message.TYPE_PIC: {
                    return isRignt ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;
                }
                //文件内容；
                case Message.TYPE_FILE:

                default: {
                    return isRignt ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
                }
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                //左右都是同一个
                case R.layout.cell_chat_text_left:
                case R.layout.cell_chat_text_right:
                    return new TextHolder(root);
                case R.layout.cell_chat_audio_left:
                case R.layout.cell_chat_audio_right:
                    return new AudioHolder(root);
                case R.layout.cell_chat_pic_left:
                case R.layout.cell_chat_pic_right:
                    return new PicHolder(root);
                //默认情况下，返回Text类型的Holder进行处理
                //文件的实现
                default:
                    return new TextHolder(root);
            }
        }
    }

    /**
     * 编译注解私有的话无法注入
     */
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R.id.im_portrait)
        PortraitView mPortrait;

        //允许为null,左边没有，右边有；
        @Nullable
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {

            User sender = message.getSender();
            //进行数据加载
            sender.load();
            //头像加载
            mPortrait.setup(Glide.with(ChatFragment.this), sender);
            if (mLoading != null) {
                //当前布局应该是在右边，
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    //正常状态,隐藏load
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    //正在发送中的状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.colorAccent));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    //发送失败的状态，失败允许重新发送
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(UiCompat.getColor(getResources(), R.color.alertImportant));
                }
                //当状态是错误状态时才允许点击
                mPortrait.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        @OnClick(R.id.im_portrait)
        void OnRePushClick() {
            if (mLoading != null) {
                //必须是右边才有可能需要重新发送
                //todo 重新发送
            }
        }
    }


    //文字的holder
    class TextHolder extends BaseHolder {

        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            //将内容设置到布局上
            mContent.setText(message.getContent());
        }
    }

    //audio的holder
    class AudioHolder extends BaseHolder {

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);

        }
    }

    //图片的holder
    class PicHolder extends BaseHolder {
        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
        }
    }
}