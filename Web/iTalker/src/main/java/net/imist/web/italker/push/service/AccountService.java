package net.imist.web.italker.push.service;

import net.imist.web.italker.push.bean.db.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author iMist
 *
 */

/**
 * //net.imist.web.italker.push.service 搜索处理的报名  api下的所有
 *     //127.0.0.1/api/account/...
 *
 *    Google 插件库可以用于调试接口
 */
@Path("/account")
public class AccountService {
    //GET 127.0.0.1/api/account/login
    @GET
    @Path("/login")  //若是不带路径则当GET请求时默认访问此方法 若是当前类存在路径相同的get方法则报错
    public String get(){
        return "you get  the login";
    }

    /**
     * POST 127.0.0.1/api/account/login
     *     jersey 可以识别请求方式 ，返回不同数据格式的值，但是要指定
     * @return
     */
    @POST
    @Path("/login")
    @Consumes (MediaType.APPLICATION_JSON)  //指定请求传入json
    @Produces (MediaType.APPLICATION_JSON)  //返回 json
    public User post(){
        User user = new User();
        user.setName("iMist");
        user.setSex(1);
        return user;
    }

}
