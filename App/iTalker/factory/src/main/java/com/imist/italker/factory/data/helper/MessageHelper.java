package com.imist.italker.factory.data.helper;

import com.imist.italker.factory.Factory;
import com.imist.italker.factory.model.api.RspModel;
import com.imist.italker.factory.model.api.message.MsgCreateModel;
import com.imist.italker.factory.model.card.MessageCard;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.Message_Table;
import com.imist.italker.factory.net.Network;
import com.imist.italker.factory.net.RemoteService;
import com.raizlabs.android.dbflow.sql.language.Operator;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageHelper {
    //从本地找消息
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    //发送时异步调用的
    public static void push(final MsgCreateModel model) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                //如果是一个已经发送过的消息，则不能重新发送；
                //正在发送状态，如果一个消息正在发送，则不能重新发送
                Message message = findFromLocal(model.getId());
                if (message != null && message.getStatus() != Message.STATUS_FAILED)
                    return;
                //todo 如果是文件类型的（语音，图片，文件）需要先上传后发送

                //我们在发送的时候需要通知界面更新状态 Card；
                final MessageCard card = model.buildCard();
                Factory.getMessageCenter().dispatch(card);

                //直接发送进行网络调度
                RemoteService service = Network.remote();
                service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if (rspModel != null && rspModel.success()) {
                            MessageCard rspCard = rspModel.getResult();
                            if (rspCard != null) {
                                //成功的调度
                                Factory.getMessageCenter().dispatch(rspCard);
                            }
                        } else {
                            //检查是否账户异常
                            Factory.decodeRspCode(rspModel, null);
                            //走失败的流程
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMessageCenter().dispatch(card);
                    }
                });
            }
        });
    }

    /**
     * 查询一个消息，这个消息是一个群中的最后一条消息；
     * @param groupId
     * @return 群中聊天的最后一条消息；
     */
    public static Message findLastWithGroup(String groupId) {

        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt,false)
                .querySingle();
    }

    /**
     * 和一个人的最后一条聊天消息s
     * @param userId
     * @return
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt,false)//倒序查询
                .querySingle();
    }
}
