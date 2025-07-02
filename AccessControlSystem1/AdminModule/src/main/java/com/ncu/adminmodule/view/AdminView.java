package com.ncu.adminmodule.view;

import com.ncu.adminmodule.controller.AdminController;
import com.ncu.common.model.User;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class AdminView {
    private AdminController adminController;

    public AdminView() {
        this.adminController = new AdminController();
    }

    public JPanel getAdminPanel() {
        JPanel adminPanel = new JPanel(new BorderLayout());

        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();

        // 用户管理面板
        JPanel userManagementPanel = createUserManagementPanel();
        tabbedPane.addTab("用户管理", userManagementPanel);

        adminPanel.add(tabbedPane, BorderLayout.CENTER);

        return adminPanel;
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 工具栏
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addUserButton = new JButton("添加用户");
        JButton resetPasswordButton = new JButton("重置密码");
        JButton refreshButton = new JButton("刷新");

        toolPanel.add(addUserButton);
        toolPanel.add(resetPasswordButton);
        toolPanel.add(refreshButton);

        // 用户表格
        String[] columns = {"ID", "用户名", "姓名", "电话", "角色", "性别", "出生日期", "创建时间"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 所有单元格不可编辑
            }
        };

        JTable userTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(userTable);

        // 加载用户数据
        adminController.loadUserData(model);

        // 添加组件到面板
        panel.add(toolPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 添加用户按钮事件
        addUserButton.addActionListener(e -> showAddUserDialog(model));

        // 重置密码按钮事件
        resetPasswordButton.addActionListener(e -> {
            int selectedRow = userTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "请先选择要重置密码的用户", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int userId = (int) model.getValueAt(selectedRow, 0);
            String username = (String) model.getValueAt(selectedRow, 1);

            String newPassword = JOptionPane.showInputDialog(panel,
                    "为用户 " + username + " 设置新密码:", "重置密码", JOptionPane.PLAIN_MESSAGE);

            if (newPassword != null && !newPassword.isEmpty()) {
                if (newPassword.length() < 6) {
                    JOptionPane.showMessageDialog(panel, "密码长度不能少于6位", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (adminController.resetPassword(userId, newPassword)) {
                    JOptionPane.showMessageDialog(panel, "密码重置成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, "密码重置失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 刷新按钮事件
        refreshButton.addActionListener(e -> adminController.loadUserData(model));

        return panel;
    }

    private void showAddUserDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog();
        dialog.setTitle("添加新用户");
        dialog.setModal(true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(20);
        panel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("初始密码:"), gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(passwordField, gbc);

        // 角色
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("角色:"), gbc);

        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "doctor"});
        panel.add(roleCombo, gbc);

        // 姓名
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("姓名:"), gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // 电话
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("电话:"), gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(20);
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

        // 身份证号（仅医护人员）
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("身份证号（医护人员）:"), gbc);

        gbc.gridx = 1;
        JTextField idCardField = new JTextField(20);
        idCardField.setEnabled(false);
        panel.add(idCardField, gbc);

        // 根据角色切换身份证号字段状态
        roleCombo.addActionListener(e -> {
            String selectedRole = (String) roleCombo.getSelectedItem();
            idCardField.setEnabled("doctor".equals(selectedRole));
        });

        // 添加按钮
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        JButton addButton = new JButton("添加");
        panel.add(addButton, gbc);

        addButton.addActionListener(e -> {
            // 获取输入数据
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            Date birthDate = birthDateChooser.getDate();
            String gender = (String) genderCombo.getSelectedItem();
            String idCard = idCardField.getText().trim();

            // 验证输入
            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请填写所有必填字段", "错误", JOptionPane.ERROR_MESSAGE);
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

            if (adminController.isUsernameExists(username)) {
                JOptionPane.showMessageDialog(dialog, "用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 添加用户
            LocalDate localBirthDate = birthDate != null ?
                    birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

            if (adminController.addUser(username, password, role, name, phone,
                    localBirthDate, gender, idCard)) {
                JOptionPane.showMessageDialog(dialog, "用户添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                adminController.loadUserData(model);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "用户添加失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }
}