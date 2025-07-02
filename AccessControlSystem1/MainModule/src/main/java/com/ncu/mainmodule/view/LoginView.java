package com.ncu.mainmodule.view;

import com.ncu.common.dao.UserDAO;
import com.ncu.mainmodule.controller.LoginController;
import com.ncu.common.model.User;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class LoginView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    private LoginController loginController;

    public LoginView() {
        loginController = new LoginController();
        initUI();
    }

    private void initUI() {
        setTitle("健康检查系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // 密码标签和输入框
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // 登录按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginButton = new JButton("登录");
        panel.add(loginButton, gbc);

        // 注册按钮
        gbc.gridy = 3;
        registerButton = new JButton("注册新用户");
        panel.add(registerButton, gbc);

        add(panel);

        // 设置登录按钮事件
        loginButton.addActionListener(e -> handleLogin());

        // 设置注册按钮事件
        registerButton.addActionListener(e -> showRegistrationDialog());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        loginController.setLoginListener(new LoginController.LoginListener() {
            @Override
            public void onLoginSuccess(User user) {
                JOptionPane.showMessageDialog(LoginView.this, "登录成功!");
                dispose();
                // 这里可以打开主界面
                new MainView(user).setVisible(true);
            }

            @Override
            public void onFirstLogin(User user) {
                handleFirstLogin(user);
            }

            @Override
            public void onLoginFailed(String errorMessage) {
                JOptionPane.showMessageDialog(LoginView.this, errorMessage, "登录失败", JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void onPasswordChangeSuccess(User user) {
                JOptionPane.showMessageDialog(LoginView.this, "密码修改成功，请重新登录", "成功", JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void onPasswordChangeFailed(String errorMessage) {
                JOptionPane.showMessageDialog(LoginView.this, errorMessage, "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginController.handleLogin(username, password);
    }

    private void handleFirstLogin(User user) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        panel.add(new JLabel("新密码:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("确认密码:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "首次登录请修改密码",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            loginController.handleChangePassword(user, newPassword, confirmPassword);
        }
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "用户注册", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("确认密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField confirmPasswordField = new JPasswordField(15);
        panel.add(confirmPasswordField, gbc);

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);

        // 电话
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("电话:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(15);
        panel.add(phoneField, gbc);

        // 出生日期
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("出生日期:"), gbc);

        gbc.gridx = 1;
        JDateChooser birthDateChooser = new JDateChooser();
        birthDateChooser.setDateFormatString("yyyy-MM-dd");
        panel.add(birthDateChooser, gbc);

        // 性别
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("性别:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"男", "女", "其他"});
        panel.add(genderCombo, gbc);

        // 用户类型
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("用户类型:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"患者", "医护人员"});
        panel.add(roleCombo, gbc);

        // 身份证号 (仅医护人员)
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("身份证号 (医护人员):"), gbc);

        gbc.gridx = 1;
        JTextField idCardField = new JTextField(15);
        idCardField.setEnabled(false);
        panel.add(idCardField, gbc);

        // 根据用户类型切换身份证号字段状态
        roleCombo.addActionListener(e -> {
            String selectedRole = (String) roleCombo.getSelectedItem();
            idCardField.setEnabled("医护人员".equals(selectedRole));
            if (!"医护人员".equals(selectedRole)) {
                idCardField.setText("");
            }
        });

        // 注册按钮
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("注册");
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            // 验证输入
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            Date birthDate = birthDateChooser.getDate();
            String gender = (String) genderCombo.getSelectedItem();
            String role = "医护人员".equals(roleCombo.getSelectedItem()) ? "doctor" : "patient";
            String idCard = idCardField.getText().trim();

            // 验证逻辑
            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty() || birthDate == null) {
                JOptionPane.showMessageDialog(dialog, "请填写所有必填字段", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的密码不一致", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(dialog, "密码长度不能少于6位", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!phone.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的11位手机号码", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ("doctor".equals(role) && (idCard.length() != 18 || !idCard.matches("\\d{17}[0-9X]"))) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的18位身份证号码", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UserDAO userDAO = new UserDAO();
            if (userDAO.isUsernameExists(username)) {
                JOptionPane.showMessageDialog(dialog, "用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 创建用户对象
            User newUser = new User(
                    0, username, password, role, true,
                    name, phone, birthDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate(),
                    gender, "doctor".equals(role) ? idCard : null
            );

            // 保存用户
            if (userDAO.addUser(newUser)) {
                JOptionPane.showMessageDialog(dialog, "注册成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "注册失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public void showLogin() {
        setVisible(true);
    }
}