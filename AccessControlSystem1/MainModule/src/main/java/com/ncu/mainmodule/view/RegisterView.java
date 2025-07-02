package com.ncu.mainmodule.view;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class RegisterView extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nameField;
    private JTextField phoneField;
    private JComboBox<String> genderComboBox;
    private JDateChooser birthDateChooser;
    private JComboBox<String> userTypeComboBox;
    private JTextField idCardField;
    private JButton registerButton;

    public RegisterView() {
        setTitle("健康检查系统 - 用户注册");
        setSize(500, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        JLabel titleLabel = new JLabel("用户注册", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // 用户名
        addLabelAndField(panel, gbc, 1, "用户名:", usernameField = new JTextField(20));

        // 密码
        addLabelAndField(panel, gbc, 2, "密码:", passwordField = new JPasswordField(20));

        // 确认密码
        addLabelAndField(panel, gbc, 3, "确认密码:", confirmPasswordField = new JPasswordField(20));

        // 姓名
        addLabelAndField(panel, gbc, 4, "姓名:", nameField = new JTextField(20));

        // 电话
        addLabelAndField(panel, gbc, 5, "电话:", phoneField = new JTextField(20));

        // 性别
        JLabel genderLabel = new JLabel("性别:");
        genderLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        panel.add(genderLabel, gbc);

        genderComboBox = new JComboBox<>(new String[]{"男", "女", "其他"});
        gbc.gridx = 1;
        panel.add(genderComboBox, gbc);

        // 出生日期
        JLabel birthLabel = new JLabel("出生日期:");
        birthLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(birthLabel, gbc);

        birthDateChooser = new JDateChooser();
        birthDateChooser.setDateFormatString("yyyy-MM-dd");
        birthDateChooser.setDate(Date.from(LocalDate.now().minusYears(20).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        gbc.gridx = 1;
        panel.add(birthDateChooser, gbc);

        // 用户类型
        JLabel userTypeLabel = new JLabel("用户类型:");
        userTypeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(userTypeLabel, gbc);

        userTypeComboBox = new JComboBox<>(new String[]{"普通用户", "医护人员"});
        userTypeComboBox.addActionListener(e -> {
            idCardField.setVisible(userTypeComboBox.getSelectedItem().equals("医护人员"));
        });
        gbc.gridx = 1;
        panel.add(userTypeComboBox, gbc);

        // 身份证号 (默认隐藏)
        JLabel idCardLabel = new JLabel("身份证号:");
        idCardLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(idCardLabel, gbc);

        idCardField = new JTextField(20);
        idCardField.setVisible(false);
        gbc.gridx = 1;
        panel.add(idCardField, gbc);

        // 注册按钮
        registerButton = new JButton("注册");
        registerButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            if (!validateInput()) {
                return;
            }

            // 这里添加注册逻辑
            JOptionPane.showMessageDialog(this, "注册成功!", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        add(panel);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private boolean validateInput() {
        // 验证用户名
        if (usernameField.getText().isEmpty()) {
            showError("用户名不能为空");
            return false;
        }

        // 验证密码
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (password.isEmpty()) {
            showError("密码不能为空");
            return false;
        }
        if (password.length() < 6) {
            showError("密码长度不能少于6位");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showError("两次输入的密码不一致");
            return false;
        }

        // 验证姓名
        if (nameField.getText().isEmpty()) {
            showError("姓名不能为空");
            return false;
        }

        // 验证电话
        String phone = phoneField.getText();
        if (phone.isEmpty()) {
            showError("电话不能为空");
            return false;
        }
        if (!phone.matches("\\d{11}")) {
            showError("请输入有效的11位手机号码");
            return false;
        }

        // 验证出生日期
        if (birthDateChooser.getDate() == null) {
            showError("请选择出生日期");
            return false;
        }

        // 验证医护人员身份证号
        if (userTypeComboBox.getSelectedItem().equals("医护人员")) {
            String idCard = idCardField.getText();
            if (idCard.isEmpty()) {
                showError("医护人员必须填写身份证号");
                return false;
            }
            if (!idCard.matches("\\d{17}[\\dXx]")) {
                showError("请输入有效的18位身份证号");
                return false;
            }
        }

        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}