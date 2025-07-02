package com.ncu.mainmodule.view;

import com.ncu.adminmodule.view.AdminView;
import com.ncu.common.model.User;
import java.awt.Font;


import javax.swing.*;

public class MainView extends JFrame {
    public MainView(User user) {
        setTitle("健康检查系统 - " + ("admin".equals(user.getRole()) ? "管理员" :
                ("doctor".equals(user.getRole()) ? "医护人员" : "患者")) + "界面");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // 根据用户角色显示不同界面
        if ("admin".equals(user.getRole())) {
            AdminView adminView = new AdminView();
            add(adminView.getAdminPanel());
        } else {
            JLabel welcomeLabel = new JLabel("欢迎, " + user.getName() + " (" + user.getUsername() + ")", JLabel.CENTER);
            welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
            add(welcomeLabel);
        }
    }
}