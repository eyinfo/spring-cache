package com.eyinfo.springcache.store;

import com.eyinfo.foundation.utils.TextUtils;
import com.eyinfo.springcache.storage.SecondaryStorage;

public class UserStore {
    private static UserStore userStore;

    public static UserStore getInstance() {
        if (userStore == null) {
            synchronized (UserStore.class) {
                userStore = new UserStore();
            }
        }
        return userStore;
    }

    public final String tokenName = "Authorization";
    //失效时间2个月
    final long expiredTime = 5184000000L;

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(String token, String userId) {
        SecondaryStorage.getInstance().set(token, userId, expiredTime);
    }

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public String getUserId(String token) {
        return SecondaryStorage.getInstance().get(token, String.class);
    }

    /**
     * 设置用户信息
     *
     * @param user 用户信息
     */
    public <T> void setUserInfo(String token, T user) {
        SecondaryStorage.getInstance().set(String.format("user_%s", token), user, expiredTime);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    public <T> T getUserInfo(Class<T> clazz, String token) {
        return SecondaryStorage.getInstance().get(String.format("user_%s", token), clazz);
    }

    /**
     * 判断是否登录
     */
    public boolean isLogin(String token) {
        if (TextUtils.isEmpty(token)) {
            return false;
        }
        String userId = SecondaryStorage.getInstance().get(token, String.class);
        return !TextUtils.isEmpty(userId);
    }
}
