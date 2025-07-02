//文件: AccessRecord.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.ncu.accessmodule.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 车辆进出记录模型
public class AccessRecord {
    private int id;
    private String plateNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal fee;
    private boolean paid;
    private Integer residentId;

    public AccessRecord(int id, String plateNumber, LocalDateTime entryTime,
                        LocalDateTime exitTime, BigDecimal fee, boolean paid, Integer residentId) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.fee = fee;
        this.paid = paid;
        this.residentId = residentId;
    }

    // Getters
    public int getId() { return id; }
    public String getPlateNumber() { return plateNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public BigDecimal getFee() { return fee; }
    public boolean isPaid() { return paid; }
    public Integer getResidentId() { return residentId; }

    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public void setPaid(boolean paid) { this.paid = paid; }
}
