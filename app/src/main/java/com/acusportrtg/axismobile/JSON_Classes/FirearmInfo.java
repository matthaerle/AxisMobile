package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/21/2017.
 */

public class FirearmInfo {
    private String SerialNumber,UPC,Manufacturer,Model,GaugeCaliber,TypeOfAction,NewUsed,Description,Importer;
    private String Status;
    private Long InvNbr;
    private int InventoryNumber;

    public FirearmInfo() {
        String serialNumber; String UPC; String manufacturer; String model; String gaugeCaliber; String typeOfAction; String newUsed; String description; String importer; String status; Long invNbr; int inventoryNumber;
        serialNumber = "NODATA";UPC= "NOUPC";manufacturer = "No Manufacturer";model = "No Model"; gaugeCaliber = "No Gauge";typeOfAction = "No Action"; newUsed = "No Data"; description = "No Description"; importer = "No Importer"; status = "No Status";
        invNbr = Long.getLong("0");
        inventoryNumber = 0;
        SerialNumber = serialNumber;
        this.UPC = UPC;
        Manufacturer = manufacturer;
        Model = model;
        GaugeCaliber = gaugeCaliber;
        TypeOfAction = typeOfAction;
        NewUsed = newUsed;
        Description = description;
        Importer = importer;
        Status = status;
        InvNbr = invNbr;
        InventoryNumber = inventoryNumber;
    }

    public String getImporter() {
        return Importer;
    }

    public void setImporter(String importer) {
        Importer = importer;
    }

    public int getInventoryNumber() {
        return InventoryNumber;
    }

    public void setInventoryNumber(int inventoryNumber) {
        InventoryNumber = inventoryNumber;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getUPC() {
        return UPC;
    }

    public void setUPC(String UPC) {
        this.UPC = UPC;
    }

    public String getManufacturer() {
        return Manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        Manufacturer = manufacturer;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getGaugeCaliber() {
        return GaugeCaliber;
    }

    public void setGaugeCaliber(String gaugeCaliber) {
        GaugeCaliber = gaugeCaliber;
    }

    public String getTypeOfAction() {
        return TypeOfAction;
    }

    public void setTypeOfAction(String typeOfAction) {
        TypeOfAction = typeOfAction;
    }

    public String getNewUsed() {
        return NewUsed;
    }

    public void setNewUsed(String newUsed) {
        NewUsed = newUsed;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Long getInvNbr() {
        return InvNbr;
    }

    public void setInvNbr(Long invNbr) {
        InvNbr = invNbr;
    }
}
