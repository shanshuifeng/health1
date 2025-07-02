package com.ncu.common.dao;

import com.ncu.common.model.User;
import com.ncu.common.util.DbUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public static final String ADMIN_INITIAL_PASSWORD = "admin123";
    public static final String USER_INITIAL_PASSWORD = "user123";

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("first_login"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null,
                        rs.getString("gender"),
                        rs.getString("id_card")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 新增方法：获取所有操作员（医护人员和管理员）
    public List<User> getAllOperators() {
        List<User> operators = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role IN ('admin', 'doctor') ORDER BY created_at DESC";
        try (Connection conn = DbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                operators.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("first_login"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null,
                        rs.getString("gender"),
                        rs.getString("id_card")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operators;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, first_login, name, phone, birth_date, gender, id_card) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setBoolean(4, user.isFirstLogin());
            stmt.setString(5, user.getName());
            stmt.setString(6, user.getPhone());
            stmt.setDate(7, user.getBirthDate() != null ? java.sql.Date.valueOf(user.getBirthDate()) : null);
            stmt.setString(8, user.getGender());
            stmt.setString(9, "doctor".equals(user.getRole()) ? user.getIdCard() : null);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserPassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ?, first_login = false WHERE id = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}