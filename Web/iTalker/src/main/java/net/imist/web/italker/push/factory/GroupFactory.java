package net.imist.web.italker.push.factory;

import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.api.group.GroupCreateModel;
import net.imist.web.italker.push.bean.db.Group;
import net.imist.web.italker.push.bean.db.GroupMember;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.utils.Hib;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupFactory {

    //通过ID拿到群model
    public static Group findById(String groupId) {

        return Hib.query(session -> session.get(Group.class,groupId));
    }
    //查询一个群，同时这个人必须是群的成员
    public static Group findById(User user , String groupId) {
        //查询一个群，同时该User必须为群的成员，否则返回null
        GroupMember member = getMember(user.getId(),groupId);
        if (member != null){
            return member.getGroup();
        }
        return null;
    }

    /**
     * 通过名字查找群
     * @param name
     * @return
     */
    public static Group findByName(String name) {
        return Hib.query(session -> (Group) session.createQuery("from Group where lower(name) =:name")
                    .setParameter("name", name.toLowerCase())
                    .uniqueResult());
    }
    /**
     * //查询一个群的成员
     * @param group
     * @return
     */
    public static Set<GroupMember> getMembers(Group group) {

        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where group =:group")
                .setParameter("group",group)
                .list();
            return new HashSet<>(members);
        });
    }

    /**
     * 获取一个人加入的所有群；
     * @param user
     * @return
     */
    public static Set<GroupMember> getMembers(User user) {

        return Hib.query(session -> {
            @SuppressWarnings("unchecked")
            List<GroupMember> members = session.createQuery("from GroupMember where userId =:userId")
                    .setParameter("userId",user.getId())
                    .list();
            return new HashSet<>(members);
        });
    }

    //创建群；
    public static Group create(User creator, GroupCreateModel model, List<User> users) {
        return Hib.query(session -> {
            Group group = new Group(creator,model);
            session.save(group);
            GroupMember ownerMember = new GroupMember(creator,group);
            //设置超级权限创建者；
            ownerMember.setPermissionType(GroupMember.PERMISSION_TYPE_ADMIN_SU);
            session.save(ownerMember);
            for (User user : users) {
                GroupMember member = new GroupMember(user,group);
                session.save(member);
            }
            //session.flush();//刷新缓冲区重新加载，这里不是很着急
            //session.load(group,group.getId());
            return group;

        });
    }

    public static GroupMember getMember(String userId, String groupId) {
        return Hib.query(session ->
        {
            return (GroupMember) session.createQuery("from GroupMember where userId = :userId and groupId = :groupId")
                    .setParameter("userId", userId)
                    .setParameter("groupId", groupId)
                    .setMaxResults(1)
                    .uniqueResult();
        });
    }

    /**
     * 根据群名搜索群列表
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Group> search(String name) {
        if (Strings.isNullOrEmpty(name)){
            name = "";
        }
        final String searchName = "%" + name+"%";

        //这里报过错，查询的返回值在session强转，否则session关闭外部强转报错（(List<Group>)）Hib.query（...）
        return Hib.query(session -> (List<Group>)session.createQuery("from Group where lower(name) like :name")
                .setParameter("name",searchName)
                .setMaxResults(20)
                .list());

    }

    //给群添加用户
    public static Set<GroupMember> addMembers(Group group, List<User> insertUsers) {
        return Hib.query(session -> {
            Set<GroupMember> members = new HashSet<>();
            for (User user : insertUsers) {
                GroupMember groupMember = new GroupMember(user,group);
                //保存，没有提交到数据库
                session.save(groupMember);
                members.add(groupMember);
            }//这里只是进行保存，没有进行关联查询那么外键为null，添加的内容没有外键

            //进行数据刷新，会进行关联查询，在循环中消耗过高
            /*for (GroupMember member : members) {
                session.refresh(member);
            }*/
            return members;
        });
    }
}
