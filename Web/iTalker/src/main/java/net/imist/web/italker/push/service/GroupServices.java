package net.imist.web.italker.push.service;

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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
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
        PushFactory.pushGroupAdd(members);

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

        return null;
    }

    /**
     * 获取一个群的信息
     * @param groupId
     * @return
     */
    @GET
    @Path("/{groupId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<GroupCard> getGroup(@PathParam("groupId") String groupId) {

        return null;
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
        return null;
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

        return null;
    }

    /**
     * 更改成员信息 ,请求的要么是群成员，要么是群管理
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
