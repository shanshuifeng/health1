//文件: PaymentRecordDAO.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.feemodule.dao;

import com.wan.feemodule.model.PaymentRecord;
import com.wan.common.util.DbUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 收费记录DAO
public class PaymentRecordDAO {
    public boolean addPaymentRecord(PaymentRecord record) {
        String sql = "INSERT INTO payment_records (resident_id, plate_number, amount, " +
                "payment_type, payment_method, operator_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, record.getResidentId() > 0 ? record.getResidentId() : null);
            stmt.setString(2, record.getPlateNumber());
            stmt.setBigDecimal(3, record.getAmount());
            stmt.setString(4, record.getPaymentType());
            stmt.setString(5, record.getPaymentMethod());
            stmt.setInt(6, record.getOperatorId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PaymentRecord> getPaymentRecordsByResident(int residentId) {
        List<PaymentRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM payment_records WHERE resident_id = ? ORDER BY payment_time DESC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, residentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(new PaymentRecord(
                        rs.getInt("id"),
                        rs.getInt("resident_id"),
                        rs.getString("plate_number"),
                        rs.getBigDecimal("amount"),
                        rs.getString("payment_type"),
                        rs.getString("payment_method"),
                        rs.getTimestamp("payment_time").toLocalDateTime(),
                        rs.getInt("operator_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<PaymentRecord> getPaymentRecordsByPlate(String plateNumber) {
        List<PaymentRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM payment_records WHERE plate_number = ? ORDER BY payment_time DESC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plateNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(new PaymentRecord(
                        rs.getInt("id"),
                        rs.getInt("resident_id"),
                        rs.getString("plate_number"),
                        rs.getBigDecimal("amount"),
                        rs.getString("payment_type"),
                        rs.getString("payment_method"),
                        rs.getTimestamp("payment_time").toLocalDateTime(),
                        rs.getInt("operator_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<PaymentRecord> getPaymentRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<PaymentRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM payment_records WHERE DATE(payment_time) BETWEEN ? AND ? ORDER BY payment_time DESC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(new PaymentRecord(
                        rs.getInt("id"),
                        rs.getInt("resident_id"),
                        rs.getString("plate_number"),
                        rs.getBigDecimal("amount"),
                        rs.getString("payment_type"),
                        rs.getString("payment_method"),
                        rs.getTimestamp("payment_time").toLocalDateTime(),
                        rs.getInt("operator_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}
