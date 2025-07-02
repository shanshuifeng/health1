package com.ncu.mainmodule.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginView() {
        // 初始化组件
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("登录");

        // 创建面板并设置布局
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        // 添加组件到面板
        panel.add(new JLabel("用户名:"));
        panel.add(usernameField);
        panel.add(new JLabel("密码:"));
        panel.add(passwordField);
        panel.add(new JLabel()); // 空标签用于对齐
        panel.add(loginButton);

        // 设置窗口属性
        setTitle("登录页面");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 居中显示窗口
        setContentPane(panel);

        // 添加登录按钮的点击事件监听器
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // 这里可以添加登录验证逻辑
                if (username.equals("admin") && password.equals("123456")) {
                    JOptionPane.showMessageDialog(LoginView.this, "登录成功!");
                } else {
                    JOptionPane.showMessageDialog(LoginView.this, "用户名或密码错误!");
                }
            }
        });
    }

    public void showLogin() {
        setVisible(true);
    }

    // 主方法用于测试
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().showLogin();
        });
    }
}
