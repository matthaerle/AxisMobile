package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class SendInventoryGroup {
    private int InventoryGroupID;
    private String GroupName;

    public int getInventoryGroupID() {
        return InventoryGroupID;
    }

    public void setInventoryGroupID(int inventoryGroupID) {
        InventoryGroupID = inventoryGroupID;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }
}
