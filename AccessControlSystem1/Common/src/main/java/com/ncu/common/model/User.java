//文件: User.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.ncu.common.model;

import java.util.Date;

// 用户模型
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private boolean firstLogin;
    private Date createdAt;  // 统一使用 java.util.Date

    public User(int id, String username, String password, String role, boolean firstLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstLogin = firstLogin;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isFirstLogin() { return firstLogin; }
    public Date getCreatedAt() { return createdAt; }

    public void setPassword(String password) { this.password = password; }
    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }
    public void setRole(String role) { this.role = role; }

}

