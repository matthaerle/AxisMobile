package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class FirearmStockUpdate {
    private int InventoryNumber;
    private int EmployeeID;
    private String MachineName,BoundBookType;

    public int getInventoryNumber() {
        return InventoryNumber;
    }

    public void setInventoryNumber(int inventoryNumber) {
        InventoryNumber = inventoryNumber;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public String getMachineName() {
        return MachineName;
    }

    public void setMachineName(String machineName) {
        MachineName = machineName;
    }

    public String getBoundBookType() {
        return BoundBookType;
    }

    public void setBoundBookType(String boundBookType) {
        BoundBookType = boundBookType;
    }
}
