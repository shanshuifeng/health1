//文件: LoginController.java ///////////////////////////////////////////////////////////////////////////////////////////

// LoginController.java
package com.ncu.wan.mainmodule.controller;

import com.wan.common.dao.UserDAO;
import com.wan.common.model.User;
import com.wan.common.util.EncryptUtil;

public class LoginController {

    public interface LoginListener {
        void onLoginSuccess(User user);
        void onFirstLogin(User user);
        void onLoginFailed(String errorMessage);
        void onPasswordChangeSuccess(User user);
        void onPasswordChangeFailed(String errorMessage);
    }

    private LoginListener loginListener;

    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    public void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            if (loginListener != null) {
                loginListener.onLoginFailed("用户名和密码不能为空");
            }
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);

        if (user == null || !EncryptUtil.decrypt(user.getPassword()).equals(password)) {
            if (loginListener != null) {
                loginListener.onLoginFailed("用户名或密码错误");
            }
            return;
        }

        if (user.isFirstLogin()) {
            if (loginListener != null) {
                loginListener.onFirstLogin(user);
            }
        } else {
            if (loginListener != null) {
                loginListener.onLoginSuccess(user);
            }
        }
    }

    public void handleChangePassword(User user, String newPassword, String confirmPassword) {
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            if (loginListener != null) {
                loginListener.onPasswordChangeFailed("密码不能为空");
            }
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            if (loginListener != null) {
                loginListener.onPasswordChangeFailed("两次输入的密码不一致");
            }
            return;
        }

        if (newPassword.length() < 6) {
            if (loginListener != null) {
                loginListener.onPasswordChangeFailed("密码长度不能少于6位");
            }
            return;
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.updateUserPassword(user.getId(), newPassword)) {
            if (loginListener != null) {
                loginListener.onPasswordChangeSuccess(user);
            }
        } else {
            if (loginListener != null) {
                loginListener.onPasswordChangeFailed("密码修改失败");
            }
        }
    }

}
