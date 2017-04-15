package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.IsConnected;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.Globals;
import com.acusportrtg.axismobile.Methods.ServerAddress;
import com.acusportrtg.axismobile.Methods.VerifyServerConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    IsConnected verified = new IsConnected();
    private ArrayList<String> employeeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        verified.setConnectionVerified(false);
        CheckServerConnected();

        final Button setupButton = (Button) findViewById(R.id.btn_Setup);
        final Button loginButton = (Button) findViewById(R.id.btn_Login);
        final EditText username = (EditText) findViewById(R.id.username_textbox);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Loading");
                pDialog.setCancelable(false);
                pDialog.show();
                Intent server_Connection = new Intent(MainActivity.this,
                        ServerConnectActivity.class);
                pDialog.dismiss();
                startActivity(server_Connection);

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if server can connect and then move on
                if(verified.getConnectionVerified()){
                    boolean activeUser = employeeList.contains(username.getText().toString());
                    if(activeUser){
                        Toast.makeText(MainActivity.this, "Employee Verified", Toast.LENGTH_LONG).show();
                        String employee = username.getText().toString();
                        Globals glob = ((Globals)getApplicationContext());
                        glob.setEmployee_Id(employee);
                        Intent taskChooser = new Intent(MainActivity.this,Task_Chooser.class);
                        startActivity(taskChooser);
                        username.setText("");
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Employee Not Found", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Error Connecting", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void CheckServerConnected() {
        VerifyServerConnection verify = new VerifyServerConnection();
        ServerAddress serverAddress = new ServerAddress();
        IsConnected verified;

        String stringAddress = ServerAddress.GetSavedServerAddress(this);
        try{
            URL servUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/IsConnected");
            new VerifyServerConnected().execute(servUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class VerifyServerConnected extends AsyncTask<URL, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Verifing Connection...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(URL... params) {
            GetJSONStringWithoutPostData serveConnect = new GetJSONStringWithoutPostData();
            String jsonStr = serveConnect.GetJSONString(params[0]);

            if(jsonStr != "") {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    verified.setConnectionVerified(jsonObj.getBoolean("ConnectionVerified"));

                } catch (final JSONException e) {
                    Log.e("Error connecting", e.getMessage(), e);
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(pDialog.isShowing())
                pDialog.dismiss();
            finishVerify();
        }
    }

    private void finishVerify() {
        if(verified.getConnectionVerified() != null) {

            if (!verified.getConnectionVerified()) {
                Toast.makeText(MainActivity.this, "Server Connection Needed", Toast.LENGTH_LONG).show();
                Intent server_Connect = new Intent(MainActivity.this,
                        ServerConnectActivity.class);
                startActivity(server_Connect);
            } else {
                Toast.makeText(MainActivity.this, "Server Connected", Toast.LENGTH_LONG).show();
                GetEmpData();
            }

        }

    }

    private void GetEmpData() {
        String stringAddress = ServerAddress.GetSavedServerAddress(this);
        try {
            URL servUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/GetActiveEmployees");
            new MainActivity.GetEmployeesFromServer().execute(servUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }
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
                        employeeList.add(emp.getEmployeeNumber());
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

        }
    }
}
