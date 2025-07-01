//文件: FeeRuleDAO.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.adminmodule.dao;

import com.wan.adminmodule.model.FeeRule;
import com.ncu.common.util.DbUtil;

import java.sql.*;

// 收费规则DAO
public class FeeRuleDAO {
    public FeeRule getFeeRule() {
        String sql = "SELECT * FROM fee_rules ORDER BY id DESC LIMIT 1";
        try (Connection conn = DbUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new FeeRule(
                        rs.getInt("id"),
                        rs.getBigDecimal("yearly_fee"),
                        rs.getBigDecimal("monthly_fee"),
                        rs.getBigDecimal("daily_fee"),
                        rs.getBigDecimal("first_hour_rate"),
                        rs.getBigDecimal("subsequent_hour_rate"),
                        rs.getBigDecimal("daily_max_rate")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateFeeRule(FeeRule feeRule) {
        String sql = "INSERT INTO fee_rules (yearly_fee, monthly_fee, daily_fee, " +
                "first_hour_rate, subsequent_hour_rate, daily_max_rate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, feeRule.getYearlyFee());
            stmt.setBigDecimal(2, feeRule.getMonthlyFee());
            stmt.setBigDecimal(3, feeRule.getDailyFee());
            stmt.setBigDecimal(4, feeRule.getFirstHourRate());
            stmt.setBigDecimal(5, feeRule.getSubsequentHourRate());
            stmt.setBigDecimal(6, feeRule.getDailyMaxRate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertFeeRule(FeeRule feeRule) {
        String sql = "INSERT INTO fee_rules (yearly_fee, monthly_fee, daily_fee, " +
                "first_hour_rate, subsequent_hour_rate, daily_max_rate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, feeRule.getYearlyFee());
            stmt.setBigDecimal(2, feeRule.getMonthlyFee());
            stmt.setBigDecimal(3, feeRule.getDailyFee());
            stmt.setBigDecimal(4, feeRule.getFirstHourRate());
            stmt.setBigDecimal(5, feeRule.getSubsequentHourRate());
            stmt.setBigDecimal(6, feeRule.getDailyMaxRate());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
