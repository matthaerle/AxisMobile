package com.acusportrtg.axismobile.Methods;

import android.app.Application;

/**
 * Created by mhaerle on 4/11/2017.
 */

public class Globals extends Application {
    private String Employee_Id;

    public String getEmployee_Id() {
        return Employee_Id;
    }

    public void setEmployee_Id(String employee_Id) {
        Employee_Id = employee_Id;
    }
}
