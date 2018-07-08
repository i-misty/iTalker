package com.imist.italker.push.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

public class NavHelper<T> {
    private final SparseArray<Tab<T>> tabs = new SparseArray<>();
    private Context context;
    private final FragmentManager fragmentManager;
    private final int containerId;
    private final onTabChangeListener<T> listener;
    //当前选中的Tab
    private Tab<T> currentTab;

    public NavHelper(Context context, int containerId, FragmentManager fragmentManager, onTabChangeListener<T> listener) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
        this.listener = listener;
    }

    /**
     * 添加tab
     *
     * @param menuId 添加对应的菜单id
     * @param tab
     */
    public NavHelper<T> add(int menuId, Tab<T> tab) {
        tabs.put(menuId, tab);
        return this;
    }

    /**
     * 获取当前显示的Tab
     *
     * @return
     */
    public Tab<T> getCurrentTab() {
        return currentTab;
    }

    /**
     * 执行点击菜单的操作
     *
     * @param menuId 菜单的id
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenu(int menuId) {
        //集合中寻找点击的菜单对于的tab ，并且进行处理
        Tab<T> tab = tabs.get(menuId);
        if (tab != null) {
            doSelect(tab);
            return true;
        }
        return false;
    }

    private void doSelect(Tab<T> tab) {
        Tab<T> oldTab = null;
        if (currentTab != null) {
            oldTab = currentTab;
            if (oldTab == tab) {
                //如果当前的Tab就是点击的Tab那么不做处理
                notifyTabReselect(tab);
            }
        }
        currentTab = tab;
        doTabChanged(currentTab, oldTab);
    }

    /**
     * 进行fragment真实的调度工作
     *
     * @param newTab
     * @param oldTab
     */
    private void doTabChanged(Tab<T> newTab, Tab<T> oldTab) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (oldTab != null) {
            if (oldTab.fragment != null) {
                //从界面移除，但是还在Fragment对应的缓存空间中
                ft.detach(oldTab.fragment);
            }
        }
        if (newTab != null) {
            if (newTab.fragment == null) {
                Fragment fragment = Fragment.instantiate(context, newTab.cls.getName(), null);
                newTab.fragment = fragment;
                ft.add(containerId, fragment, newTab.cls.getName());
            } else {
                //从fragmentManager的缓存空间中加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        //提交事务
        ft.commit();
        notifyTabSelect(newTab, oldTab);
    }

    /**
     * 回调监听器
     *
     * @param newTab
     * @param oldTab
     */
    private void notifyTabSelect(Tab<T> newTab, Tab<T> oldTab) {
        if (listener != null) {
            listener.onTabChanged(newTab, oldTab);
        }
    }

    private void notifyTabReselect(Tab<T> tab) {

    }

    /**
     * 所有的tab基础属性
     *
     * @param <T>
     */
    public static class Tab<T> {
        public Tab(Class<?> cls, T extra) {
            this.cls = cls;
            this.extra = extra;
        }

        //Fragment对应的class信息
        public Class<?> cls;
        //额外的字段 ，用户自己设定使用
        public T extra;
        //内部缓存对应的Fragment
        Fragment fragment;
    }

    public interface onTabChangeListener<T> {
        void onTabChanged(Tab<T> newTab, Tab<T> oldTab);
    }


}
