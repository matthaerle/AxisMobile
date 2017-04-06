package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class IsFirearmDisposed {
    private boolean Disposed;
    private int InventoryNumber;

    public boolean isDisposed() {
        return Disposed;
    }

    public void setDisposed(boolean disposed) {
        Disposed = disposed;
    }

    public int getInventoryNumber() {
        return InventoryNumber;
    }

    public void setInventoryNumber(int inventoryNumber) {
        InventoryNumber = inventoryNumber;
    }
}
