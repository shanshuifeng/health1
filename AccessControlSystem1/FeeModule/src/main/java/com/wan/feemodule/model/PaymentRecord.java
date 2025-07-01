//文件: PaymentRecord.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.feemodule.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 收费记录模型
public class PaymentRecord {
    private int id;
    private int residentId;
    private String plateNumber;
    private BigDecimal amount;
    private String paymentType;
    private String paymentMethod;
    private LocalDateTime paymentTime;
    private int operatorId;

    public PaymentRecord(int id, int residentId, String plateNumber, BigDecimal amount,
                         String paymentType, String paymentMethod, LocalDateTime paymentTime, int operatorId) {
        this.id = id;
        this.residentId = residentId;
        this.plateNumber = plateNumber;
        this.amount = amount;
        this.paymentType = paymentType;
        this.paymentMethod = paymentMethod;
        this.paymentTime = paymentTime;
        this.operatorId = operatorId;
    }

    // Getters
    public int getId() { return id; }
    public int getResidentId() { return residentId; }
    public String getPlateNumber() { return plateNumber; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentType() { return paymentType; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getPaymentTime() { return paymentTime; }
    public int getOperatorId() { return operatorId; }
}

