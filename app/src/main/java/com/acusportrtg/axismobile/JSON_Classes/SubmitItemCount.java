package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class SubmitItemCount {
    private String ProductUPC;
    private int EmployeeID;
    private int CountQty;
    private int GroupID;

    public String getProductUPC() {
        return ProductUPC;
    }

    public void setProductUPC(String productUPC) {
        ProductUPC = productUPC;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public int getCountQty() {
        return CountQty;
    }

    public void setCountQty(int countQty) {
        CountQty = countQty;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }
}
