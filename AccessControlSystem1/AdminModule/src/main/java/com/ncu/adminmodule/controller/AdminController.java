package com.ncu.adminmodule.controller;

import com.ncu.common.dao.UserDAO;
import com.ncu.common.model.User;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.util.List;

public class AdminController {
    private UserDAO userDAO;

    public AdminController() {
        this.userDAO = new UserDAO();
    }

    // 加载所有用户数据到表格模型
    public void loadUserData(DefaultTableModel model) {
        model.setRowCount(0); // 清空现有数据
        List<User> users = userDAO.getAllOperators(); // 获取所有操作员（管理员和医护人员）

        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getName(),
                    user.getPhone(),
                    "admin".equals(user.getRole()) ? "管理员" : "医护人员",
                    user.getGender(),
                    user.getBirthDate() != null ? user.getBirthDate().toString() : "",
                    user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
            });
        }
    }

    // 添加新用户
    public boolean addUser(String username, String password, String role,
                           String name, String phone, LocalDate birthDate,
                           String gender, String idCard) {
        User newUser = new User(0, username, password, role, true,
                name, phone, birthDate, gender, idCard);
        return userDAO.addUser(newUser);
    }

    // 重置用户密码
    public boolean resetPassword(int userId, String newPassword) {
        return userDAO.updateUserPassword(userId, newPassword);
    }

    // 检查用户名是否存在
    public boolean isUsernameExists(String username) {
        return userDAO.isUsernameExists(username);
    }
}