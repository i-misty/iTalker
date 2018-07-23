package com.imist.italker.factory.data.message;

import android.text.TextUtils;

import com.imist.italker.factory.data.helper.GroupHelper;
import com.imist.italker.factory.data.helper.MessageHelper;
import com.imist.italker.factory.data.helper.UserHelper;
import com.imist.italker.factory.data.user.UserCenter;
import com.imist.italker.factory.data.user.UserDispatcher;
import com.imist.italker.factory.model.card.MessageCard;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MessageDispatcher implements MessageCenter{

    private static MessageCenter instance;
    //单线程池，处理卡片一个一个消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MessageCenter instance(){
        if (instance == null){
            synchronized (MessageDispatcher.class){
                if (instance == null){
                    instance = new MessageDispatcher();
                }
            }
        }
        return instance;
    }
    @Override
    public void dispatch(MessageCard... cards) {

        if (cards == null || cards.length == 0){
            return;
        }
        executor.execute(new MessageCardHandler(cards));
    }

    private class MessageCardHandler implements Runnable{

        private final MessageCard[] cards;

        public MessageCardHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            //遍历card
            for (MessageCard card : cards){
                //卡片基础信息过滤，错误卡片直接过滤
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        ||TextUtils.isEmpty(card.getId())
                        ||TextUtils.isEmpty(card.getReceiverId())
                        && TextUtils.isEmpty(card.getGroupId()))
                    continue;
                //消息卡片可能是推送来的，也有可能是自己造的；
                //推送来的代表服务器一定有，我们一定可以查询到（本地有可能有，有可能没有）
                //如果是直接造的，那么先存储本地然后发送网络
                //发送流程 写消息 --> 存储本地->发送网络->网络返回->刷新本地状态

                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null){
                    //消息本身字段从发送之后就不变化了，如果收到了消息
                    //本地有，同时本地显示消息状态为完成状态，则不必处理；因为此时回来的消息和本地一模一样
                    //如果本地消息显示已经完成则不作处理
                    if (message.getStatus() == Message.STATUS_DONE)
                        continue;
                    //新状态为完成才更新服务器时间，不然不做更新
                    if (card.getStatus()== Message.STATUS_DONE){
                        //代表网络发送成功，此时需要修改时间为服务器时间
                        message.setCreateAt(card.getCreateAt());
                        //如果没有进入判断，则代表这个消息发送失败了
                        //重新进入数据库更新而已
                    }
                    //更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    message.setStatus(card.getStatus());
                }else {
                    //没有找到本地消息，初次在数据库存储
                    User sender = UserHelper.searchFirstLocal(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if (!TextUtils.isEmpty(card.getReceiverId())){
                        receiver = UserHelper.searchFirstLocal(card.getReceiverId());
                    }else if (!TextUtils.isEmpty(card.getGroupId())){
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    //接受者总有一个
                    if (receiver == null && group == null && sender == null)
                        continue;
                    message = card.build(sender,receiver,group);
                }
                messages.add(message);
            }
        }
    }
}
