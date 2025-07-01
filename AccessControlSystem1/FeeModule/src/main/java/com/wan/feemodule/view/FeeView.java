//文件: FeeView.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.feemodule.view;

import com.toedter.calendar.JDateChooser;
import com.wan.feemodule.controller.FeeController;
import com.wan.vehiclemodule.controller.VehicleController;
import com.wan.adminmodule.dao.FeeRuleDAO;
import com.wan.adminmodule.model.FeeRule;
import com.wan.feemodule.dao.PaymentRecordDAO;
import com.wan.feemodule.model.PaymentRecord;
import com.wan.vehiclemodule.dao.ResidentVehicleDAO;
import com.wan.vehiclemodule.model.ResidentVehicle;
import com.wan.common.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class FeeView {
    private FeeController feeController;
    private VehicleController vehicleController;
    private User currentUser;

    public FeeView(User user) {
        feeController = new FeeController();
        vehicleController = new VehicleController();
        this.currentUser = user;
    }

    public JPanel getFeePanel(){
        JPanel feePanel = new JPanel(new BorderLayout());

        // 选项卡
        JTabbedPane tabbedPane = new JTabbedPane();

        // 收费选项卡
        JPanel chargePanel = new JPanel(new BorderLayout());

        // 收费工具栏
        JPanel chargeToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton chargeButton = new JButton("收费");
        JButton rechargeButton = new JButton("充值");
        JButton refreshButton = new JButton("刷新");
        JButton searchButton = new JButton("搜索");
        JTextField searchField = new JTextField(20);

        chargeToolPanel.add(chargeButton);
        chargeToolPanel.add(rechargeButton);
        chargeToolPanel.add(refreshButton);
        chargeToolPanel.add(new JLabel("搜索:"));
        chargeToolPanel.add(searchField);
        chargeToolPanel.add(searchButton);

        // 收费表格
        String[] columns = {"ID", "住户姓名", "电话", "车牌号", "开始日期", "结束日期", "余额"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // 加载数据
        vehicleController.loadVehicleData(model, "");

        chargePanel.add(chargeToolPanel, BorderLayout.NORTH);
        chargePanel.add(scrollPane, BorderLayout.CENTER);

        // 收费记录选项卡
        JPanel recordPanel = new JPanel(new BorderLayout());

        // 记录工具栏
        JPanel recordToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchRecordButton = new JButton("搜索记录");
        JComboBox<String> recordTypeComboBox = new JComboBox<>(new String[]{"全部", "包年", "包月", "包时段", "充值"});
        JTextField recordSearchField = new JTextField(20);

        JDateChooser recordStartDateChooser = new JDateChooser();
        recordStartDateChooser.setDateFormatString("yyyy-MM-dd");

        JDateChooser recordEndDateChooser = new JDateChooser();
        recordEndDateChooser.setDateFormatString("yyyy-MM-dd");

        recordToolPanel.add(new JLabel("类型:"));
        recordToolPanel.add(recordTypeComboBox);
        recordToolPanel.add(new JLabel("搜索:"));
        recordToolPanel.add(recordSearchField);
        recordToolPanel.add(new JLabel("开始日期:"));
        recordToolPanel.add(recordStartDateChooser);
        recordToolPanel.add(new JLabel("结束日期:"));
        recordToolPanel.add(recordEndDateChooser);
        recordToolPanel.add(searchRecordButton);

        // 记录表格
        String[] recordColumns = {"ID", "车牌号", "金额", "收费类型", "收费方式", "收费时间", "操作员"};
        DefaultTableModel recordModel = new DefaultTableModel(recordColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable recordTable = new JTable(recordModel);
        JScrollPane recordScrollPane = new JScrollPane(recordTable);

        recordPanel.add(recordToolPanel, BorderLayout.NORTH);
        recordPanel.add(recordScrollPane, BorderLayout.CENTER);

        // 添加选项卡
        tabbedPane.addTab("住户车辆", chargePanel);
        tabbedPane.addTab("收费记录", recordPanel);

        feePanel.add(tabbedPane, BorderLayout.CENTER);

        // 事件处理
        chargeButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "请选择要收费的车辆", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int vehicleId = (int) table.getValueAt(selectedRow, 0);
            String plateNumber = (String) table.getValueAt(selectedRow, 3);
            ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
            ResidentVehicle vehicle = vehicleDAO.getResidentVehicleById(vehicleId);
            FeeRuleDAO feeRuleDAO = new FeeRuleDAO();
            FeeRule feeRule = feeRuleDAO.getFeeRule();

            if (feeRule == null) {
                JOptionPane.showMessageDialog(null, "请先设置收费规则", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog();
            dialog.setTitle("收费");
            dialog.setModal(true);
            dialog.setSize(500, 450);
            dialog.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel plateLabel = new JLabel("车牌号:");
            JTextField plateField = new JTextField(plateNumber);
            plateField.setEditable(false);

            JLabel typeLabel = new JLabel("收费类型:");
            JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"yearly", "monthly", "period"});

            JLabel dailyFeeLabel = new JLabel("每天收费标准:");
            JTextField dailyFeeField = new JTextField(feeRule.getDailyFee().toString());
            dailyFeeField.setEditable(false);

            JLabel startDateLabel = new JLabel("开始日期:");
            JTextField startDateField = new JTextField(vehicle.getEndDate() != null && !vehicle.getEndDate().isBefore(LocalDate.now()) ?
                    vehicle.getEndDate().plusDays(1).toString() : LocalDate.now().toString());
            startDateField.setEditable(false);

            JLabel endDateLabel = new JLabel("结束日期:");
            JTextField endDateField = new JTextField();
            endDateField.setEditable(false);

            JLabel amountLabel = new JLabel("金额:");
            JTextField amountField = new JTextField();
            amountField.setEditable(false);

            JLabel methodLabel = new JLabel("收费方式:");
            JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"cash", "wechat", "alipay"});

            panel.add(plateLabel);
            panel.add(plateField);
            panel.add(typeLabel);
            panel.add(typeComboBox);
            panel.add(dailyFeeLabel);
            panel.add(dailyFeeField);
            panel.add(startDateLabel);
            panel.add(startDateField);
            panel.add(endDateLabel);
            panel.add(endDateField);
            panel.add(amountLabel);
            panel.add(amountField);
            panel.add(methodLabel);
            panel.add(methodComboBox);

            // Update amount when type changes
            PropertyChangeListener feeCalculator = evt -> {
                String paymentType = (String) typeComboBox.getSelectedItem();
                LocalDate startDate = LocalDate.parse(startDateField.getText());

                BigDecimal amount = BigDecimal.ZERO;
                LocalDate endDate = startDate;

                switch (paymentType) {
                    case "yearly":
                        amount = feeRule.getYearlyFee();
                        endDate = startDate.plusYears(1);
                        break;
                    case "monthly":
                        amount = feeRule.getMonthlyFee();
                        endDate = startDate.plusMonths(1);
                        break;
                    case "period":
                        // For period, we'll show a date chooser
                        JDateChooser periodEndDateChooser = new JDateChooser();
                        periodEndDateChooser.setDateFormatString("yyyy-MM-dd");
                        periodEndDateChooser.setDate(java.sql.Date.valueOf(startDate.plusDays(1)));

                        int result = JOptionPane.showConfirmDialog(null, periodEndDateChooser,
                                "选择结束日期", JOptionPane.OK_CANCEL_OPTION);

                        if (result == JOptionPane.OK_OPTION) {
                            endDate = periodEndDateChooser.getDate().toInstant()
                                    .atZone(ZoneId.systemDefault()).toLocalDate();
                            long days = ChronoUnit.DAYS.between(startDate, endDate);
                            amount = feeRule.getDailyFee().multiply(BigDecimal.valueOf(days));
                        }
                        break;
                }

                amountField.setText(amount.toString());
                endDateField.setText(endDate.toString());
            };

            typeComboBox.addActionListener(e1 -> feeCalculator.propertyChange(null));

            // Initial calculation
            typeComboBox.setSelectedIndex(0);
            feeCalculator.propertyChange(null);

            JButton saveButton = new JButton("收费");
            saveButton.addActionListener(ev -> {
                try {
                    String paymentType = (String) typeComboBox.getSelectedItem();
                    BigDecimal amount = new BigDecimal(amountField.getText());
                    String paymentMethod = (String) methodComboBox.getSelectedItem();
                    LocalDate startDate = LocalDate.parse(startDateField.getText());
                    LocalDate endDate = LocalDate.parse(endDateField.getText());

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(dialog, "金额必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Calculate new balance (existing balance + new payment)
                    BigDecimal newBalance = vehicle.getBalance().add(amount);

                    // Create payment record
                    PaymentRecord record = new PaymentRecord(0, vehicleId, plateNumber,
                            amount, paymentType, paymentMethod,
                            LocalDateTime.now(), currentUser.getId());

                    PaymentRecordDAO recordDAO = new PaymentRecordDAO();

                    // Update vehicle with new balance and end date
                    vehicle.setStartDate(startDate);
                    vehicle.setEndDate(endDate);
                    vehicle.setBalance(newBalance);

                    if (vehicleDAO.updateResidentVehicle(vehicle)) {
                        // Add payment record
                        if (recordDAO.addPaymentRecord(record)) {
                            JOptionPane.showMessageDialog(dialog, "收费成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                            vehicleController.loadVehicleData(model, "");
                        } else {
                            JOptionPane.showMessageDialog(dialog, "收费记录添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "车辆信息更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "金额必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton cancelButton = new JButton("取消");
            cancelButton.addActionListener(ev -> dialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        });

        rechargeButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "请选择要充值的车辆", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int vehicleId = (int) table.getValueAt(selectedRow, 0);
            String plateNumber = (String) table.getValueAt(selectedRow, 3);
            ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
            ResidentVehicle vehicle = vehicleDAO.getResidentVehicleById(vehicleId);
            FeeRuleDAO feeRuleDAO = new FeeRuleDAO();
            FeeRule feeRule = feeRuleDAO.getFeeRule();

            if (feeRule == null) {
                JOptionPane.showMessageDialog(null, "请先设置收费规则", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog();
            dialog.setTitle("充值");
            dialog.setModal(true);
            dialog.setSize(400, 250);
            dialog.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel plateLabel = new JLabel("车牌号:");
            JTextField plateField = new JTextField(plateNumber);
            plateField.setEditable(false);

            JLabel amountLabel = new JLabel("充值金额:");
            JTextField amountField = new JTextField();

            JLabel methodLabel = new JLabel("充值方式:");
            JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"cash", "wechat", "alipay"});

            panel.add(plateLabel);
            panel.add(plateField);
            panel.add(amountLabel);
            panel.add(amountField);
            panel.add(methodLabel);
            panel.add(methodComboBox);

            JButton saveButton = new JButton("充值");
            saveButton.addActionListener(ev -> {
                try {
                    BigDecimal amount = new BigDecimal(amountField.getText());
                    String paymentMethod = (String) methodComboBox.getSelectedItem();

                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        JOptionPane.showMessageDialog(dialog, "充值金额必须大于0", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Calculate new balance and end date
                    BigDecimal newBalance = vehicle.getBalance().add(amount);
                    long days = newBalance.divide(feeRule.getDailyFee(), 0, BigDecimal.ROUND_DOWN).longValue();
                    LocalDate endDate = vehicle.getStartDate().plusDays(days);

                    // 创建充值记录
                    PaymentRecord record = new PaymentRecord(0, vehicleId, plateNumber,
                            amount, "recharge", paymentMethod,
                            LocalDateTime.now(), currentUser.getId());

                    PaymentRecordDAO recordDAO = new PaymentRecordDAO();

                    // 更新车辆余额和结束日期
                    vehicle.setBalance(newBalance);
                    vehicle.setEndDate(endDate);

                    if (vehicleDAO.updateResidentVehicle(vehicle)) {
                        // 添加充值记录
                        if (recordDAO.addPaymentRecord(record)) {
                            JOptionPane.showMessageDialog(dialog, "充值成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                            vehicleController.loadVehicleData(model, "");
                        } else {
                            JOptionPane.showMessageDialog(dialog, "充值记录添加失败", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "车辆信息更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "金额必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton cancelButton = new JButton("取消");
            cancelButton.addActionListener(ev -> dialog.dispose());

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        });

        refreshButton.addActionListener(e -> vehicleController.loadVehicleData(model, ""));

        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            vehicleController.loadVehicleData(model, keyword);
        });

        searchField.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            vehicleController.loadVehicleData(model, keyword);
        });

        searchRecordButton.addActionListener(e -> {
            String keyword = recordSearchField.getText().trim();
            String recordType = (String) recordTypeComboBox.getSelectedItem();
            Date startDate = recordStartDateChooser.getDate();
            Date endDate = recordEndDateChooser.getDate();

            feeController.loadPaymentRecords(recordModel, keyword, recordType, startDate, endDate);
        });

        return feePanel;
    }
}
