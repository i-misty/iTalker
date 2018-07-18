package net.imist.web.italker.push.factory;

import com.google.common.base.Strings;
import net.imist.web.italker.push.bean.card.UserCard;
import net.imist.web.italker.push.bean.db.User;
import net.imist.web.italker.push.bean.db.UserFollow;
import net.imist.web.italker.push.utils.Hib;
import net.imist.web.italker.push.utils.TextUtil;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return Hib.query(session -> {
            return (User) session
                    .createQuery("from User where name =:name")
                    .setParameter("name", name)
                    .uniqueResult();
        });
    }
    public static User findById(String id) {
        //通过主键查询更方便
        return Hib.query(session -> session.get(User.class,id));
    }

    /**
     * 更新用户信息到数据库
     *
     * @param user
     * @return
     */
    public static User update(User user) {
        return Hib.query(session -> {
            session.saveOrUpdate(user);
            return user;
        });
    }


    /**
     * 给当前的账户绑定pushId
     *
     * @param user
     * @param pushId
     * @return User
     */
    @SuppressWarnings("unchecked")
    public static User bindPushId(User user, String pushId) {
        if (Strings.isNullOrEmpty(pushId)) {
            return null;
        }
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
            return update(user);
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
        // 账户就是手机号
        user.setPhone(account);
        return Hib.query(session -> {
            session.save(user);
            return user;
        });
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
        return update(user);

    }

    /**
     * 对密码进行加密操作
     *
     * @param password 原文
     * @return 密文
     */
    private static String encodePassword(String password) {
        //密码去除首尾空格
        password = password.trim();
        //对MD5非对称加密，加盐更加安全，盐也要储存
        password = TextUtil.getMD5(password);
        //再进行一次base64加密，当然也可以采取加盐方案
        return TextUtil.encodeBase64(password);
    }

    /**
     * 获取联系人列表
     * @param self
     * @return List<User>
     */
    public static List<User> contacts(User self){
        //self.getFollowers();因为是懒加载，当事务中session加载完毕之后就会销毁，这样无法拿到数据
        return Hib.query(session -> {
            //重新加载一次用户信息到session中，和当前的session绑定
            session.load(self,self.getId());
            Set<UserFollow> flows = self.getFollowing();
            return flows.stream()
                    .map(UserFollow::getTarget)
                    .collect(Collectors.toList());
        });
    }

    /**
     * 关注人的操作
     * @param origin 发起者
     * @param target 被关注人
     * @param alias  备注名
     * @return  被关注的人的信息
     */
    public static User follow(final User origin,final User target,String alias){
        UserFollow follow = getUserFollow(origin,target);
        if (follow != null){
            //已关注，直接返回
            return follow.getTarget();
        }
        return Hib.query(session -> {
            //操作懒加载的数据需要重新load一次
            session.load(origin,origin.getId());
            session.load(target,target.getId());
            //我关注人的时候同时他也关注我； 所以需要添加两条userfollow数据
            UserFollow originFollow = new UserFollow();
            originFollow.setOrigin(origin);
            originFollow.setTarget(target);
            originFollow.setAlias(alias);
            //他关注我没有备注信息，我变成被关注对象
            UserFollow targetFollow = new UserFollow();
            targetFollow.setOrigin(target);
            targetFollow.setTarget(origin);

            session.save(originFollow);
            session.save(targetFollow);
            return target;
        });
    }

    /**
     * 查询两个人是否已经关注
     * @param origin 发起者
     * @param target  被关注人
     * @return 返回中间类 UserFollow
     */
    public static UserFollow getUserFollow(final User origin , final User target){
        return Hib.query(session -> {
           return (UserFollow)session.createQuery("from UserFollow where originId =:originId and targetId = :targetId")
                    .setParameter("originId",origin.getId())
                    .setParameter("targetId",target.getId())
                    .setMaxResults(1)
                    //查询一条数据
                    .uniqueResult();
        });
    }

    /**
     * 搜索联系人额实现
     * @param name 查询的name,允许为null，如果那么为null,则返回最近的用户
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<User> search(String name) {
        if (Strings.isNullOrEmpty(name)){
            name = "";//保证不能为null的情况，减少后面的一些判断和额外的错误
        }
        final String searchName = "%" +name+"%";
        return Hib.query(session -> {
            return  (List<User>) session.createQuery("from User where lower(name) like :name and portrait is not null and description is not null  ")
                    .setParameter("name",searchName)
                    .setMaxResults(20)//至多20条
                    .list();
        });
    }
}
