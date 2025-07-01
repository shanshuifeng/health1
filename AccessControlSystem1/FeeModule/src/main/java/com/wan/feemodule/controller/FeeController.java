//文件: FeeController.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.feemodule.controller;

import com.wan.feemodule.dao.PaymentRecordDAO;
import com.wan.feemodule.model.PaymentRecord;

import javax.swing.table.DefaultTableModel;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class FeeController {

    // Load Payment Records（(FeeView)
    public void loadPaymentRecords(DefaultTableModel model, String keyword, String recordType, Date startDate, Date endDate) {
        model.setRowCount(0);
        PaymentRecordDAO recordDAO = new PaymentRecordDAO();
        List<PaymentRecord> records;

        if (startDate != null && endDate != null) {
            records = recordDAO.getPaymentRecordsByDateRange(
                    startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            );
        } else {
            records = recordDAO.getPaymentRecordsByPlate(keyword);
        }

        // Filter record type
        if (!recordType.equals("全部")) {
            records.removeIf(record -> !record.getPaymentType().equals(recordType.toLowerCase()));
        }

        // Filter keyword
        if (!keyword.isEmpty()) {
            records.removeIf(record -> !record.getPlateNumber().contains(keyword));
        }

        for (PaymentRecord record : records) {
            String paymentType = "";
            switch (record.getPaymentType()) {
                case "yearly": paymentType = "包年"; break;
                case "monthly": paymentType = "包月"; break;
                case "temporary": paymentType = "临时"; break;
                case "recharge": paymentType = "充值"; break;
            }

            String paymentMethod = "";
            switch (record.getPaymentMethod()) {
                case "cash": paymentMethod = "现金"; break;
                case "wechat": paymentMethod = "微信"; break;
                case "alipay": paymentMethod = "支付宝"; break;
            }

            model.addRow(new Object[]{
                    record.getId(),
                    record.getPlateNumber(),
                    String.format("%.2f", record.getAmount()),
                    paymentType,
                    paymentMethod,
                    record.getPaymentTime(),
                    record.getOperatorId()
            });
        }
    }
}

