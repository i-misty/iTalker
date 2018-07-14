package net.imist.web.italker.push.service;

import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.api.base.ResponseModel;
import net.imist.web.italker.push.bean.api.user.UpdateInfoModel;
import net.imist.web.italker.push.bean.card.UserCard;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


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
}
