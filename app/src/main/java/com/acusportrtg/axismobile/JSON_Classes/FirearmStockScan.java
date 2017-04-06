package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class FirearmStockScan {
    private Long Log;
    private String SerialNumber;
    private boolean SerialScanned;
    private boolean LogScanned;

    public Long getLog() {
        return Log;
    }

    public void setLog(Long log) {
        Log = log;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public boolean isSerialScanned() {
        return SerialScanned;
    }

    public void setSerialScanned(boolean serialScanned) {
        SerialScanned = serialScanned;
    }

    public boolean isLogScanned() {
        return LogScanned;
    }

    public void setLogScanned(boolean logScanned) {
        LogScanned = logScanned;
    }
}
