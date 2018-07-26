package com.imist.italker.factory.data.helper;

import com.imist.italker.factory.model.db.AppDatabase;
import com.imist.italker.factory.model.db.Group;
import com.imist.italker.factory.model.db.GroupMember;
import com.imist.italker.factory.model.db.Group_Table;
import com.imist.italker.factory.model.db.Message;
import com.imist.italker.factory.model.db.Session;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 数据库的辅助工具类，
 * 辅助完成增删改 ，这样在更换数据库的时候就不用修改其他类只需要更改实现即可；
 */
public class DbHelper {
    private static final DbHelper instance;

    static {
        instance = new DbHelper();
    }

    private DbHelper() {
    }

    /**
     * 观察者的集合
     * Class<?> 观察的表
     * Set<ChangedListener> 每一个表对应的观察者有很多
     */
    private  final Map<Class<?>, Set<ChangedListener>> changedListeners = new HashMap<>();

    /**
     * 从所有的监听器中，获取一个表的所有监听者；
     *
     * @param modelClass
     * @param <Model>
     * @return
     */
    public  <Model extends BaseModel> Set<ChangedListener> getListeners(Class<Model> modelClass) {
        if (instance.changedListeners.containsKey(modelClass)) {
            return instance.changedListeners.get(modelClass);
        }
        return null;
    }

    /**
     * 添加一个监听
     *
     * @param tClass   对某个表的关注
     * @param listener 监听者
     * @param <Model>  表的 泛型
     */
    public static <Model extends BaseModel> void addChangeListener(Class<Model> tClass, ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            //初始化某一类型的容器
            changedListeners = new HashSet<>();
            //添加到的Map
            instance.changedListeners.put(tClass, changedListeners);
        }
        changedListeners.add(listener);
    }

    /**
     * 移除一个监听
     *
     * @param tClass   对某个表的关注
     * @param listener 监听者
     * @param <Model>  表的 泛型
     */
    public static <Model extends BaseModel> void removeChangeListener(Class<Model> tClass, ChangedListener<Model> listener) {
        Set<ChangedListener> changedListeners = instance.getListeners(tClass);
        if (changedListeners == null) {
            //容器本身为null代表本身就没有
            return;
        }
        //从容器中删除监者听者
        changedListeners.remove(listener);
    }

    //第一种，直接保存一列
    //user.save();
    //2.ModelAdapter 可以保存集合
    /*FlowManager.getModelAdapter(User.class).save(user);

    //3.事务保存
    DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
                    definition.beginTransactionAsync(new ITransaction() {
        @Override
        public void execute (DatabaseWrapper databaseWrapper){
            FlowManager.getModelAdapter(User.class)
                    .save(user);
        }
    }).build().execute();*/

    /*public static void save(final User... users){
        if (users == null || users.length == 0)
            return;
        //获取数据库的管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        //提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<User> adapter = FlowManager.getModelAdapter(User.class);
                //数组转换集合
                adapter.saveAll(Arrays.asList(users));
            }
        }).build().execute();
    }*/

    /**
     * 统一保存单个和保存多个的问题；统一多种数据类型保存问题，一个方法方便维护
     * <p>
     * 这里是异步的保存操作
     * <p>
     * 限定条件是BaseModel DBFlow 的数据库实体继承的基类，防止传入任意类型的数据报错
     * <p>
     * 新增或者修改的统一方法
     *
     * @param tClass  传递一个class信息
     * @param models  这个class对于的实例的数组
     * @param <Model> 这个实例的泛型，限定条件是BaseModel
     */
    public static <Model extends BaseModel> void save(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;
        //获取数据库的管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        //提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //保存
                adapter.saveAll(Arrays.asList(models));
                //唤起通知
                instance.notifySave(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 进行删除数据库的统一方法
     *
     * @param tClass  传递一个class信息
     * @param models  这个class对于的实例的数组
     * @param <Model> 这个实例的泛型，限定条件是BaseModel
     */
    public static <Model extends BaseModel> void delete(final Class<Model> tClass, final Model... models) {
        if (models == null || models.length == 0)
            return;
        //获取数据库的管理者
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        //提交一个事物
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                //执行
                ModelAdapter<Model> adapter = FlowManager.getModelAdapter(tClass);
                //删除
                adapter.deleteAll(Arrays.asList(models));
                //唤起通知
                instance.notifyDelete(tClass, models);
            }
        }).build().execute();
    }

    /**
     * 进行通知调用
     *
     * @param tClass
     * @param models
     * @param <Model>
     */
    private final <Model extends BaseModel> void notifySave(final Class<Model> tClass, final Model... models) {
        //找监听器
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            //因为每一次都是确定的，随不用进行检查，除非set集合内部问题
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataSave(models);
            }
        }
        //列外情况；
        //群成员变更，需要通知对应群信息更新
        //消息变化。应该通知会话列表跟新
        if (GroupMember.class.equals(tClass)){
            //群成员更新，需要通知对应的群信息更新
            updateGroup((GroupMember[]) models);
        }else if (Message.class.equals(tClass)){
            //消息变化，应该通知会话列表更新
            updateSession((Message[]) models);
        }
    }


    /**
     * 进行通知调用
     *
     * @param tClass
     * @param models
     * @param <Model>
     */
    private final <Model extends BaseModel> void notifyDelete(final Class<Model> tClass, final Model... models) {
        //找监听器
        final Set<ChangedListener> listeners = getListeners(tClass);
        if (listeners != null && listeners.size() > 0) {
            //因为每一次都是确定的，随不用进行检查，除非set集合内部问题
            for (ChangedListener<Model> listener : listeners) {
                listener.onDataDelete(models);
            }
        }
        //列外情况；
        //群成员变更，需要通知对应群信息更新
        //消息变化。应该通知会话列表跟新
        if (GroupMember.class.equals(tClass)){
            //群成员更新，需要通知对应的群信息更新
            updateGroup((GroupMember[]) models);
        }else if (Message.class.equals(tClass)){
            //消息变化，应该通知会话列表更新
            updateSession((Message[]) models);
        }
    }

    /**
     * 從成員中找出对应的群，并对群进行更新
     * @param members
     */
    private void updateGroup(final GroupMember ...members){
        //不重复集合
        final Set<String> groupIds = new HashSet<>();
        for (GroupMember member : members){
            //添加群id
            groupIds.add(member.getGroup().getId());
        }
        //异步的数据查询，并发起二次通知
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                List<Group> groups = SQLite.select()
                        .from(Group.class)
                        .where(Group_Table.id.in(groupIds))
                        .queryList();
                //调用直接进行一次消息分发
               instance.notifySave(Group.class,groups.toArray(new Group[0]));
            }
        }).build().execute();

    }
    private void updateSession(Message ...messages){
        //标识一个session的唯一性
        final Set<Session.Identify> identifies = new HashSet<>();
        for (Message message : messages) {
            Session.Identify identify = Session.createSessionIdentify(message);
            identifies.add(identify);
        }
        DatabaseDefinition definition = FlowManager.getDatabase(AppDatabase.class);
        definition.beginTransactionAsync(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                ModelAdapter<Session> adapter = FlowManager.getModelAdapter(Session.class);
                Session[] sessions = new Session[identifies.size()];
                int index = 0;
                for (Session.Identify identify : identifies) {
                    Session session = SessionHelper.findFromLocal(identify.id);
                    if (session == null){
                        //第一次聊天，创建一个和对方的会话
                        session = new Session(identify);
                    }
                    //把会话刷新到当前Message的最新状态
                    session.refreshToNow();
                    //数据存储
                    adapter.save(session);
                    //添加到集合
                    sessions[index++] = session;
                }
                //调用直接进行一次消息分发
                instance.notifySave(Session.class,sessions);
            }
        }).build().execute();
    }

    /**
     * 从消息列表中筛选出对应的会话，并且对会话进行更新
     * @param <Data>
     */
    @SuppressWarnings({"unused", "unchecked"})
    public interface ChangedListener<Data extends BaseModel> {

        void onDataSave(Data... list);


        void onDataDelete(Data... list);

    }

}
