package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class AddRecordToInventoryGroupProductByMachine {
    private Long InventoryGroupProductID;
    private int Count;
    private String MachineName;

    public Long getInventoryGroupProductID() {
        return InventoryGroupProductID;
    }

    public void setInventoryGroupProductID(Long inventoryGroupProductID) {
        InventoryGroupProductID = inventoryGroupProductID;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public String getMachineName() {
        return MachineName;
    }

    public void setMachineName(String machineName) {
        MachineName = machineName;
    }
}
