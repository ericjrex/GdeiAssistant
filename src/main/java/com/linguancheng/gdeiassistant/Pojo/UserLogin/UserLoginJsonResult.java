package com.linguancheng.gdeiassistant.Pojo.UserLogin;

import com.linguancheng.gdeiassistant.Pojo.Entity.Token;
import com.linguancheng.gdeiassistant.Pojo.Entity.User;
import com.linguancheng.gdeiassistant.Pojo.Result.JsonResult;

/**
 * 广东二师助手团队 林冠成 版权所有
 * All rights reserved © 2016 - 2018
 * Author:林冠成
 * Date:2018/11/5
 */
public class UserLoginJsonResult extends JsonResult {

    private User user;

    private String accessToken;

    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
