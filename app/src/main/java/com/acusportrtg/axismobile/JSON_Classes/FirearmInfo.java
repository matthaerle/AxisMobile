package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/21/2017.
 */

public class FirearmInfo {
    private String SerialNumber,UPC,Manufacturer,Model,GaugeCaliber,TypeOfAction,NewUsed,Description;
    private Character Status;
    private Long InvNbr;

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

    public Character getStatus() {
        return Status;
    }

    public void setStatus(Character status) {
        Status = status;
    }

    public Long getInvNbr() {
        return InvNbr;
    }

    public void setInvNbr(Long invNbr) {
        InvNbr = invNbr;
    }
}
