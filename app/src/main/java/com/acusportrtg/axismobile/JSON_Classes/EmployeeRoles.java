package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by jpederson on 11/10/2017.
 */

public class EmployeeRoles {
    private Boolean Firearms;
    private Boolean FirearmBoundBook;
    private Boolean FirearmPhysicalInventory;
    private Boolean FirearmViewCost;
    private Boolean Pricing;
    private Boolean ProductCost;
    private Boolean IMProdMaintAdjQoH;
    private Boolean InventoryManagement;
    private Boolean IMUpdate;
    private Boolean IMInvStockTaking;

    public EmployeeRoles() {
        Firearms = false;
        FirearmBoundBook = false;
        FirearmPhysicalInventory = false;
        FirearmViewCost = false;
        Pricing = false;
        ProductCost = false;
        IMProdMaintAdjQoH = false;
        IMUpdate = false;
        InventoryManagement = false;
        IMInvStockTaking = false;
    }

    public Boolean getFirearmsPermission() {
        return Firearms;
    }
    public void setFirearmsPermission(Boolean permission) {
        Firearms = permission;
    }

    public Boolean getFirearmBoundBookPermission() {
        return FirearmBoundBook;
    }
    public void setFirearmBoundBookPermission(Boolean permission) {
        FirearmBoundBook = permission;
    }

    public Boolean getFirearmPhysicalInventory() {
        return FirearmPhysicalInventory;
    }
    public void setFirearmPhysicalInventoryPermission(Boolean permission) {
        FirearmPhysicalInventory = permission;
    }

    public Boolean getFirearmViewCostPermission() {
        return FirearmViewCost;
    }
    public void setFirearmViewCostPermission(Boolean permission) {
        FirearmViewCost = permission;
    }

    public Boolean getPricingPermission() {
        return Pricing;
    }
    public void setPricingPermission(Boolean permission) {
        Pricing = permission;
    }

    public Boolean getProductCostPermission() {
        return ProductCost;
    }
    public void setProductCostPermission(Boolean permission) {
        ProductCost = permission;
    }

    public Boolean getIMProdMaintAdjQoHPermission() {
        return IMProdMaintAdjQoH;
    }
    public void setIMProdMaintAdjQoHPermission(Boolean permission) {
        IMProdMaintAdjQoH = permission;
    }

    public Boolean getIMUpdatePermission() {
        return IMUpdate;
    }
    public void setIMUpdatePermission(Boolean permission) { IMUpdate = permission; }

    public Boolean getInventoryManagementPermission() {
        return InventoryManagement;
    }
    public void setInventoryManagementPermission(Boolean permission) { InventoryManagement = permission; }

    public Boolean getIMInvStockTaking() {return IMInvStockTaking;}
    public void setIMInvStockTaking(Boolean IMInvStockTaking) {this.IMInvStockTaking = IMInvStockTaking;}





}
