package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class GetInventoryGroupProductID {
    private int GroupID;
    private String ProductUPC;

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public String getProductUPC() {
        return ProductUPC;
    }

    public void setProductUPC(String productUPC) {
        ProductUPC = productUPC;
    }
}
