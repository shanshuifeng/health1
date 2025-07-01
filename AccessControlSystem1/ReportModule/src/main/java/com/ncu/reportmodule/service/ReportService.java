//文件: ReportService.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.ncu.reportmodule.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ncu.common.util.DbUtil;

// 报表服务
public class ReportService {
    public Object[][] generateDailyReport(LocalDate date) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT " +
                "DATE(payment_time) AS report_date, " +
                "SUM(CASE WHEN payment_type = 'yearly' THEN amount ELSE 0 END) AS yearly_income, " +
                "SUM(CASE WHEN payment_type = 'monthly' THEN amount ELSE 0 END) AS monthly_income, " +
                "SUM(CASE WHEN payment_type = 'temporary' THEN amount ELSE 0 END) AS temporary_income, " +
                "SUM(CASE WHEN payment_type = 'recharge' THEN amount ELSE 0 END) AS recharge_income, " +
                "SUM(amount) AS total_income " +
                "FROM payment_records " +
                "WHERE DATE(payment_time) = ? " +
                "GROUP BY DATE(payment_time)";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Object[] row = {
                        rs.getDate("report_date"),
                        rs.getBigDecimal("yearly_income"),
                        rs.getBigDecimal("monthly_income"),
                        rs.getBigDecimal("temporary_income"),
                        rs.getBigDecimal("recharge_income"),
                        rs.getBigDecimal("total_income")
                };
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows.toArray(new Object[0][]);
    }

    public Object[][] generateMonthlyReport(int year, int month) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT " +
                "DATE_FORMAT(payment_time, '%Y-%m') AS report_month, " +
                "SUM(CASE WHEN payment_type = 'yearly' THEN amount ELSE 0 END) AS yearly_income, " +
                "SUM(CASE WHEN payment_type = 'monthly' THEN amount ELSE 0 END) AS monthly_income, " +
                "SUM(CASE WHEN payment_type = 'temporary' THEN amount ELSE 0 END) AS temporary_income, " +
                "SUM(CASE WHEN payment_type = 'recharge' THEN amount ELSE 0 END) AS recharge_income, " +
                "SUM(amount) AS total_income " +
                "FROM payment_records " +
                "WHERE YEAR(payment_time) = ? AND MONTH(payment_time) = ? " +
                "GROUP BY DATE_FORMAT(payment_time, '%Y-%m')";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Object[] row = {
                        rs.getString("report_month"),
                        rs.getBigDecimal("yearly_income"),
                        rs.getBigDecimal("monthly_income"),
                        rs.getBigDecimal("temporary_income"),
                        rs.getBigDecimal("recharge_income"),
                        rs.getBigDecimal("total_income")
                };
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows.toArray(new Object[0][]);
    }

    public Object[][] generateYearlyReport(int year) {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT " +
                "YEAR(payment_time) AS report_year, " +
                "SUM(CASE WHEN payment_type = 'yearly' THEN amount ELSE 0 END) AS yearly_income, " +
                "SUM(CASE WHEN payment_type = 'monthly' THEN amount ELSE 0 END) AS monthly_income, " +
                "SUM(CASE WHEN payment_type = 'temporary' THEN amount ELSE 0 END) AS temporary_income, " +
                "SUM(CASE WHEN payment_type = 'recharge' THEN amount ELSE 0 END) AS recharge_income, " +
                "SUM(amount) AS total_income " +
                "FROM payment_records " +
                "WHERE YEAR(payment_time) = ? " +
                "GROUP BY YEAR(payment_time)";

        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Object[] row = {
                        rs.getInt("report_year"),
                        rs.getBigDecimal("yearly_income"),
                        rs.getBigDecimal("monthly_income"),
                        rs.getBigDecimal("temporary_income"),
                        rs.getBigDecimal("recharge_income"),
                        rs.getBigDecimal("total_income")
                };
                rows.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rows.toArray(new Object[0][]);
    }

    public boolean exportToExcel(Object[][] data, String[] headers, String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("报表");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    if (data[i][j] instanceof Date) {
                        cell.setCellValue((Date) data[i][j]);
                    } else if (data[i][j] instanceof Number) {
                        cell.setCellValue(((Number) data[i][j]).doubleValue());
                    } else {
                        cell.setCellValue(String.valueOf(data[i][j]));
                    }
                }
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
