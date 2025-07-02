package com.ncu.common.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String password;
    private String role; // "admin", "patient" 或 "doctor"
    private boolean firstLogin;
    private String name;
    private String phone;
    private LocalDate birthDate;
    private String gender;
    private String idCard; // 仅医护人员有
    private LocalDateTime createdAt; // 新增字段

    public User(int id, String username, String password, String role, boolean firstLogin,
                String name, String phone, LocalDate birthDate, String gender, String idCard) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.firstLogin = firstLogin;
        this.name = name;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.idCard = idCard;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public boolean isFirstLogin() { return firstLogin; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getGender() { return gender; }
    public String getIdCard() { return idCard; }
    public LocalDateTime getCreatedAt() { return createdAt; } // 新增方法

    // Setters
    public void setPassword(String password) { this.password = password; }
    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }
}