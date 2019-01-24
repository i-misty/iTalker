package com.imist.italker.factory.presenter.message;

import android.support.v7.util.DiffUtil;
import android.text.TextUtils;

import com.imist.italker.factory.data.helper.MessageHelper;
import com.imist.italker.factory.data.message.MessageDataSource;
import com.imist.italker.factory.model.api.message.MsgCreateModel;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.persistence.Account;
import com.imist.italker.factory.presenter.BaseSourcePresenter;
import com.imist.italker.factory.utils.DiffUiDataCallback;

import java.util.List;

public class ChatPresenter<View extends ChatContact.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContact.Presenter {

    //群或者联系人的id
    protected String mReceiverId;
    //区分是群还是联系人
    protected int mReceiveType;

    public ChatPresenter(MessageDataSource source, View view, String mReceiverId, int mReceiveType) {
        super(source, view);
        this.mReceiverId = mReceiverId;
        this.mReceiveType = mReceiveType;
    }


    @Override
    public void pushText(String content) {
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiveType)
                .content(content, Message.TYPE_STR)
                .build();
        MessageHelper.push(model);
    }

    //发送语音
    public void pushAudio(String path, long time) {
        if(TextUtils.isEmpty(path)){
            return;
        }

        // 构建一个新的消息
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiveType)
                .content(path, Message.TYPE_AUDIO)
                .attach(String.valueOf(time))
                .build();

        // 进行网络发送
        MessageHelper.push(model);
    }


    @Override
    public void pushImage(String[] paths) {
        //发送图片
        if (paths == null || paths.length == 0)
            return;
        //此时路径是本地的手机上的路径
        for (String path : paths) {
            //构建一个新的消息
            MsgCreateModel model = new MsgCreateModel.Builder()
                    .receiver(mReceiverId, mReceiveType)
                    .content(path, Message.TYPE_PIC)
                    .build();
            //进行网络发送
            MessageHelper.push(model);
        }
    }

    @Override
    public boolean rePush(Message message) {
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED) {
            message.setStatus(Message.STATUS_CREATED);
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContact.View view = getView();
        if (view == null)
            return;
        //拿到老数据
        List<Message> old = view.getRecyclerAdapter().getItems();
        //差异计算
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        //进行界面刷新
        refreshData(result, messages);
    }
}
