package net.imist.web.italker.push.factory;

import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.api.base.PushModel;
import net.imist.web.italker.push.bean.card.GroupCard;
import net.imist.web.italker.push.bean.card.GroupMemberCard;
import net.imist.web.italker.push.bean.card.MessageCard;
import net.imist.web.italker.push.bean.card.UserCard;
import net.imist.web.italker.push.bean.db.*;
import net.imist.web.italker.push.utils.Hib;
import net.imist.web.italker.push.utils.PushDispatcher;
import net.imist.web.italker.push.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PushFactory {
    //发送一条消息，并在当前的发送历史记录中储存记录；
    public static void pushNewMessage(User sender, Message message) {
        if (sender == null || message == null)
            return;
        //消息卡片用于发送
        MessageCard card = new MessageCard(message);
        //要推送的字符串
        String entity = TextUtil.toJson(card);
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        if (message.getGroup() == null && Strings.isNullOrEmpty(message.getGroupId())) {
            //给朋友发送消息
            User receiver = UserFactory.findById(message.getReceiverId());
            if (receiver == null) {
                return;
            }
            //历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntityType(PushModel.ENTITY_TYPE_MESSAGE);
            history.setEntity(entity);
            history.setReceiver(receiver);
            //接收者当前的设备推送id;
            history.setReceiverPushId(receiver.getPushId());
            //推送的真实model
            PushModel pushModel = new PushModel();
            //每一条历史记录都是独立的，可以单独发送
            pushModel.add(history.getEntityType(), history.getEntity());
            //吧需要发送的数据丢给发送者进行发送；
            dispatcher.add(receiver, pushModel);
            //保存到数据库
            Hib.queryOnly(session -> session.save(history));
        } else {
            //因为延迟加载的情况可能为null,需要通过id查询；
            Group group = message.getGroup();
            if (group == null)
                group = GroupFactory.findById(message.getGroupId());
            //如果群真的没有，则返回
            if (group == null) {
                return;
            }
            //给群成员发送消息
            Set<GroupMember> members = GroupFactory.getMembers(group);
            if (members == null || members.size() == 0)
                return;
            //过滤我自己
            members = members.stream()
                    .filter(groupMember ->
                            !groupMember.getUserId().equalsIgnoreCase(sender.getId()))
                    .collect(Collectors.toSet());
            if (members.size() == 0)
                return;

            //一个历史记录列表
            List<PushHistory> histories = new ArrayList<>();

            addGroupMemberPushMoel(dispatcher, //推送的发送者
                    histories,  //数据库要存储的列表
                    members,   //所哟的成员
                    entity,
                    PushModel.ENTITY_TYPE_MESSAGE);
            Hib.queryOnly(session -> {
                        for (PushHistory history : histories) {
                            session.saveOrUpdate(history);
                        }
                    }
            );

        }
        //发送者进行真实的提交
        dispatcher.submit();
    }

    /**
     * 给群成员构建一个消息，
     * 把消息存储到数据库的历史记录中，每个人每个消息都是一条记录
     *
     * @param dispatcher
     * @param histories
     * @param members
     * @param entity
     * @param entityTypeMessage
     */
    private static void addGroupMemberPushMoel(PushDispatcher dispatcher, List<PushHistory> histories, Set<GroupMember> members, String entity, int entityTypeMessage) {
        for (GroupMember member : members) {
            User receiver = member.getUser();
            if (receiver == null)
                return;
            //历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntityType(entityTypeMessage);
            history.setEntity(entity);
            history.setReceiver(receiver);
            //接收者当前的设备推送id;
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());
            //添加到发送者的数据集中
            dispatcher.add(receiver, pushModel);
        }

    }

    /**
     * 给群成员发送已经被添加的消息
     *
     * @param members
     */
    public static void pushJoinGroup(Set<GroupMember> members) {
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        //一个历史记录列表
        List<PushHistory> histories = new ArrayList<>();
        for (GroupMember member : members) {
            User receiver = member.getUser();
            if (receiver == null)
                return;//?continue
            //每个成员的信息卡片
            GroupMemberCard memberCard = new GroupMemberCard(member);
            String entity = TextUtil.toJson(memberCard);

            //历史记录表字段建立
            PushHistory history = new PushHistory();
            history.setEntity(entity);
            history.setEntityType(PushModel.ENTITY_TYPE_ADD_GROUP);
            history.setReceiver(receiver);
            history.setReceiverPushId(receiver.getPushId());
            histories.add(history);
            //构建一个消息model
            PushModel pushModel = new PushModel();
            pushModel.add(history.getEntityType(), history.getEntity());
            //添加到发送者的数据集中
            dispatcher.add(receiver, pushModel);
        }
        Hib.queryOnly(session -> {
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });
        dispatcher.submit();
    }

    /**
     * 通知老成员有一系列新成员加入
     *
     * @param oldMembers
     * @param insertCards
     */
    public static void pushGroupMemberAdd(Set<GroupMember> oldMembers, List<GroupMemberCard> insertCards) {
        PushDispatcher dispatcher = new PushDispatcher();
        List<PushHistory> histories = new ArrayList<>();
        //当前新增的用户的集合的json字符串
        String entity = TextUtil.toJson(insertCards);
        //给每个人发送的消息都是一样的
        //进行循环添加给每一个老用户构建一条消息，消息的内容为新增的用户集合
        addGroupMemberPushMoel(dispatcher, histories, oldMembers, entity, PushModel.ENTITY_TYPE_ADD_GROUP_MEMBERS);
        //保存数据库
        Hib.queryOnly(session -> {
            for (PushHistory history : histories) {
                session.saveOrUpdate(history);
            }
        });
        //提交发送
        dispatcher.submit();
    }

    /**
     * 推送退出消息，
     *
     * @param receiver 接收者
     * @param pushId   这时刻接收者的pushid
     */
    public static void pushLogout(User receiver, String pushId) {
        PushHistory history = new PushHistory();
        history.setEntityType(PushModel.ENTITY_TYPE_LOGOUT);
        history.setEntity("Account Logout !");
        history.setReceiver(receiver);
        history.setReceiverPushId(pushId);//防止此时pushId已经改变，使用传入的参数

        //保存
        Hib.queryOnly(session -> session.save(history));
        //发送者
        PushDispatcher dispatcher = new PushDispatcher();
        PushModel pushModel = new PushModel()
                .add(history.getEntityType(), history.getEntity());
        //添加并且提交到第三方推送
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }

    /**
     * 给一个朋友推送一个我的信息过去，
     * 类型是我关注了他
     *
     * @param receiver
     * @param userCard
     */
    public static void pushFollow(User receiver, UserCard userCard) {
        //一定是相互关注了;
        userCard.setFollow(true);
        TextUtil.toJson(userCard);
        PushHistory history = new PushHistory();
        String entity = TextUtil.toJson(userCard);
        history.setEntityType(PushModel.ENTITY_TYPE_ADD_FRIEND);
        history.setEntity(entity);
        history.setReceiver(receiver);
        history.setReceiverPushId(receiver.getPushId());
        //保存到记录表
        Hib.queryOnly(session -> session.save(history));

        PushDispatcher dispatcher = new PushDispatcher();
        PushModel pushModel = new PushModel()
                .add(history.getEntityType(), history.getEntity());
        dispatcher.add(receiver, pushModel);
        dispatcher.submit();
    }
}
