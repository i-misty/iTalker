package com.imist.italker.factory.data.group;

import com.imist.italker.factory.data.helper.DbHelper;
import com.imist.italker.factory.data.helper.GroupHelper;
import com.imist.italker.factory.data.helper.UserHelper;
import com.imist.italker.factory.model.card.GroupCard;
import com.imist.italker.factory.model.card.GroupMemberCard;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.GroupMember;
import com.imist.italker.factory.model.db.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GroupDispatcher implements GroupCenter {
    private static GroupCenter instance;
    //单线程池，处理卡片一个一个消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static GroupCenter instance() {
        if (instance == null) {
            synchronized (GroupDispatcher.class) {
                if (instance == null) {
                    instance = new GroupDispatcher();
                }
            }
        }
        return instance;
    }

    @Override
    public void dispatch(GroupCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        executor.execute(new GroupHandler(cards));
    }

    @Override
    public void dispatch(GroupMemberCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        executor.execute(new GroupMenberRspHandler(cards));
    }

    private class GroupMenberRspHandler implements Runnable {
        private final GroupMemberCard[] cards;

        public GroupMenberRspHandler(GroupMemberCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<GroupMember> members = new ArrayList<>();
            for (GroupMemberCard model : cards) {
                //成员对应的人的信息；
                User user = UserHelper.searchFirstLocal(model.getUserId());
                //成员对于的群信息
                Group group = GroupHelper.find(model.getGroupId());
                if (user != null && group != null) {
                    GroupMember member = model.build(group, user);
                    members.add(member);
                }
                if (members.size() > 0) {
                    DbHelper.save(GroupMember.class, members.toArray(new GroupMember[0]));
                }
            }
        }
    }

    private class GroupHandler implements Runnable {
        private final GroupCard[] groupCards;

        public GroupHandler(GroupCard[] groupCards) {
            this.groupCards = groupCards;
        }

        @Override
        public void run() {
            List<Group> groups = new ArrayList<>();
            for (GroupCard card : groupCards) {
                //搜索管理员
                User owner = UserHelper.searchFirstLocal(card.getOwnerId());
                if (owner != null) {
                    Group group = card.build(owner);
                    groups.add(group);
                }
            }
            if (groups.size() > 0) {
                DbHelper.save(Group.class, groups.toArray(new Group[0]));
            }

        }
    }


}
