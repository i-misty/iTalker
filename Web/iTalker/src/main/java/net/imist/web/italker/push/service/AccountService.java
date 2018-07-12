package net.imist.web.italker.push.service;

import net.imist.web.italker.push.bean.api.account.AccountRspModel;
import net.imist.web.italker.push.bean.api.account.LoginModel;
import net.imist.web.italker.push.bean.api.account.RegisterModel;
import net.imist.web.italker.push.bean.api.base.ResponseModel;
import net.imist.web.italker.push.bean.card.UserCard;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.factory.UserFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author iMist
 * //net.imist.web.italker.push.service 搜索处理的报名  api下的所有
 *     //127.0.0.1/api/account/...
 *    Google 插件库可以用于调试接口
 */

@Path("/account")
public class AccountService {


    @POST
    @Path("/login")
    @Consumes (MediaType.APPLICATION_JSON)  //指定请求传入json
    @Produces (MediaType.APPLICATION_JSON)  //返回 json
    public ResponseModel<AccountRspModel> login(LoginModel model) {
        User user = UserFactory.login(model.getAccount(),model.getPassword());
        if (user != null){
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        }else {
            return ResponseModel.buildLoginError();
        }
    }
    @POST
    @Path("/register")
    @Consumes (MediaType.APPLICATION_JSON)  //指定请求传入json
    @Produces (MediaType.APPLICATION_JSON)  //返回 json
    public ResponseModel<AccountRspModel> register(RegisterModel model) {
        User user = UserFactory.findByPhone(model.getAccount().trim());
        if (user != null){
            return ResponseModel.buildAccountError();
        }
        user = UserFactory.findByName(model.getName().trim());
        if (user != null){
            return ResponseModel.buildHaveNameError();
        }
        user = UserFactory.register(model.getAccount(),
                model.getPassword(),
                model.getName());
        if (user != null){
            AccountRspModel rspModel = new AccountRspModel(user);
            return ResponseModel.buildOk(rspModel);
        }else {
            return ResponseModel.buildRegisterError();
        }
    }
}
