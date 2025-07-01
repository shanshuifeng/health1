//文件: LoginView.java ///////////////////////////////////////////////////////////////////////////////////////////

// LoginView.java
package com.ncu.wan.mainmodule.view;

import com.ncu.wan.mainmodule.controller.LoginController;
import com.wan.common.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginView implements LoginController.LoginListener {
    private JFrame loginFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private LoginController loginController;

    public LoginView() {
        this.loginController = new LoginController();
        this.loginController.setLoginListener(this);
    }

    public void showLogin() {
        loginFrame = new JFrame("登录");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 350);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setResizable(false);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        JLabel titleLabel = new JLabel("小区汽车门禁系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 10, 0);
        gbc.fill = GridBagConstraints.NONE;

        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("密    码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton loginButton = new JButton("登录");
        loginButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        loginButton.setPreferredSize(new Dimension(60, 35));
        loginButton.addActionListener(e -> performLogin());
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(loginButton, gbc);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        loginFrame.add(mainPanel);

        // 回车键登录
        passwordField.addActionListener(e -> performLogin());

        loginFrame.setVisible(true);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        loginController.handleLogin(username, password);
    }

    public void showChangePasswordDialog(User user) {
        JDialog dialog = new JDialog(loginFrame, "修改密码", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(loginFrame);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("首次登录，请修改密码", JLabel.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        JLabel newPasswordLabel = new JLabel("新密码:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(newPasswordLabel, gbc);

        JPasswordField newPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(newPasswordField, gbc);

        JLabel confirmPasswordLabel = new JLabel("确认密码:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(confirmPasswordLabel, gbc);

        JPasswordField confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(confirmPasswordField, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            loginController.handleChangePassword(user, newPassword, confirmPassword);
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    @Override
    public void onLoginSuccess(User user) {
        loginFrame.dispose();
        new com.ncu.wan.mainmodule.view.MainView(user);
    }

    @Override
    public void onFirstLogin(User user) {
        showChangePasswordDialog(user);
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        JOptionPane.showMessageDialog(loginFrame, errorMessage, "错误", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onPasswordChangeSuccess(User user) {
        JOptionPane.showMessageDialog(loginFrame, "密码修改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
        loginFrame.dispose();
        new com.ncu.wan.mainmodule.view.MainView(user);
    }

    @Override
    public void onPasswordChangeFailed(String errorMessage) {
        JOptionPane.showMessageDialog(loginFrame, errorMessage, "错误", JOptionPane.ERROR_MESSAGE);
    }

}
