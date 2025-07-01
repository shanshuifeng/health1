//文件: ResidentVehicleDAO.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.vehiclemodule.dao;

import com.wan.vehiclemodule.model.ResidentVehicle;
import com.wan.common.util.DbUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 住户车辆DAO
public class ResidentVehicleDAO {
    public boolean addResidentVehicle(ResidentVehicle vehicle) {
        String sql = "INSERT INTO resident_vehicles (resident_name, phone, plate_number, " +
                "start_date, end_date, balance) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getResidentName());
            stmt.setString(2, vehicle.getPhone());
            stmt.setString(3, vehicle.getPlateNumber());
            stmt.setDate(4, java.sql.Date.valueOf(vehicle.getStartDate()));
            stmt.setDate(5, vehicle.getEndDate() != null ? java.sql.Date.valueOf(vehicle.getEndDate()) : null);
            stmt.setBigDecimal(6, vehicle.getBalance());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateResidentVehicle(ResidentVehicle vehicle) {
        String sql = "UPDATE resident_vehicles SET resident_name = ?, phone = ?, " +
                "start_date = ?, end_date = ?, balance = ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicle.getResidentName());
            stmt.setString(2, vehicle.getPhone());
            stmt.setDate(3, java.sql.Date.valueOf(vehicle.getStartDate()));
            stmt.setDate(4, vehicle.getEndDate() != null ? java.sql.Date.valueOf(vehicle.getEndDate()) : null);
            stmt.setBigDecimal(5, vehicle.getBalance());
            stmt.setInt(6, vehicle.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteResidentVehicle(int vehicleId) {
        String sql = "DELETE FROM resident_vehicles WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ResidentVehicle getResidentVehicleById(int vehicleId) {
        String sql = "SELECT * FROM resident_vehicles WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ResidentVehicle(
                        rs.getInt("id"),
                        rs.getString("resident_name"),
                        rs.getString("phone"),
                        rs.getString("plate_number"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getBigDecimal("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResidentVehicle getResidentVehicleByPlate(String plateNumber) {
        String sql = "SELECT * FROM resident_vehicles WHERE plate_number = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plateNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ResidentVehicle(
                        rs.getInt("id"),
                        rs.getString("resident_name"),
                        rs.getString("phone"),
                        rs.getString("plate_number"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getBigDecimal("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ResidentVehicle> searchResidentVehicles(String keyword) {
        List<ResidentVehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM resident_vehicles WHERE resident_name LIKE ? OR phone LIKE ? OR plate_number LIKE ? ORDER BY plate_number";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + keyword + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            stmt.setString(3, likeKeyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new ResidentVehicle(
                        rs.getInt("id"),
                        rs.getString("resident_name"),
                        rs.getString("phone"),
                        rs.getString("plate_number"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getBigDecimal("balance")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    public boolean updateVehicleBalance(int vehicleId, BigDecimal amount) {
        String sql = "UPDATE resident_vehicles SET balance = balance + ? WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, vehicleId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ResidentVehicle> getExpiringVehicles(int daysBeforeExpire) {
        List<ResidentVehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM resident_vehicles WHERE end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, daysBeforeExpire);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new ResidentVehicle(
                        rs.getInt("id"),
                        rs.getString("resident_name"),
                        rs.getString("phone"),
                        rs.getString("plate_number"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date") != null ? rs.getDate("end_date").toLocalDate() : null,
                        rs.getBigDecimal("balance")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }
}