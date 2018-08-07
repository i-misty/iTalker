package net.imist.web.italker.push.service;

import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.api.base.ResponseModel;
import net.imist.web.italker.push.bean.api.group.GroupCreateModel;
import net.imist.web.italker.push.bean.api.group.GroupMemberAddModel;
import net.imist.web.italker.push.bean.api.group.GroupMemberUpdateModel;
import net.imist.web.italker.push.bean.card.ApplyCard;
import net.imist.web.italker.push.bean.card.GroupCard;
import net.imist.web.italker.push.bean.card.GroupMemberCard;
import net.imist.web.italker.push.bean.db.Group;
import net.imist.web.italker.push.bean.db.GroupMember;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.factory.GroupFactory;
import net.imist.web.italker.push.factory.PushFactory;
import net.imist.web.italker.push.factory.UserFactory;
import net.imist.web.italker.push.provider.LocalDateTimeConverter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/group")
public class GroupServices extends BaseService{


    /**
     * 创建群
     * @param model
     * @return 群信息
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> create(GroupCreateModel model){
        if (!GroupCreateModel.check(model)){
            return ResponseModel.buildParameterError();
        }
        User creator = getSelf();
        model.getUsers().remove(creator.getId());
        if (model.getUsers().size() == 0)
            return ResponseModel.buildParameterError();
        //检查是否已有
        if (GroupFactory.findByName(model.getName()) != null){
            return ResponseModel.buildHaveNameError();
        }
        List<User> users = new ArrayList<>();
        for (String s : model.getUsers()){
            User user = UserFactory.findById(s);
            if (user == null)
                continue;
            users.add(user);
        }
        //没有一个成员
        if (users.size() == 0){
            return ResponseModel.buildParameterError();
        }
        Group group = GroupFactory.create(creator,model,users);
        if (group == null){
            return ResponseModel.buildServiceError();
        }
        //拿到群的成员，给所有的群成员发送消息已经被添加到群的信息；
        GroupMember createMember = GroupFactory.getMember(creator.getId(),group.getId());
        if (createMember == null){
            //服务器异常
            return ResponseModel.buildServiceError();
        }
        //拿到群的成员，给所有的群成员发送消息，已经被添加到群的信息
        Set<GroupMember> members = GroupFactory.getMembers(group);
        if (members == null){
            //服务器异常
            return ResponseModel.buildServiceError();
        }
        members = members.stream()
                .filter(groupMember -> !groupMember.getUserId().equalsIgnoreCase(createMember.getUserId()))
                .collect(Collectors.toSet());
        //开始发起推送
        PushFactory.pushJoinGroup(members);

        return ResponseModel.buildOk(new GroupCard(createMember));

    }

    /**
     * 查找群，没有传递参数就是搜索最近所有群
     * @param groupName
     * @return
     */
    @GET
    @Path("/search/{name:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> search(@PathParam("name") @DefaultValue("") String groupName){

        //这里通过多次查询获取
        User self = getSelf();
        List<Group> groups = GroupFactory.search(groupName);
        if (groups != null && groups.size() > 0){
            List<GroupCard> groupCards = groups.stream().map(group -> {
                GroupMember member = GroupFactory.getMember(self.getId(),group.getId());
                return new GroupCard(group,member);
            }).collect(Collectors.toList());
            return ResponseModel.buildOk(groupCards);
        }
        return ResponseModel.buildOk();
    }

    /**
     * 拉取某个时间段之后的好友
     * @param dataStr
     * @return
     */
    @GET
    @Path("/list/{date:(.*)?}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupCard>> list(@DefaultValue("") @PathParam("date") String dataStr){
        User self = getSelf();
        LocalDateTime dateTime = null;
        if (!Strings.isNullOrEmpty(dataStr)){
            dateTime = LocalDateTime.parse(dataStr,LocalDateTimeConverter.FORMATTER);
        }
        Set<GroupMember> members = GroupFactory.getMembers(self);
        if (members == null || members.size() == 0){
            return ResponseModel.buildOk();
        }
        final LocalDateTime finalDatatime = dateTime;
        List<GroupCard> groupCards = members.stream()
                //时间如果为null则不做限制，否则拉取最后时间之后的
                .filter(groupMember -> finalDatatime == null || groupMember.getUpdateAt().isAfter(finalDatatime))
                .map(GroupCard::new)
                .collect(Collectors.toList());
        return ResponseModel.buildOk(groupCards);
    }

    /**
     * 获取一个群的信息,你必须是这群的成员
     * @param groupId
     * @return
     */
    @GET
    @Path("/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String groupId) {
        if (Strings.isNullOrEmpty(groupId))
            return ResponseModel.buildParameterError();
        User user = getSelf();
        GroupMember member = GroupFactory.getMember(user.getId(),groupId);
        if (member == null){
            return ResponseModel.buildNotFoundGroupError(null);
        }
        return ResponseModel.buildOk(new GroupCard(member));
    }

    /**
     * 拉取一个群的所有成员，你必须是成员之一
     * @param groupId
     * @return
     */
    @GET
    @Path("/{groupId}/member")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> members(@PathParam("groupId" )String groupId){
        if (Strings.isNullOrEmpty(groupId))
            return ResponseModel.buildParameterError();
        Group group = GroupFactory.findById(groupId);
        if (group == null){
            return ResponseModel.buildNotFoundGroupError(null);
        }
        User user = getSelf();
        GroupMember selfMember = GroupFactory.getMember(user.getId(),groupId);
        if (selfMember == null)
            //有这个群但是不在里面
            ResponseModel.buildNoPermissionError();
        //所有的成员
        Set<GroupMember> members = GroupFactory.getMembers(group);
        //至少有个管理员，一旦为null就是服务器错误
        if (members == null)
            return ResponseModel.buildServiceError();
        List<GroupMemberCard> memberCards = members.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());
        return ResponseModel.buildOk(memberCards);
    }

    /**
     * 给群添加成员的接口
     * @param groupId 群id,你必须是群的管理者之一
     * @param model
     * @return
     */
    @POST
    @Path("/{groupId}/member")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<GroupMemberCard>> memberAdd(@PathParam("groupId") String groupId, GroupMemberAddModel model){
        if (!GroupMemberAddModel.check(model)){
            return ResponseModel.buildParameterError();
        }
        User self = getSelf();
        model.getUsers().remove(self.getId());
        if (model.getUsers().size() == 0)
            return ResponseModel.buildParameterError();
        Group group = GroupFactory.findById(groupId);
        if (group == null)
            return ResponseModel.buildNotFoundGroupMemberError(null);
        //我必须是成员
        GroupMember selfMember = GroupFactory.getMember(self.getId(),groupId);
        if (selfMember == null || selfMember.getPermissionType() == GroupMember.PERMISSION_TYPE_NONE)
            //普通成员返回权限错误；
            return ResponseModel.buildNoPermissionError();

        //拿到已有的群成员
        Set<GroupMember> oldMembers = GroupFactory.getMembers(group);
        Set<String> oldMemberUserIds = oldMembers.stream()
                .map(GroupMember::getUserId)//懒加载直接获取不了用户信息，但是可以获取id
                .collect(Collectors.toSet());

        List<User> insertUsers = new ArrayList<>();
        for (String userId : model.getUsers()) {
            //一定要有这用户
            User user = UserFactory.findById(userId);
            if (user == null)
                continue;
            //已经在群里了
            if (oldMemberUserIds.contains(user.getId()))
                continue;
            insertUsers.add(user);
        }
        //没有一个新增的成员
        if (insertUsers.size() == 0)
            return ResponseModel.buildParameterError();
        //进行添加操作
        Set<GroupMember> insertMembers = GroupFactory.addMembers(group,insertUsers);

        if (insertMembers == null || insertMembers.size() == 0)
            return ResponseModel.buildServiceError();
        List<GroupMemberCard> insertCards  = insertMembers.stream()
                .map(GroupMemberCard::new)
                .collect(Collectors.toList());
        //通知，两部曲
        //通知新增的成员，你被加入了XXX群；
        PushFactory.pushJoinGroup(insertMembers);
        //通知群中的老成员，XXX被加入了群
        PushFactory.pushGroupMemberAdd(oldMembers,insertCards);
        return ResponseModel.buildOk(insertCards);
    }

    /**
     * 更改成员信息 ,请求的要么是群成员自己，要么是群管理，更改之后通知所有群成员
     * @param groupId 成员ID，可以查絮到对应的群
     * @param model 修改的model
     * @return
     */
    @PUT
    @Path("/member/{memberId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupMemberCard> modifyMember(@PathParam("memberId") String groupId, GroupMemberUpdateModel model){

        return null;
    }

    /**
     * 申请加入一个群，此时会创建一个加入的申请，并且写入表然后会给管理员发送消息
     * 管理员统一，其实就是调用添加成员的接口把对应的用户添加进去
     * @param groupId
     * @return  申请的信息
     */
    @POST
    @Path("/applyJoin/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<ApplyCard> join(@PathParam("groupId") String groupId){

        return null;
    }

}
