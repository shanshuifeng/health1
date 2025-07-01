//文件: FeeRule.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.adminmodule.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 收费规则模型
public  class FeeRule {
    private int id;
    private BigDecimal yearlyFee;      // 包年费用
    private BigDecimal monthlyFee;     // 包月费用
    private BigDecimal dailyFee;       // 按天计算费用(住户)
    private BigDecimal firstHourRate;  // 首小时收费(临时车)
    private BigDecimal subsequentHourRate; // 后续每小时收费(临时车)
    private BigDecimal dailyMaxRate;   // 单日封顶收费(临时车)
    private LocalDateTime updateAt;

    public FeeRule(int id, BigDecimal yearlyFee, BigDecimal monthlyFee, BigDecimal dailyFee,
                   BigDecimal firstHourRate, BigDecimal subsequentHourRate, BigDecimal dailyMaxRate) {
        this.id = id;
        this.yearlyFee = yearlyFee;
        this.monthlyFee = monthlyFee;
        this.dailyFee = dailyFee;
        this.firstHourRate = firstHourRate;
        this.subsequentHourRate = subsequentHourRate;
        this.dailyMaxRate = dailyMaxRate;
    }

    // Getters
    public int getId() { return id; }
    public BigDecimal getYearlyFee() { return yearlyFee; }
    public BigDecimal getMonthlyFee() { return monthlyFee; }
    public BigDecimal getDailyFee() { return dailyFee; }
    public BigDecimal getFirstHourRate() { return firstHourRate; }
    public BigDecimal getSubsequentHourRate() { return subsequentHourRate; }
    public BigDecimal getDailyMaxRate() { return dailyMaxRate; }
    public LocalDateTime getUpdateAt() { return updateAt; }
}

