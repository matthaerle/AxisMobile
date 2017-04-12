package com.acusportrtg.axismobile;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.Methods.EmpSpinAdapter;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.Globals;
import com.acusportrtg.axismobile.Methods.ServerAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;
/**
 * Created by mhaerle on 4/11/2017.
 */

public class Employee_Select_Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<GetEmployees> employeeList = new ArrayList<GetEmployees>();
    private SpinnerAdapter adapter;
    private Button btn_Continue;
    private Spinner empSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_select_activity);
        
        empSpinner = (Spinner) findViewById(R.id.spn_Emp);
        btn_Continue = (Button) findViewById(R.id.btn_Continue);
        
        GetEmpData();
        btn_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetEmployees employee ;
                employee = (GetEmployees) adapter.getItem(empSpinner.getSelectedItemPosition());
                Globals glob = ((Globals)getApplicationContext());
                glob.setEmployee_Id(employee.getEmployeeID());
                Intent task_chooser = new Intent(Employee_Select_Activity.this, Task_Chooser.class);
                startActivity(task_chooser);
            }
        });
    }

    private void FillDropDown() {
        if (!employeeList.isEmpty()) {
           adapter = new EmpSpinAdapter(this,android.R.layout.simple_spinner_item , employeeList);
            empSpinner.setAdapter(adapter);
        }
    }

    private void GetEmpData() {
        String stringAddress = ServerAddress.GetSavedServerAddress(this);
        try {
            URL servUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/GetActiveEmployees");
            new GetEmployeesFromServer().execute(servUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class GetEmployeesFromServer extends AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground(URL... params) {
            GetJSONStringWithoutPostData empget = new GetJSONStringWithoutPostData();
            String jsonStr = empget.GetJSONString(params[0]);

            if(jsonStr != "") {
                try {
                    JSONArray jsonAr = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonAr.length(); i++) {
                        JSONObject obj = jsonAr.getJSONObject(i);
                        GetEmployees emp = new GetEmployees();
                        emp.setEmployeeID(obj.getInt("EmployeeID"));
                        emp.setFirstName(obj.getString("FirstName"));
                        emp.setMiddleName(obj.getString("MiddleName"));
                        emp.setLastName(obj.getString("LastName"));
                        emp.setEmployeeNumber(obj.getString("EmployeeNumber"));
                        employeeList.add(emp);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            FillDropDown();
        }
    }


}
