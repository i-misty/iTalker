package net.imist.web.italker.push.service;

import net.imist.web.italker.push.bean.api.account.RegisterModel;
import net.imist.web.italker.push.bean.db.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author iMist
 */

/**
 * //net.imist.web.italker.push.service 搜索处理的报名  api下的所有
 *     //127.0.0.1/api/account/...
 *
 *    Google 插件库可以用于调试接口
 */
@Path("/account")
public class AccountService {
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User register(RegisterModel model){
        User user = new User();
        user.setName("iMist");
        user.setSex(1);
        return user;
    }

}
