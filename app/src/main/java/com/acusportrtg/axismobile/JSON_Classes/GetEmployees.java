package com.acusportrtg.axismobile.JSON_Classes;

/**
 * Created by mhaerle on 4/11/2017.
 */

public class GetEmployees {
    private int EmployeeID;
    private String FirstName;
    private String MiddleName;
    private String LastName;

    public int getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(int employeeID) {
        EmployeeID = employeeID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
