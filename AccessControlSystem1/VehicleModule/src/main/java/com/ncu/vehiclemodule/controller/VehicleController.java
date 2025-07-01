//文件: VehicleController.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.ncu.vehiclemodule.controller;

import com.ncu.vehiclemodule.dao.ResidentVehicleDAO;
import com.ncu.vehiclemodule.model.ResidentVehicle;

import javax.swing.table.DefaultTableModel;
import java.util.List;

public class VehicleController {

    // Load Vehicle Data(FeeView、VehicleView)
    public void loadVehicleData(DefaultTableModel model, String keyword) {
        model.setRowCount(0);
        ResidentVehicleDAO vehicleDAO = new ResidentVehicleDAO();
        List<ResidentVehicle> vehicles = vehicleDAO.searchResidentVehicles(keyword);

        for (ResidentVehicle vehicle : vehicles) {
            model.addRow(new Object[]{
                    vehicle.getId(),
                    vehicle.getResidentName(),
                    vehicle.getPhone(),
                    vehicle.getPlateNumber(),
                    vehicle.getStartDate(),
                    vehicle.getEndDate(),
                    String.format("%.2f", vehicle.getBalance())
            });
        }
    }
}


