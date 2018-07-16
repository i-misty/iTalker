package net.imist.web.italker.push.service;

import net.imist.web.italker.push.bean.db.User;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

public class BaseService {
    // 添加一个上下文注解 ，该注解会自动给 securityContext 赋予值
    //具体值是我们拦截器中所返回的SecurityContext
    @Context
    protected SecurityContext securityContext;

    /**
     * 上下文中直接获取自己的信息
     * @return
     */
    protected User getSelf(){
        return (User) securityContext.getUserPrincipal();
    }
}
