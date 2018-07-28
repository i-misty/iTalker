package net.imist.web.italker.push.factory;

import net.imist.web.italker.push.bean.db.Group;
import net.imist.web.italker.push.bean.db.GroupMember;
import net.imist.web.italker.push.bean.db.User;

import java.util.Set;

public class GroupFactory {


    public static Group findById(String groupId) {
        return null;
    }
    public static Group findById(User user , String groupId) {
        //查询一个群，同时该User必须为群的成员，否则返回null
        return null;
    }

    public static Set<GroupMember> getMembers(Group group) {
        return null;
    }
}
