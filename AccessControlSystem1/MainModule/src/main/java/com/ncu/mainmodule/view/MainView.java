//文件: MainView.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.ncu.mainmodule.view;


import com.wan.accessmodule.view.AccessView;
import com.wan.adminmodule.view.AdminView;
import com.ncu.common.model.User;
import com.ncu.feemodule.view.FeeView;
import com.ncu.reportmodule.view.ReportView;
import com.ncu.vehiclemodule.view.VehicleView;


import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.*;

public class MainView {
    private JFrame mainFrame;
    //private AdminController adminController;
    private JPanel mainPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private JPanel currentPanel;
    private User currentUser;

    public MainView(User currentUser) {
        this.currentUser = currentUser;
        initMainView();
    }

    private void initMainView() {
        mainPanel = new JPanel(new BorderLayout());

        mainFrame = new JFrame("小区汽车门禁系统");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setLocationRelativeTo(null);

        // Left Panel
        leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        leftPanel.setBackground(new Color(0x5168C5));

        // Toolbar Buttons
        JButton homeButton = createToolButton("首页", "home.png");
        JButton vehicleButton = createToolButton("住户车辆管理", "vehicle.png");
        JButton feeButton = createToolButton("住户收费管理", "payment.png");
        JButton reportButton = createToolButton("统计报表", "report.png");
        JButton accessButton = createToolButton("车辆进出管理", "access.png");
        JButton adminButton = createToolButton("系统管理", "admin.png");
        JButton logoutButton = createToolButton("退出登录", "logout2.png");
        JButton exitButton = createToolButton("退出系统", "poweroff.png");
        JButton aboutButton = createToolButton("关于", "about.png");

        leftPanel.add(homeButton);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(vehicleButton);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(feeButton);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(reportButton);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(accessButton);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(adminButton);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(aboutButton);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logoutButton);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(exitButton);

        // Right Panel
        rightPanel = new JPanel(new BorderLayout());
        currentPanel = new JPanel();
        rightPanel.add(currentPanel, BorderLayout.CENTER);

        // Status Panel
        statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusLabel = new JLabel("当前用户: " + currentUser.getUsername());
        statusPanel.add(statusLabel);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Add Action Listeners
        homeButton.addActionListener(e -> showHomePanel());
        vehicleButton.addActionListener(e -> showVehiclePanel());
        feeButton.addActionListener(e -> showFeePanel());
        reportButton.addActionListener(e -> showReportPanel());
        accessButton.addActionListener(e -> showAccessPanel());
        adminButton.addActionListener(e -> showAdminPanel());
        logoutButton.addActionListener(e -> logout());
        exitButton.addActionListener(e -> System.exit(0));
        aboutButton.addActionListener(e -> showAboutPanel());

        // Admin Button Visibility
        reportButton.setVisible(currentUser.getRole().equals("admin"));
        adminButton.setVisible(currentUser.getRole().equals("admin"));

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

        showHomePanel();
    }

    private void setButtonStatus(JButton button, boolean isFocused) {
        final Color NORMAL_COLOR = new Color(0x95E1E7FF);
        final Color NORMAL_TEXT_COLOR = new Color(0x033BA5);
        final Border NORMAL_BORDER = new CompoundBorder(
                new LineBorder(new Color(0xB9BFD9), 1),
                new EmptyBorder(9, 19, 9, 19)
        );

        final Color FOCUS_COLOR = new Color(0xA4B4F4);
        final Color FOCUS_TEXT_COLOR = new Color(0xFFFFFF);
        final Border FOCUS_BORDER = new CompoundBorder(
                new LineBorder(Color.WHITE, 1),
                new EmptyBorder(9, 19, 9, 19)
        );

        if (isFocused) {
            button.setContentAreaFilled(true);
            button.setForeground(FOCUS_TEXT_COLOR); // 字体颜色
            button.setBackground(FOCUS_COLOR); // 背景色
            button.setBorder(FOCUS_BORDER);
        } else {
            button.setContentAreaFilled(true);
            button.setForeground(NORMAL_TEXT_COLOR); // 字体颜色
            button.setBackground(NORMAL_COLOR); // 浅灰色背景
            button.setBorder(NORMAL_BORDER);
        }
    }

    private JButton createToolButton(String text, String iconName) {
        JButton button = new JButton(text);
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/icons/" + iconName));
        button.setIcon(icon);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setMaximumSize(new Dimension(180, 50));
        button.setPreferredSize(new Dimension(180, 50));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));

        button.setFocusPainted(false); // 禁用焦点边框
        setButtonStatus(button, false);

        // 按钮的焦点状态
        button.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                setButtonStatus(button, true);
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                setButtonStatus(button, false);
            }
        });

        return button;
    }

    // Set Status Label
    public void setStatusLabel(String text) {
        statusLabel.setText(text);
    }

    //
    public void showCurrentPanel(JPanel newPanel) {
        currentPanel.removeAll();
        currentPanel.setLayout(new BorderLayout());
        currentPanel.setBackground(new Color(245, 245, 245));
        currentPanel.add(newPanel, BorderLayout.CENTER);
        currentPanel.revalidate();
        currentPanel.repaint();
    }

    public void showHomePanel() {
        mainFrame.setTitle("小区汽车门禁系统 - 首页");
        setStatusLabel("当前用户: " + currentUser.getUsername() + " | 首页");
        showCurrentPanel(getHomePanel());
    }

    private void showMessage(){
        JOptionPane.showMessageDialog(
                mainFrame,
                "此模块正在开发中......",
                "提示",
                JOptionPane.ERROR_MESSAGE);
    }

    // Show Admin Panel
    public void showAdminPanel() {
        if (!currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(mainFrame, "您没有权限访问此模块", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        mainFrame.setTitle("小区汽车门禁系统 - 后台管理");
        setStatusLabel("当前用户: " + currentUser.getUsername() + " | 后台管理");

        showCurrentPanel(new AdminView().getAdminPanel());
    }

    public void showVehiclePanel() {
        mainFrame.setTitle("小区汽车门禁系统 - 住户车辆管理");
        setStatusLabel("当前用户: " + currentUser.getUsername() + " | 住户车辆管理");

        showCurrentPanel(new VehicleView().getVehiclePanel());
    }

    public void showFeePanel() {
        mainFrame.setTitle("小区汽车门禁系统 - 住户收费管理");
        setStatusLabel("当前用户: " + currentUser.getUsername() + " | 住户收费管理");

        showCurrentPanel(new FeeView(currentUser).getFeePanel());
    }

    public void showReportPanel() {
        if (!currentUser.getRole().equals("admin")) {
            JOptionPane.showMessageDialog(
                    mainFrame,
                    "您没有权限访问此模块",
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mainFrame.setTitle("小区汽车门禁系统 - 统计报表");
        setStatusLabel("当前用户: " + currentUser.getUsername() + " | 统计报表");

        showCurrentPanel(new ReportView().getReportPanel());
    }

    public void showAccessPanel() {
        mainFrame.setTitle("小区汽车门禁系统 - 车辆进出管理");
        setStatusLabel("当前用户: " + currentUser.getUsername() + " | 车辆进出管理");

        showCurrentPanel(new AccessView().getAccessPanel());
    }

    public void showAboutPanel() {
        mainFrame.setTitle("小区汽车门禁系统 - 关于");
        setStatusLabel("当前用户: " + currentUser.getUsername() + " | 关于");
        showCurrentPanel(getAboutPanel());
    }

    public void logout() {
        mainFrame.dispose();
        new LoginView().showLogin();
    }

    public JPanel getHomePanel() {
        JLabel welcomeLabel = new JLabel("欢迎使用小区汽车门禁系统", JLabel.CENTER);
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(new Color(0x2F3D73));
        homePanel.add(welcomeLabel, BorderLayout.CENTER);
        return homePanel;
    }

    public JPanel getAboutPanel() {
        JPanel aboutPanel = new JPanel(new BorderLayout(10, 10));
        aboutPanel.setBackground(null);
        aboutPanel.setPreferredSize(new Dimension(400, 400));
        aboutPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        StringBuffer sbAboutInfo = new StringBuffer();
        sbAboutInfo.append("\n\n版本: 1.0.0\n\n");
        sbAboutInfo.append("作者: Wan Lizhong\n\n");
        sbAboutInfo.append("本程序为软件开发实训课程案例程序\n\n");
        sbAboutInfo.append("日期: " + LocalDate.now()+"\n\n\n");
        sbAboutInfo.append("All Rights Reserved (C) 2025");

        JTextArea aboutTextArea = new JTextArea(sbAboutInfo.toString());
        aboutTextArea.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        aboutTextArea.setForeground(Color.WHITE);
        aboutTextArea.setLineWrap(true);
        aboutTextArea.setWrapStyleWord(true);
        aboutTextArea.setEditable(false);
        aboutTextArea.setBackground(null); // 透明背景
        aboutTextArea.setBorder(null);

        JLabel titleLabel = new JLabel("小区汽车门禁系统", JLabel.LEFT);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        aboutPanel.add(titleLabel, BorderLayout.NORTH);
        aboutPanel.add(aboutTextArea, BorderLayout.CENTER);

        JPanel bkgPanel = new JPanel();
        bkgPanel.setBackground(new Color(0x2F3D73));
        bkgPanel.setLayout(new GridBagLayout());
        bkgPanel.add(aboutPanel);
        return bkgPanel;
    }

}
