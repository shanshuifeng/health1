//文件: AdminView.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.ncu.adminmodule.view;

import com.ncu.adminmodule.controller.AdminController;
import com.ncu.adminmodule.dao.FeeRuleDAO;
import com.ncu.adminmodule.model.FeeRule;
import com.ncu.common.dao.UserDAO;
import com.ncu.common.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;

public class AdminView {
    private AdminController adminController;

    public AdminView() {
        adminController = new AdminController();
    }

    public JPanel getAdminPanel(){
        JTabbedPane tabbedPane = new JTabbedPane();

        // User Management Panel
        JPanel userPanel = createUserManagementPanel();
        tabbedPane.addTab("用户管理", userPanel);

        // Fee Rules Panel
        JPanel feeRulesPanel = createFeeRulesPanel();
        tabbedPane.addTab("收费规则管理", feeRulesPanel);

        JPanel adminPanel = new JPanel(new BorderLayout());
        adminPanel.add(tabbedPane, BorderLayout.CENTER) ;
        return adminPanel;
    }

    private JPanel createUserManagementPanel() {
        JPanel userPanel = new JPanel(new BorderLayout());

        // User toolbar
        JPanel userToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addUserButton = new JButton("添加操作员");
        JButton resetPasswordButton = new JButton("重置密码");
        JButton deleteUserButton = new JButton("删除用户");
        JButton refreshButton = new JButton("刷新");

        userToolPanel.add(addUserButton);
        userToolPanel.add(resetPasswordButton);
        userToolPanel.add(deleteUserButton);
        userToolPanel.add(refreshButton);

        // User table
        String[] userColumns = {"ID", "用户名", "角色", "首次登录", "创建时间"};
        DefaultTableModel userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable userTable = new JTable(userTableModel);
        JScrollPane userScrollPane = new JScrollPane(userTable);

        // Load user data
        adminController.loadUserData(userTableModel);

        userPanel.add(userToolPanel, BorderLayout.NORTH);
        userPanel.add(userScrollPane, BorderLayout.CENTER);

        // Event handlers for user management
        addUserButton.addActionListener(e -> {
            JTextField usernameField = new JTextField();
            JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"operator"});

            JPanel addUserPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            addUserPanel.add(new JLabel("用户名:"));
            addUserPanel.add(usernameField);
            addUserPanel.add(new JLabel("角色:"));
            addUserPanel.add(roleComboBox);

            int result = JOptionPane.showConfirmDialog(null, addUserPanel,
                    "添加操作员", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String username = usernameField.getText().trim();
                String role = (String) roleComboBox.getSelectedItem();

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "用户名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                User newUser = new User(0, username, UserDAO.OPERATOR_INITIAL_PASSWORD, role, true);
                UserDAO userDAO = new UserDAO();
                if (userDAO.addUser(newUser)) {
                    JOptionPane.showMessageDialog(null,
                            "操作员添加成功，初始密码为：" + UserDAO.OPERATOR_INITIAL_PASSWORD + "，首次登录必须修改密码！",
                            "成功", JOptionPane.INFORMATION_MESSAGE);
                    adminController.loadUserData(userTableModel);
                } else {
                    JOptionPane.showMessageDialog(null, "操作员添加失败，用户名可能已存在", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return userPanel;
    }

    // Create Fee Rules Panel
    private JPanel createFeeRulesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create a panel with vertical box layout to hold both fee rule panels
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Resident Fee Rules Panel
        JPanel residentPanel = new JPanel(new BorderLayout());
        residentPanel.setBorder(BorderFactory.createTitledBorder("住户收费规则"));
        residentPanel.setPreferredSize(new Dimension(600, 200));

        JPanel residentFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 10);

        JLabel dailyFeeLabel = new JLabel("每天收费标准(元):");
        dailyFeeLabel.setPreferredSize(new Dimension(150, 30));
        dailyFeeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        residentFormPanel.add(dailyFeeLabel, gbc);

        JTextField dailyFeeField = new JTextField();
        dailyFeeField.setPreferredSize(new Dimension(300, 30));
        dailyFeeField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        residentFormPanel.add(dailyFeeField, gbc);

        JLabel yearlyFeeLabel = new JLabel("包年费用(元):");
        yearlyFeeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        yearlyFeeLabel.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 0;
        gbc.gridy = 1;
        residentFormPanel.add(yearlyFeeLabel, gbc);

        JTextField yearlyFeeField = new JTextField();
        yearlyFeeField.setEditable(false);
        yearlyFeeField.setPreferredSize(new Dimension(300, 30));
        yearlyFeeField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        residentFormPanel.add(yearlyFeeField, gbc);

        JLabel monthlyFeeLabel = new JLabel("包月费用(元):");
        monthlyFeeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        monthlyFeeLabel.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 0;
        gbc.gridy = 2;
        residentFormPanel.add(monthlyFeeLabel, gbc);

        JTextField monthlyFeeField = new JTextField();
        monthlyFeeField.setEditable(false);
        monthlyFeeField.setPreferredSize(new Dimension(300, 30));
        monthlyFeeField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbc.gridx = 1;
        residentFormPanel.add(monthlyFeeField, gbc);

        JLabel noteLabel = new JLabel("注: 包年=365天, 包月=30天");
        noteLabel.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.gridy = 3;
        residentFormPanel.add(noteLabel, gbc);

        residentPanel.add(residentFormPanel, BorderLayout.CENTER);

        // Temporary Vehicle Fee Rules Panel
        JPanel tempVehiclePanel = new JPanel(new BorderLayout());
        tempVehiclePanel.setBorder(BorderFactory.createTitledBorder("临时车收费规则"));
        tempVehiclePanel.setPreferredSize(new Dimension(600, 200));

        JPanel tempVehicleFormPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcTemp = new GridBagConstraints();
        gbcTemp.insets = new Insets(10, 5, 10, 10);

        JLabel firstHourLabel = new JLabel("首小时收费(元):");
        firstHourLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        firstHourLabel.setPreferredSize(new Dimension(150, 30));
        gbcTemp.gridx = 0;
        gbcTemp.gridy = 0;
        tempVehicleFormPanel.add(firstHourLabel, gbcTemp);

        JTextField firstHourField = new JTextField();
        firstHourField.setPreferredSize(new Dimension(300, 30));
        firstHourField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbcTemp.gridx = 1;
        tempVehicleFormPanel.add(firstHourField, gbcTemp);

        JLabel subsequentHourLabel = new JLabel("后续每小时收费(元):");
        subsequentHourLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        subsequentHourLabel.setPreferredSize(new Dimension(150, 30));
        gbcTemp.gridx = 0;
        gbcTemp.gridy = 1;
        tempVehicleFormPanel.add(subsequentHourLabel, gbcTemp);

        JTextField subsequentHourField = new JTextField();
        subsequentHourField.setPreferredSize(new Dimension(300, 30));
        subsequentHourField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbcTemp.gridx = 1;
        tempVehicleFormPanel.add(subsequentHourField, gbcTemp);

        JLabel dailyMaxLabel = new JLabel("单日封顶收费(元):");
        dailyMaxLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        dailyMaxLabel.setPreferredSize(new Dimension(150, 30));
        gbcTemp.gridx = 0;
        gbcTemp.gridy = 2;
        tempVehicleFormPanel.add(dailyMaxLabel, gbcTemp);

        JTextField dailyMaxField = new JTextField();
        dailyMaxField.setPreferredSize(new Dimension(300, 30));
        dailyMaxField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        gbcTemp.gridx = 1;
        tempVehicleFormPanel.add(dailyMaxField, gbcTemp);

        tempVehiclePanel.add(tempVehicleFormPanel, BorderLayout.CENTER);

        // Add both panels to content panel
        contentPanel.add(residentPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(tempVehiclePanel);

        // Load current fee rules
        FeeRuleDAO feeRuleDAO = new FeeRuleDAO();
        FeeRule currentFeeRule = feeRuleDAO.getFeeRule();

        // Determine if we're adding new rules or updating existing ones
        final boolean[] hasExistingRules = {currentFeeRule != null};

        JButton saveButton = new JButton();
        // Set button texts based on whether rules exist
        saveButton.setText(hasExistingRules[0] ? "更新收费规则" : "添加收费规则");

        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(saveButton);
        contentPanel.add(Box.createVerticalStrut(20));

        if (currentFeeRule != null) {
            // Populate resident fee fields
            dailyFeeField.setText(currentFeeRule.getDailyFee().toString());
            yearlyFeeField.setText(currentFeeRule.getYearlyFee().toString());
            monthlyFeeField.setText(currentFeeRule.getMonthlyFee().toString());

            // Populate temporary vehicle fee fields
            firstHourField.setText(currentFeeRule.getFirstHourRate().toString());
            subsequentHourField.setText(currentFeeRule.getSubsequentHourRate().toString());
            dailyMaxField.setText(currentFeeRule.getDailyMaxRate().toString());
        }

        // Calculate fees when daily fee changes
        dailyFeeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateFees(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateFees(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateFees(); }

            private void updateFees() {
                try {
                    BigDecimal dailyFee = new BigDecimal(dailyFeeField.getText());
                    yearlyFeeField.setText(dailyFee.multiply(BigDecimal.valueOf(365)).toString());
                    monthlyFeeField.setText(dailyFee.multiply(BigDecimal.valueOf(30)).toString());
                } catch (NumberFormatException ex) {
                    // Ignore while typing
                }
            }
        });

        saveButton.addActionListener(e -> {
            try {
                BigDecimal dailyFee = new BigDecimal(dailyFeeField.getText());
                BigDecimal yearlyFee = dailyFee.multiply(BigDecimal.valueOf(365));
                BigDecimal monthlyFee = dailyFee.multiply(BigDecimal.valueOf(30));

                if (dailyFee.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(null, "每天收费标准必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                BigDecimal firstHourRate = new BigDecimal(firstHourField.getText());
                BigDecimal subsequentHourRate = new BigDecimal(subsequentHourField.getText());
                BigDecimal dailyMaxRate = new BigDecimal(dailyMaxField.getText());

                if (firstHourRate.compareTo(BigDecimal.ZERO) < 0 ||
                        subsequentHourRate.compareTo(BigDecimal.ZERO) < 0 ||
                        dailyMaxRate.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(null, "费用不能为负数", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FeeRule newFeeRule = new FeeRule(
                        0, yearlyFee, monthlyFee, dailyFee,
                        firstHourRate, subsequentHourRate, dailyMaxRate);

                boolean success;
                if (hasExistingRules[0]) {
                    success = feeRuleDAO.updateFeeRule(newFeeRule);
                } else {
                    success = feeRuleDAO.insertFeeRule(newFeeRule);
                }

                if (success) {
                    yearlyFeeField.setText(yearlyFee.toString());
                    monthlyFeeField.setText(monthlyFee.toString());
                    String message = hasExistingRules[0] ? "收费规则更新成功" : "收费规则添加成功";
                    JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);

                    // Update button text if this was the first time adding rules
                    if (!hasExistingRules[0]) {
                        hasExistingRules[0] = true;
                        saveButton.setText("更新收费规则");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "操作失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "请输入有效的数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

}


