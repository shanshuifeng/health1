//文件: AccessController.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.accessmodule.controller;

import com.wan.accessmodule.dao.AccessRecordDAO;
import com.ncu.vehiclemodule.dao.ResidentVehicleDAO;
import com.wan.accessmodule.model.AccessRecord;
import com.wan.adminmodule.model.FeeRule;
import com.ncu.vehiclemodule.model.ResidentVehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class AccessController {
    // Load Current Vehicles（AccessView）
    public void loadCurrentVehicles(DefaultTableModel model) {
        model.setRowCount(0);
        AccessRecordDAO accessDAO = new AccessRecordDAO();
        ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();

        List<AccessRecord> records = accessDAO.getAccessRecordsByDateRange(
                LocalDate.now().minusDays(7), LocalDate.now()
        );

        records.removeIf(record -> record.getExitTime() != null);

        for (AccessRecord record : records) {
            ResidentVehicle vehicle = vehicleDAO.getResidentVehicleByPlate(record.getPlateNumber());
            String residentName = vehicle != null ? vehicle.getResidentName() : "临时车辆";

            model.addRow(new Object[]{
                    record.getId(),
                    record.getPlateNumber(),
                    record.getEntryTime(),
                    residentName
            });
        }
    }

    // Load Resident Plates（Access View）
    public void loadResidentPlates(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        comboBox.addItem(""); // 空选项

        ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
        List<ResidentVehicle> vehicles = vehicleDAO.searchResidentVehicles("");

        for (ResidentVehicle vehicle : vehicles) {
            comboBox.addItem(vehicle.getPlateNumber());
        }
    }

    // （AccessView）
    public void generatePlateImage(JLabel label, String plateNumber) {
        int plateWidth = 300;
        int plateHeight = 120;
        BufferedImage image = new BufferedImage(plateWidth, plateHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿（图形和文本）
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.BLUE);
        g.fillRoundRect(0, 0, plateWidth, plateHeight, 20, 20);
        g.setColor(Color.WHITE);
        Font plateFont = new Font("黑体", Font.BOLD, 48) ;
        g.setFont(plateFont);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(plateNumber);
        int textHeight = fm.getHeight();
        // 计算居中位置
        int x = (plateWidth - textWidth) / 2;
        int y = (plateHeight - textHeight) / 2 + fm.getAscent();

        g.drawString(plateNumber, x, y);
        g.setStroke(new BasicStroke(3));
        //g.drawRect(0, 0, plateWidth-3, plateHeight-3);
        g.drawRoundRect(0, 0, plateWidth-3, plateHeight-3, 20, 20);
        g.dispose();

        ImageIcon icon = new ImageIcon(image.getScaledInstance(plateWidth, plateHeight, Image.SCALE_SMOOTH));
        label.setIcon(icon);
    }

    // Calculate Temporary Fee（AccessView）
    public BigDecimal calculateTemporaryFee(long hours, FeeRule feeRule) {
        if (feeRule == null) return BigDecimal.ZERO;

        BigDecimal fee = BigDecimal.ZERO;

        // Calculate full days and remaining hours
        long fullDays = hours / 24;
        long remainingHours = hours % 24;

        // Apply daily max rate for full days
        fee = fee.add(feeRule.getDailyMaxRate().multiply(BigDecimal.valueOf(fullDays)));

        // Calculate remaining hours fee
        if (remainingHours > 0) {
            BigDecimal remainingFee = feeRule.getFirstHourRate();
            if (remainingHours > 1) {
                remainingFee = remainingFee.add(
                        feeRule.getSubsequentHourRate().multiply(BigDecimal.valueOf(remainingHours - 1))
                );
            }

            // Cap at daily max rate
            fee = fee.add(remainingFee.min(feeRule.getDailyMaxRate()));
        }

        return fee;
    }
    // （AccessView）
    public void displayVehicleInfo(JTextArea infoTextArea, String plateNumber) {
        ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
        AccessRecordDAO accessDAO = new AccessRecordDAO();

        ResidentVehicle vehicle = vehicleDAO.getResidentVehicleByPlate(plateNumber);
        AccessRecord latestRecord = accessDAO.getLatestEntryRecord(plateNumber);

        StringBuilder info = new StringBuilder();
        info.append("车牌号: ").append(plateNumber).append("\n\n");

        if (vehicle != null) {
            info.append("住户姓名: ").append(vehicle.getResidentName()).append("\n");
            info.append("联系电话: ").append(vehicle.getPhone()).append("\n");
            info.append("开始日期: ").append(vehicle.getStartDate()).append("\n");
            info.append("结束日期: ").append(vehicle.getEndDate()).append("\n");
            info.append("账户余额: ").append(String.format("%.2f", vehicle.getBalance())).append(" 元\n");

            // 检查是否即将到期
            if (vehicle.getEndDate() != null) {
                long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), vehicle.getEndDate());
                if (daysRemaining <= 7) {
                    info.append("\n警告: 该住户车辆将在 ").append(daysRemaining).append(" 天后到期!\n");
                }
            }
        } else {
            info.append("临时车辆\n");
        }

        if (latestRecord != null && latestRecord.getExitTime() == null) {
            info.append("\n当前状态: 已进入\n");
            info.append("进入时间: ").append(latestRecord.getEntryTime()).append("\n");
            long minutes = Duration.between(latestRecord.getEntryTime(), LocalDateTime.now()).toMinutes();
            info.append("已停留: ").append(minutes).append(" 分钟\n");
        } else {
            info.append("\n当前状态: 未进入\n");
        }

        infoTextArea.setText(info.toString());
    }
}
