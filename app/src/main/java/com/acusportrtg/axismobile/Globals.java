package com.acusportrtg.axismobile;

import android.app.Application;

import com.acusportrtg.axismobile.JSON_Classes.SendInventoryGroup;

/**
 * Created by mhaerle on 4/11/2017.
 */

public class Globals extends Application {
    private String Employee_Id;
    private int Inventory_Group_Id;
    private SendInventoryGroup invGroup;

    public SendInventoryGroup getInvGroup() {
        return invGroup;
    }

    public void setInvGroup(SendInventoryGroup invGroup) {
        this.invGroup = invGroup;
    }

    public int getInventory_Group_Id() {
        return Inventory_Group_Id;
    }

    public void setInventory_Group_Id(int inventory_Group_Id) {
        Inventory_Group_Id = inventory_Group_Id;
    }

    public String getEmployee_Id() {
        return Employee_Id;
    }

    public void setEmployee_Id(String employee_Id) {
        Employee_Id = employee_Id;
    }
}
