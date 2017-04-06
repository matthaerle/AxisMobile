package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class UpdateMinMax {
    private Long ProductID;
    private int MinLevel;
    private int MaxLevel;
    private int EmployeeID;

    public Long getProductID() {
        return ProductID;
    }

    public void setProductID(Long productID) {
        ProductID = productID;
    }

    public int getMinLevel() {
        return MinLevel;
    }

    public void setMinLevel(int minLevel) {
        MinLevel = minLevel;
    }

    public int getMaxLevel() {
        return MaxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        MaxLevel = maxLevel;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }
}
