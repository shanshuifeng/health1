//文件: VehicleView.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.ncu.vehiclemodule.view;

import com.ncu.vehiclemodule.controller.VehicleController;
import com.wan.adminmodule.dao.FeeRuleDAO;
import com.wan.adminmodule.model.FeeRule;
import com.ncu.vehiclemodule.dao.ResidentVehicleDAO;
import com.ncu.vehiclemodule.model.ResidentVehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class VehicleView {

    private VehicleController vehicleController;

    public VehicleView() {
        vehicleController = new VehicleController();
    }

    public JPanel getVehiclePanel(){
        // Toolbar
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("添加车辆");
        JButton editButton = new JButton("修改车辆");
        JButton deleteButton = new JButton("删除车辆");
        JButton refreshButton = new JButton("刷新");
        JButton searchButton = new JButton("搜索");
        JTextField searchField = new JTextField(20);

        toolPanel.add(addButton);
        toolPanel.add(editButton);
        toolPanel.add(deleteButton);
        toolPanel.add(refreshButton);
        toolPanel.add(new JLabel("搜索:"));
        toolPanel.add(searchField);
        toolPanel.add(searchButton);

        // Table
        String[] columns = {"ID", "住户姓名", "电话", "车牌号", "开始日期", "结束日期", "余额"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load data
        vehicleController.loadVehicleData(model, "");

        JPanel vehiclePanel = new JPanel(new BorderLayout());
        vehiclePanel.add(toolPanel, BorderLayout.NORTH);
        vehiclePanel.add(scrollPane, BorderLayout.CENTER);

        // Event handlers
        addButton.addActionListener(e -> {
            JDialog dialog = new JDialog();
            dialog.setModal(true);
            dialog.setTitle("添加住户车辆");
            dialog.setSize(500, 350);
            dialog.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel nameLabel = new JLabel("住户姓名:");
            JTextField nameField = new JTextField();

            JLabel phoneLabel = new JLabel("电话:");
            JTextField phoneField = new JTextField();

            JLabel plateLabel = new JLabel("车牌号:");
            JTextField plateField = new JTextField();

            JLabel startDateLabel = new JLabel("开始日期:");
            JTextField startDateField = new JTextField(LocalDate.now().toString());

            JLabel balanceLabel = new JLabel("初始余额:");
            JTextField balanceField = new JTextField("0");
            balanceField.setEditable(false);

            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(phoneLabel);
            panel.add(phoneField);
            panel.add(plateLabel);
            panel.add(plateField);
            panel.add(startDateLabel);
            panel.add(startDateField);
            panel.add(balanceLabel);
            panel.add(balanceField);

            JButton saveButton = new JButton("保存");
            saveButton.addActionListener(ev -> {
                try {
                    String residentName = nameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    String plateNumber = plateField.getText().trim().toUpperCase();
                    LocalDate startDate = LocalDate.parse(startDateField.getText());
                    BigDecimal balance = new BigDecimal(balanceField.getText());

                    if (residentName.isEmpty() || phone.isEmpty() || plateNumber.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "请填写完整信息", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (!phone.matches("\\d{7,15}")) {
                        JOptionPane.showMessageDialog(dialog, "电话号码格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (!plateNumber.matches("[\\u4e00-\\u9fa5]{1}[A-Z]{1}[A-Z0-9]{5}")) {
                        JOptionPane.showMessageDialog(dialog, "车牌号格式不正确(示例:京A12345)", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (balance.compareTo(BigDecimal.ZERO) < 0) {
                        JOptionPane.showMessageDialog(dialog, "余额不能为负数", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Calculate end date based on balance and daily fee
                    FeeRuleDAO feeRuleDAO = new FeeRuleDAO();
                    FeeRule feeRule = feeRuleDAO.getFeeRule();
                    LocalDate endDate = startDate;
                    if (feeRule != null && feeRule.getDailyFee().compareTo(BigDecimal.ZERO) > 0) {
                        long days = balance.divide(feeRule.getDailyFee(), 0, BigDecimal.ROUND_DOWN).longValue();
                        endDate = startDate.plusDays(days);
                    }

                    ResidentVehicle vehicle = new ResidentVehicle(0, residentName, phone, plateNumber, startDate, endDate, balance);
                    ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
                    if (vehicleDAO.addResidentVehicle(vehicle)) {
                        JOptionPane.showMessageDialog(dialog, "车辆添加成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        vehicleController.loadVehicleData(model, "");
                    } else {
                        JOptionPane.showMessageDialog(dialog, "车辆添加失败，车牌号可能已存在", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "日期格式不正确 (YYYY-MM-DD)", "错误", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "余额必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
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

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "请选择要修改的车辆", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int vehicleId = (int) table.getValueAt(selectedRow, 0);
            ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
            ResidentVehicle vehicle = vehicleDAO.getResidentVehicleById(vehicleId);

            if (vehicle == null) {
                JOptionPane.showMessageDialog(null, "车辆信息不存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog dialog = new JDialog();
            dialog.setTitle("修改住户车辆");
            dialog.setModal(true);
            dialog.setSize(500, 350);
            dialog.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel nameLabel = new JLabel("住户姓名:");
            JTextField nameField = new JTextField(vehicle.getResidentName());

            JLabel phoneLabel = new JLabel("电话:");
            JTextField phoneField = new JTextField(vehicle.getPhone());

            JLabel plateLabel = new JLabel("车牌号:");
            JTextField plateField = new JTextField(vehicle.getPlateNumber());
            plateField.setEditable(false);

            JLabel startDateLabel = new JLabel("开始日期:");
            JTextField startDateField = new JTextField(vehicle.getStartDate().toString());

            JLabel balanceLabel = new JLabel("余额:");
            JTextField balanceField = new JTextField(vehicle.getBalance().toString());
            balanceField.setEditable(false);

            panel.add(nameLabel);
            panel.add(nameField);
            panel.add(phoneLabel);
            panel.add(phoneField);
            panel.add(plateLabel);
            panel.add(plateField);
            panel.add(startDateLabel);
            panel.add(startDateField);
            panel.add(balanceLabel);
            panel.add(balanceField);

            JButton saveButton = new JButton("保存");
            saveButton.addActionListener(ev -> {
                try {
                    String residentName = nameField.getText().trim();
                    String phone = phoneField.getText().trim();
                    LocalDate startDate = LocalDate.parse(startDateField.getText());

                    if (residentName.isEmpty() || phone.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "请填写完整信息", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (!phone.matches("\\d{7,15}")) {
                        JOptionPane.showMessageDialog(dialog, "电话号码格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Calculate new end date based on balance and new start date
                    FeeRuleDAO feeRuleDAO = new FeeRuleDAO();
                    FeeRule feeRule = feeRuleDAO.getFeeRule();
                    LocalDate endDate = startDate;
                    if (feeRule != null && feeRule.getDailyFee().compareTo(BigDecimal.ZERO) > 0) {
                        long days = vehicle.getBalance().divide(feeRule.getDailyFee(), 0, BigDecimal.ROUND_DOWN).longValue();
                        endDate = startDate.plusDays(days);
                    }

                    ResidentVehicle updatedVehicle = new ResidentVehicle(vehicleId, residentName, phone,
                            vehicle.getPlateNumber(), startDate, endDate, vehicle.getBalance());

                    if (vehicleDAO.updateResidentVehicle(updatedVehicle)) {
                        JOptionPane.showMessageDialog(dialog, "车辆信息更新成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        vehicleController.loadVehicleData(model, "");
                    } else {
                        JOptionPane.showMessageDialog(dialog, "车辆信息更新失败", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "日期格式不正确 (YYYY-MM-DD)", "错误", JOptionPane.ERROR_MESSAGE);
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

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "请选择要删除的车辆", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int vehicleId = (int) table.getValueAt(selectedRow, 0);
            String plateNumber = (String) table.getValueAt(selectedRow, 3);
            BigDecimal balance = new BigDecimal(String.valueOf(table.getValueAt(selectedRow, 6)));

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "该车辆还有余额 " + balance + " 元未使用，确定要删除吗?", "确认", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
            if (vehicleDAO.deleteResidentVehicle(vehicleId)) {
                JOptionPane.showMessageDialog(null, "车辆删除成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                vehicleController.loadVehicleData(model, "");
            } else {
                JOptionPane.showMessageDialog(null, "车辆删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
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

        return vehiclePanel;
    }
}
