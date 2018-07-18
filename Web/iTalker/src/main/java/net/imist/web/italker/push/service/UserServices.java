package net.imist.web.italker.push.service;


import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.api.base.ResponseModel;
import net.imist.web.italker.push.bean.api.user.UpdateInfoModel;
import net.imist.web.italker.push.bean.card.UserCard;
import net.imist.web.italker.push.bean.db.User;

import net.imist.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import java.util.stream.Collectors;


/**
 * 用户信息处理的Services
 */
@Path("/user")
public class UserServices extends BaseService {

    /**
     * 更新用户信息的接口
     *
     * @param model
     * @return 返回自己的用户信息
     */
    @PUT  //不写就是当前的目录
    @Consumes(MediaType.APPLICATION_JSON)  //指定请求传入json
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> update(UpdateInfoModel model) {
        if (!UpdateInfoModel.check(model)) {
            ResponseModel.buildParameterError();
        }
        User self = getSelf();
        self = model.updateToUser(self);
        self = UserFactory.update(self);
        UserCard card = new UserCard(self, true);
        return ResponseModel.buildOk(card);
    }

    @GET
    @Path("/contact")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<List<UserCard>> contact() {
        User self = getSelf();
        //拿到我的联系人
        List<User> users = UserFactory.contacts(self);
        //转换为usercard
        List<UserCard> userCards = users.stream()
                //map操作相当于转制操作
                .map(user -> {
                    return new UserCard(user, true);
                }).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }

    @PUT//相当于修改关注状态
    @Path("/follow/{followId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> follow(@PathParam("followId") String followId) {
        User self = getSelf();
        //不能关注自己
        if (self.getId().equalsIgnoreCase(followId)) {
            //返回参数异常
            return ResponseModel.buildParameterError();
        }
        //找到我也关注的人
        User followUser = UserFactory.findById(followId);
        if (followUser == null) {
            return ResponseModel.buildNotFoundUserError(null);
        }
        //默认没有备注方便扩展
        followUser = UserFactory.follow(self, followUser, null);
        if (followUser == null) {
            return ResponseModel.buildServiceError();
        }
        //TODO 通知我关注的人，我关注了他

        //返回关注的人的信息
        return ResponseModel.buildOk(new UserCard(followUser, true));
    }

    //获取某人的信息
    @GET
    @Path("{id}") //"http://127.0.0.1/api/user/{id}"
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseModel<UserCard> getUser(@PathParam("id") String id) {
        if (Strings.isNullOrEmpty(id)) {
            ResponseModel.buildParameterError();
        }
        User self = getSelf();
        if (self.getId().equalsIgnoreCase(id)) {
            return ResponseModel.buildOk(new UserCard(self, true));
        }
        User user = UserFactory.findById(id);
        if (user == null) {
            //没找到，参数异常
            ResponseModel.buildNotFoundUserError(null);
        }
        //如果我们直接有关注的记录，则我已关注需要查询信息的用户
        boolean isFollow = UserFactory.getUserFollow(self, user) != null;
        return ResponseModel.buildOk(new UserCard(user, isFollow));
    }

    //搜索人的接口实现
    //为了简化分页，一次返回20条
    @GET //搜索人，不涉及到数据更改，只是查询，则为get
    //"http://127.0.0.1/api/user/search/"
    @Path("/search/{name:(.*)?}")
    public ResponseModel<List<UserCard>> search(@DefaultValue("") @PathParam("name") String name) {
        User self = getSelf();
        List<User> searchUsers = UserFactory.search(name);
        //将查询的人封装为UserCard；
        //判断这些人是否有我已经关注的人；
        //如果有的话，则返回的关注状态中应该有我已经设置好的状态；

        //拿出我的联系人
        List<User> contacts = UserFactory.contacts(self);
        //不算太高效，但是联系人比较少，并且只取了20条，最好关联查询；
        List<UserCard> userCards = searchUsers.stream()
                .map(user -> {
                    boolean isFollow = user.getId().equalsIgnoreCase(self.getId())
                            || contacts.stream().anyMatch(contactUser ->
                            contactUser.getId().equalsIgnoreCase(user.getId())
                    );
                    return new UserCard(user, isFollow);
                }).collect(Collectors.toList());
        return ResponseModel.buildOk(userCards);
    }
}
