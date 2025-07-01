//文件: AdminController.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.adminmodule.controller;

import com.wan.common.dao.UserDAO;
import com.wan.common.model.User;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class AdminController {

    // Load User Data（AdminView）
    public void loadUserData(DefaultTableModel model) {
        model.setRowCount(0);
        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllOperators();

        for (User user : users) {
            model.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getRole().equals("admin") ? "管理员" : "操作员",
                    user.isFirstLogin() ? "是" : "否",
                    user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
            });
        }
    }

}
