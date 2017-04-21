package com.acusportrtg.axismobile;

import android.app.Application;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.SendInventoryGroup;

/**
 * Created by mhaerle on 4/11/2017.
 */

public class Globals extends Application {
    private GetEmployees Employee;
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

    public GetEmployees getEmployee() {
        return Employee;
    }

    public void setEmployee(GetEmployees employee) {
        Employee = employee;
    }
}
