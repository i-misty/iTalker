package net.imist.web.italker.push.factory;

import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.utils.Hib;
import net.imist.web.italker.push.utils.TextUtil;
import org.hibernate.Session;

import java.util.List;
import java.util.UUID;

public class UserFactory {

    //通过token字段查询用户信息
    //只能自己使用，查询的是自己的信息。而不是他人
    public static User findByToken(String token) {
        return Hib.query(session -> (User) session.createQuery("from User where token = :token")
                .setParameter("token", token)
                .uniqueResult());
    }

    public static User findByPhone(String phone) {
        return Hib.query(session -> (User) session.createQuery("from User where phone =:inPhone")
                .setParameter("inPhone", phone)
                .uniqueResult());
    }

    public static User findByName(String name) {
        return Hib.query(session -> (User) session.createQuery("from User where name =:name")
                .setParameter("name", name)
                .uniqueResult());
    }

    /**
     * 给当前的账户绑定pushId
     *
     * @param user
     * @param pushId
     * @return User
     */
    public static User bindPushId(User user, String pushId) {
        //第一步，查询是否有其他账户绑定了这个设备
        //取消绑定避免消息推送混乱
        Hib.queryOnly(session -> {
            List<User> userList = (List<User>) session
                    .createQuery("from User where lower(pushId)=:pushId and id != :userId")
                    .setParameter("pushId", pushId.toLowerCase())
                    .setParameter("userId", user.getId())
                    .list();
            for (User u : userList) {
                u.setToken(null);
                session.saveOrUpdate(u);
            }
        });
        if (pushId.equalsIgnoreCase(user.getPushId())) {
            //如果当前需要绑定的设备id，之前已经绑定过了，那么不再需要额外的绑定
            return user;
        } else {
            //如果当前的账户之前的设备id和需要绑定的不同
            //呢么需要单点登陆 ，让之前的设备推出账户，给之前的账户推送一条推出消息
            if (Strings.isNullOrEmpty(user.getPushId())) {
                //TODO 推送一条退出消息
            }
            //更新新的设备id
            user.setPushId(pushId);
            return Hib.query(session -> {
                session.saveOrUpdate(user);
                return user;
            });
        }


    }

    /**
     * 使用账户和密码进行登陆
     *
     * @param account
     * @param password
     * @return
     */
    public static User login(String account, String password) {
        String accountStr = account.trim();
        //将原文进行同样的处理然后才能匹配
        String encodePassword = encodePassword(password);
        User user = Hib.query(session -> (User) session.createQuery("from User where phone =:phone and password =:password")
                .setParameter("phone", accountStr)
                .setParameter("password", encodePassword)
                .uniqueResult());
        if (user != null) {
            //对token进行登录操作，更新token
            user = login(user);
        }
        return user;
    }

    /**
     * 用户注册
     *
     * @param account
     * @param password
     * @param name
     * @return User
     */
    public static User register(String account, String password, String name) {
        account = account.trim();
        //处理密码
        password = encodePassword(password);
        User user = createUser(account, password, name);
        if (user != null) {
            user = login(user);
        }
        return user;
    }

    /**
     * 注册部分，新建用户逻辑
     *
     * @param account
     * @param password
     * @param name
     * @return 返回一个用户
     */
    public static User createUser(String account, String password, String name) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhone(account);
        return Hib.query(session -> (User) session.save(user));
    }

    /**
     * 把一个user进行登陆操作,本质上是对token操作
     *
     * @param user
     * @return
     */
    private static User login(User user) {
        String newToken = UUID.randomUUID().toString();
        newToken = TextUtil.encodeBase64(newToken);
        user.setToken(newToken);
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });

    }

    private static String encodePassword(String password) {
        //密码去除首尾空格
        password = password.trim();
        //对MD5非对称加密，加盐更加安全，盐也要储存
        password = TextUtil.getMD5(password);
        //再进行一次base64加密，当然也可以采取加盐方案
        return TextUtil.encodeBase64(password);
    }
}
