package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class InventoryGroupProduct {
    private Long InventoryGroupProductID;
    private int InventoryGroupID;
    private Long ProductID;
    private int EmployeeID;
    private int QtyScanned;

    public Long getInventoryGroupProductID() {
        return InventoryGroupProductID;
    }

    public void setInventoryGroupProductID(Long inventoryGroupProductID) {
        InventoryGroupProductID = inventoryGroupProductID;
    }

    public int getInventoryGroupID() {
        return InventoryGroupID;
    }

    public void setInventoryGroupID(int inventoryGroupID) {
        InventoryGroupID = inventoryGroupID;
    }

    public Long getProductID() {
        return ProductID;
    }

    public void setProductID(Long productID) {
        ProductID = productID;
    }

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public int getQtyScanned() {
        return QtyScanned;
    }

    public void setQtyScanned(int qtyScanned) {
        QtyScanned = qtyScanned;
    }
}
