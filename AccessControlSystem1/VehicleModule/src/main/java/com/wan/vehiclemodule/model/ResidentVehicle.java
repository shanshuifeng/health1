//文件: ResidentVehicle.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.wan.vehiclemodule.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// 住户车辆模型
public class ResidentVehicle {
    private int id;
    private String residentName;
    private String phone;
    private String plateNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal balance;

    public ResidentVehicle(int id, String residentName, String phone, String plateNumber,
                           LocalDate startDate, LocalDate endDate, BigDecimal balance) {
        this.id = id;
        this.residentName = residentName;
        this.phone = phone;
        this.plateNumber = plateNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.balance = balance;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getResidentName() { return residentName; }
    public String getPhone() { return phone; }
    public String getPlateNumber() { return plateNumber; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getBalance() { return balance; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}

