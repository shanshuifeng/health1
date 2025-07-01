//文件: AccessRecordDAO.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.accessmodule.dao;

import com.wan.accessmodule.model.AccessRecord;
import com.wan.common.util.DbUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// 车辆进出记录DAO
public class AccessRecordDAO {
    public boolean addAccessRecord(AccessRecord record) {
        String sql = "INSERT INTO access_records (plate_number, entry_time, resident_id) VALUES (?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, record.getPlateNumber());
            stmt.setTimestamp(2, Timestamp.valueOf(record.getEntryTime()));
            stmt.setObject(3, record.getResidentId() != null ? record.getResidentId() : null);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAccessRecordExit(AccessRecord record) {
        String sql = "UPDATE access_records SET exit_time = ?, fee = ?, paid = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(record.getExitTime()));
            stmt.setBigDecimal(2, record.getFee());
            stmt.setBoolean(3, record.isPaid());
            stmt.setInt(4, record.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AccessRecord getLatestEntryRecord(String plateNumber) {
        String sql = "SELECT * FROM access_records WHERE plate_number = ? AND exit_time IS NULL ORDER BY entry_time DESC LIMIT 1";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plateNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AccessRecord(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getTimestamp("entry_time").toLocalDateTime(),
                        null,
                        BigDecimal.ZERO,
                        false,
                        rs.getInt("resident_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AccessRecord> getAccessRecordsByPlate(String plateNumber) {
        List<AccessRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM access_records WHERE plate_number = ? ORDER BY entry_time DESC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plateNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(new AccessRecord(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getTimestamp("entry_time").toLocalDateTime(),
                        rs.getTimestamp("exit_time") != null ? rs.getTimestamp("exit_time").toLocalDateTime() : null,
                        rs.getBigDecimal("fee"),
                        rs.getBoolean("paid"),
                        rs.getInt("resident_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    public List<AccessRecord> getAccessRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<AccessRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM access_records WHERE DATE(entry_time) BETWEEN ? AND ? ORDER BY entry_time DESC";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                records.add(new AccessRecord(
                        rs.getInt("id"),
                        rs.getString("plate_number"),
                        rs.getTimestamp("entry_time").toLocalDateTime(),
                        rs.getTimestamp("exit_time") != null ? rs.getTimestamp("exit_time").toLocalDateTime() : null,
                        rs.getBigDecimal("fee"),
                        rs.getBoolean("paid"),
                        rs.getInt("resident_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}
