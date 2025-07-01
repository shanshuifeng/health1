//文件: ReportView.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.reportmodule.view;

import com.toedter.calendar.JDateChooser;
import com.wan.reportmodule.service.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;

public class ReportView {

    public JPanel getReportPanel(){
        JPanel reportPanel = new JPanel(new BorderLayout());

        // 选项卡
        JTabbedPane tabbedPane = new JTabbedPane();

        // 日报表
        JPanel dailyPanel = new JPanel(new BorderLayout());

        // 日报表工具栏
        JPanel dailyToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton dailySearchButton = new JButton("查询");

        JDateChooser dailyDateChooser = new JDateChooser();
        dailyDateChooser.setDateFormatString("yyyy-MM-dd");
        dailyDateChooser.setDate(new Date());

        JButton dailyExportButton = new JButton("导出Excel");

        dailyToolPanel.add(new JLabel("日期:"));
        dailyToolPanel.add(dailyDateChooser);
        dailyToolPanel.add(dailySearchButton);
        dailyToolPanel.add(dailyExportButton);

        // 日报表表格
        String[] dailyColumns = {"日期", "包年收入", "包月收入", "临时停车收入", "充值收入", "总收入"};
        DefaultTableModel dailyModel = new DefaultTableModel(dailyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable dailyTable = new JTable(dailyModel);
        JScrollPane dailyScrollPane = new JScrollPane(dailyTable);

        dailyPanel.add(dailyToolPanel, BorderLayout.NORTH);
        dailyPanel.add(dailyScrollPane, BorderLayout.CENTER);

        // 月报表
        JPanel monthlyPanel = new JPanel(new BorderLayout());

        // 月报表工具栏
        JPanel monthlyToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton monthlySearchButton = new JButton("查询");
        JComboBox<Integer> yearComboBox = new JComboBox<>();
        JComboBox<Integer> monthComboBox = new JComboBox<>();

        // 初始化年份和月份
        int currentYear = Year.now().getValue();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedItem(currentYear);

        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }
        monthComboBox.setSelectedItem(LocalDate.now().getMonthValue());

        JButton monthlyExportButton = new JButton("导出Excel");

        monthlyToolPanel.add(new JLabel("年份:"));
        monthlyToolPanel.add(yearComboBox);
        monthlyToolPanel.add(new JLabel("月份:"));
        monthlyToolPanel.add(monthComboBox);
        monthlyToolPanel.add(monthlySearchButton);
        monthlyToolPanel.add(monthlyExportButton);

        // 月报表表格
        String[] monthlyColumns = {"月份", "包年收入", "包月收入", "临时停车收入", "充值收入", "总收入"};
        DefaultTableModel monthlyModel = new DefaultTableModel(monthlyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable monthlyTable = new JTable(monthlyModel);
        JScrollPane monthlyScrollPane = new JScrollPane(monthlyTable);

        monthlyPanel.add(monthlyToolPanel, BorderLayout.NORTH);
        monthlyPanel.add(monthlyScrollPane, BorderLayout.CENTER);

        // 年报表
        JPanel yearlyPanel = new JPanel(new BorderLayout());

        // 年报表工具栏
        JPanel yearlyToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton yearlySearchButton = new JButton("查询");
        JComboBox<Integer> yearlyYearComboBox = new JComboBox<>();

        // 初始化年份
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearlyYearComboBox.addItem(i);
        }
        yearlyYearComboBox.setSelectedItem(currentYear);

        JButton yearlyExportButton = new JButton("导出Excel");

        yearlyToolPanel.add(new JLabel("年份:"));
        yearlyToolPanel.add(yearlyYearComboBox);
        yearlyToolPanel.add(yearlySearchButton);
        yearlyToolPanel.add(yearlyExportButton);

        // 年报表表格
        String[] yearlyColumns = {"年份", "包年收入", "包月收入", "临时停车收入", "充值收入", "总收入"};
        DefaultTableModel yearlyModel = new DefaultTableModel(yearlyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable yearlyTable = new JTable(yearlyModel);
        JScrollPane yearlyScrollPane = new JScrollPane(yearlyTable);

        yearlyPanel.add(yearlyToolPanel, BorderLayout.NORTH);
        yearlyPanel.add(yearlyScrollPane, BorderLayout.CENTER);

        // 添加选项卡
        tabbedPane.addTab("日报表", dailyPanel);
        tabbedPane.addTab("月报表", monthlyPanel);
        tabbedPane.addTab("年报表", yearlyPanel);

        reportPanel.add(tabbedPane, BorderLayout.CENTER);

        // 事件处理
        dailySearchButton.addActionListener(e -> {
            Date selectedDate = dailyDateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(null, "请选择日期", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate date = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            ReportService reportService = new ReportService();
            Object[][] data = reportService.generateDailyReport(date);

            dailyModel.setRowCount(0);
            for (Object[] row : data) {
                dailyModel.addRow(row);
            }
        });

        dailyExportButton.addActionListener(e -> {
            if (dailyModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "没有数据可导出", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存日报表");
            fileChooser.setSelectedFile(new File("日报表_" + LocalDate.now() + ".xlsx"));

            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                ReportService reportService = new ReportService();
                boolean success = reportService.exportToExcel(
                        getTableData(dailyModel),
                        dailyColumns,
                        fileToSave.getAbsolutePath()
                );

                if (success) {
                    JOptionPane.showMessageDialog(null, "导出成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "导出失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        monthlySearchButton.addActionListener(e -> {
            int year = (int) yearComboBox.getSelectedItem();
            int month = (int) monthComboBox.getSelectedItem();

            ReportService reportService = new ReportService();
            Object[][] data = reportService.generateMonthlyReport(year, month);

            monthlyModel.setRowCount(0);
            for (Object[] row : data) {
                monthlyModel.addRow(row);
            }
        });

        monthlyExportButton.addActionListener(e -> {
            if (monthlyModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "没有数据可导出", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存月报表");
            fileChooser.setSelectedFile(new File("月报表_" + LocalDate.now().getYear() + "-" +
                    LocalDate.now().getMonthValue() + ".xlsx"));

            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                ReportService reportService = new ReportService();
                boolean success = reportService.exportToExcel(
                        getTableData(monthlyModel),
                        monthlyColumns,
                        fileToSave.getAbsolutePath()
                );

                if (success) {
                    JOptionPane.showMessageDialog(null, "导出成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "导出失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        yearlySearchButton.addActionListener(e -> {
            int year = (int) yearlyYearComboBox.getSelectedItem();

            ReportService reportService = new ReportService();
            Object[][] data = reportService.generateYearlyReport(year);

            yearlyModel.setRowCount(0);
            for (Object[] row : data) {
                yearlyModel.addRow(row);
            }
        });

        yearlyExportButton.addActionListener(e -> {
            if (yearlyModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "没有数据可导出", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("保存年报表");
            fileChooser.setSelectedFile(new File("年报表_" + LocalDate.now().getYear() + ".xlsx"));

            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                ReportService reportService = new ReportService();
                boolean success = reportService.exportToExcel(
                        getTableData(yearlyModel),
                        yearlyColumns,
                        fileToSave.getAbsolutePath()
                );

                if (success) {
                    JOptionPane.showMessageDialog(null, "导出成功", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "导出失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 默认加载当天日报表
        dailySearchButton.doClick();

        return reportPanel;

    }

    private Object[][] getTableData(DefaultTableModel model) {
        Object[][] data = new Object[model.getRowCount()][model.getColumnCount()];
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                data[i][j] = model.getValueAt(i, j);
            }
        }
        return data;
    }

}
