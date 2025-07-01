//文件: AccessView.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.accessmodule.view;

import com.toedter.calendar.JDateChooser;
import com.wan.accessmodule.controller.AccessController;
import com.wan.accessmodule.dao.AccessRecordDAO;
import com.wan.accessmodule.model.AccessRecord;
import com.wan.adminmodule.dao.FeeRuleDAO;
import com.wan.adminmodule.model.FeeRule;
import com.ncu.vehiclemodule.dao.ResidentVehicleDAO;
import com.ncu.vehiclemodule.model.ResidentVehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class AccessView {
    private AccessController accessController;

    public AccessView() {
        accessController = new AccessController();
    }

    public JPanel getAccessPanel() {
        JPanel accessPanel = new JPanel(new BorderLayout());
        // 上方面板 - 当前已进入未离开的车辆列表
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("当前在场车辆"));

        // 工具栏
        JPanel topToolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton queryRecordsButton = new JButton("查询车辆进出记录");
        topToolPanel.add(queryRecordsButton);

        // 车辆表格
        String[] columns = {"ID", "车牌号", "进入时间", "车辆类型", "住户姓名"};
        DefaultTableModel vehicleTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable vehicleTable = new JTable(vehicleTableModel);
        JScrollPane vehicleScrollPane = new JScrollPane(vehicleTable);

        // 加载当前在场车辆数据
        accessController.loadCurrentVehicles(vehicleTableModel);

        topPanel.add(topToolPanel, BorderLayout.NORTH);
        topPanel.add(vehicleScrollPane, BorderLayout.CENTER);

        // 下方面板
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JSplitPane splitterBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitterBottom.setResizeWeight(0.3);
        splitterBottom.setDividerLocation(0.3);

        // 左侧面板 - 车牌操作区
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 住户车辆车牌下拉列表
        JPanel platePanel = new JPanel(new BorderLayout(0, 10));
        platePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel residentLabel = new JLabel("住户车辆车牌:");
        JComboBox<String> residentPlateCombo = new JComboBox<>();
        accessController.loadResidentPlates(residentPlateCombo);
        platePanel.add(residentLabel, BorderLayout.NORTH);
        platePanel.add(residentPlateCombo, BorderLayout.CENTER);

        // 生成临时车牌按钮
        JButton generateTempPlateButton = new JButton("生成临时车辆车牌");
        platePanel.add(generateTempPlateButton, BorderLayout.SOUTH);

        // 车辆信息显示区
        JTextArea vehicleInfoArea = new JTextArea();
        vehicleInfoArea.setEditable(false);
        JScrollPane infoScrollPane = new JScrollPane(vehicleInfoArea);

        leftPanel.add(platePanel, BorderLayout.NORTH);
        leftPanel.add(infoScrollPane, BorderLayout.CENTER);

        // 右侧面板 - 摄像头画面区
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("摄像头入口画面"));

        // 摄像头画面
        JLabel cameraLabel = new JLabel("", JLabel.CENTER);
        cameraLabel.setPreferredSize(new Dimension(400, 200));
        cameraLabel.setOpaque(true);
        cameraLabel.setBackground(Color.BLACK);
        rightPanel.add(cameraLabel, BorderLayout.CENTER);

        // 操作按钮
        JButton accessButton = new JButton("车辆进入");
        accessButton.setPreferredSize(new Dimension(200, 35));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(accessButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        splitterBottom.setLeftComponent(leftPanel);
        splitterBottom.setRightComponent(rightPanel);
        bottomPanel.add(splitterBottom, BorderLayout.CENTER);

        // 主面板布局
        JSplitPane mainSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        mainSplitter.setResizeWeight(0.5);
        accessPanel.add(mainSplitter, BorderLayout.CENTER);

        // 当前识别的车牌和状态
        String[] currentPlate = {null};
        boolean[] isEntryMode = {true};
        BufferedImage[] currentImage = {null};

        // 事件处理
        queryRecordsButton.addActionListener(e -> showAccessRecordsDialog());

        residentPlateCombo.addActionListener(e -> {
            String selectedPlate = (String) residentPlateCombo.getSelectedItem();
            if (selectedPlate != null && !selectedPlate.isEmpty()) {
                currentPlate[0] = selectedPlate;
                isEntryMode[0] = true;
                rightPanel.setBorder(BorderFactory.createTitledBorder("摄像头入口画面"));
                accessButton.setText("车辆进入");

                // 生成车牌图片
                accessController.generatePlateImage(cameraLabel, selectedPlate);
                accessController.displayVehicleInfo(vehicleInfoArea, selectedPlate);
            }
        });

        generateTempPlateButton.addActionListener(e -> {
            // 生成随机车牌
            String[] provinces = {"京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", "沪", "苏",
                    "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂",
                    "琼", "渝", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新"};

            String plateNumber = provinces[(int) (Math.random() * provinces.length)] +
                    (char) ('A' + (int) (Math.random() * 26)) +
                    String.format("%05d", (int) (Math.random() * 10000));

            currentPlate[0] = plateNumber;
            isEntryMode[0] = true;
            rightPanel.setBorder(BorderFactory.createTitledBorder("摄像头入口画面"));
            accessButton.setText("车辆进入");

            // 生成车牌图片
            accessController.generatePlateImage(cameraLabel, plateNumber);
            accessController.displayVehicleInfo(vehicleInfoArea, plateNumber);
        });

        vehicleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = vehicleTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String plateNumber = (String) vehicleTable.getValueAt(selectedRow, 1);
                    currentPlate[0] = plateNumber;
                    isEntryMode[0] = false;
                    rightPanel.setBorder(BorderFactory.createTitledBorder("摄像头出口画面"));
                    accessButton.setText("车辆离开");

                    // 生成车牌图片
                    accessController.generatePlateImage(cameraLabel, plateNumber);
                    accessController.displayVehicleInfo(vehicleInfoArea, plateNumber);
                }
            }
        });

        accessButton.addActionListener(e -> {
            if (currentPlate[0] == null) {
                JOptionPane.showMessageDialog(null, "请先选择或生成车牌", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            AccessRecordDAO accessDAO = new AccessRecordDAO();
            ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
            FeeRuleDAO feeRuleDAO = new FeeRuleDAO();

            if (isEntryMode[0]) {
                // 车辆进入处理
                AccessRecord existingRecord = accessDAO.getLatestEntryRecord(currentPlate[0]);
                if (existingRecord != null) {
                    JOptionPane.showMessageDialog(null, "该车辆已有未离开的记录", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                ResidentVehicle vehicle = vehicleDAO.getResidentVehicleByPlate(currentPlate[0]);
                Integer residentId = vehicle != null ? vehicle.getId() : null;

                AccessRecord record = new AccessRecord(0, currentPlate[0], LocalDateTime.now(), null, BigDecimal.ZERO, false, residentId);
                if (accessDAO.addAccessRecord(record)) {
                    JOptionPane.showMessageDialog(null, "车辆进入记录添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                    accessController.loadCurrentVehicles(vehicleTableModel);
                    accessController.displayVehicleInfo(vehicleInfoArea, currentPlate[0]);
                } else {
                    JOptionPane.showMessageDialog(null, "车辆进入记录添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // 车辆离开处理
                AccessRecord record = accessDAO.getLatestEntryRecord(currentPlate[0]);
                if (record == null) {
                    JOptionPane.showMessageDialog(null, "没有找到该车辆的进入记录", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDateTime exitTime = LocalDateTime.now();
                long minutes = Duration.between(record.getEntryTime(), exitTime).toMinutes();
                long hours = (minutes + 59) / 60;

                FeeRule feeRule = feeRuleDAO.getFeeRule();
                BigDecimal fee = BigDecimal.ZERO;
                ResidentVehicle vehicle = vehicleDAO.getResidentVehicleByPlate(currentPlate[0]);
                boolean isResident = vehicle != null;
                boolean isPaid = false;

                if (isResident) {
                    if (vehicle.getEndDate() != null && LocalDate.now().isAfter(vehicle.getEndDate())) {
                        fee = accessController.calculateTemporaryFee(hours, feeRule);
                        if (vehicle.getBalance().compareTo(fee) >= 0) {
                            if (vehicleDAO.updateVehicleBalance(vehicle.getId(), fee.negate())) {
                                isPaid = true;
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "住户余额不足，请充值", "警告", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                        isPaid = true;
                    }
                } else {
                    fee = accessController.calculateTemporaryFee(hours, feeRule);
                    int option = JOptionPane.showConfirmDialog(null,
                            "临时车辆 " + currentPlate[0] + " 需支付 " + fee + " 元\n是否现在支付?",
                            "支付确认", JOptionPane.YES_NO_OPTION);
                    isPaid = option == JOptionPane.YES_OPTION;
                }

                record.setExitTime(exitTime);
                record.setFee(fee);
                record.setPaid(isPaid);

                if (accessDAO.updateAccessRecordExit(record)) {
                    String message = "车辆离开记录更新成功\n";
                    message += "车牌号: " + currentPlate[0] + "\n";
                    message += "进入时间: " + record.getEntryTime() + "\n";
                    message += "离开时间: " + exitTime + "\n";
                    message += "停留时间: " + minutes + " 分钟\n";
                    message += "费用: " + fee + " 元\n";
                    message += "支付状态: " + (isPaid ? "已支付" : "未支付");

                    JOptionPane.showMessageDialog(null, message, "成功", JOptionPane.INFORMATION_MESSAGE);
                    accessController.loadCurrentVehicles(vehicleTableModel);
                    accessController.displayVehicleInfo(vehicleInfoArea, currentPlate[0]);
                } else {
                    JOptionPane.showMessageDialog(null, "车辆离开记录更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return accessPanel;
    }

    private void showAccessRecordsDialog() {
        JDialog dialog = new JDialog();
        dialog.setTitle("查询车辆进出记录");
        dialog.setModal(true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        // 查询工具栏
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("查询");
        JTextField plateSearchField = new JTextField(15);
        JDateChooser startDateChooser = new JDateChooser();
        JDateChooser endDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        endDateChooser.setDateFormatString("yyyy-MM-dd");

        toolPanel.add(new JLabel("车牌号:"));
        toolPanel.add(plateSearchField);
        toolPanel.add(new JLabel("开始日期:"));
        toolPanel.add(startDateChooser);
        toolPanel.add(new JLabel("结束日期:"));
        toolPanel.add(endDateChooser);
        toolPanel.add(searchButton);

        // 记录表格
        String[] columns = {"ID", "车牌号", "进入时间", "离开时间", "费用", "是否支付"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(toolPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String plateNumber = plateSearchField.getText().trim();
            Date startDate = startDateChooser.getDate();
            Date endDate = endDateChooser.getDate();

            AccessRecordDAO accessDAO = new AccessRecordDAO();
            java.util.List<AccessRecord> records;

            if (startDate != null && endDate != null) {
                records = accessDAO.getAccessRecordsByDateRange(
                        startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                );
            } else {
                records = accessDAO.getAccessRecordsByPlate(plateNumber);
            }

            if (!plateNumber.isEmpty()) {
                records.removeIf(record -> !record.getPlateNumber().contains(plateNumber));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            model.setRowCount(0);
            for (AccessRecord record : records) {
                model.addRow(new Object[]{
                        record.getId(),
                        record.getPlateNumber(),
                        sdf.format(Timestamp.valueOf(record.getEntryTime())),
                        record.getExitTime() != null ? sdf.format(Timestamp.valueOf(record.getExitTime())) : "",
                        String.format("%.2f", record.getFee()),
                        record.isPaid() ? "是" : "否"
                });
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }
}