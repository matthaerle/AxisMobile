package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.graphics.PorterDuff;
import android.text.TextWatcher;
import android.text.Editable;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.IsConnected;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.SharedPrefs;
import com.acusportrtg.axismobile.Methods.VerifyServerConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private IsConnected verified = new IsConnected();
    private ArrayList<String> employeeNameList = new ArrayList<String>();
    private HashMap<String,GetEmployees> employeeMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Axis Mobile");

        verified.setConnectionVerified(false);
        CheckServerConnected();

        final Button setupButton = (Button) findViewById(R.id.btn_Setup);
        final Button loginButton = (Button) findViewById(R.id.btn_Login);
        final EditText username = (EditText) findViewById(R.id.username_textbox);
        final EditText password = (EditText) findViewById(R.id.pass_textbox);

        loginButton.setEnabled(false);
        username.clearFocus();

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("focus", "focus loosed");
                    // Do whatever you want here
                } else {
                    Log.d("focus", "focused");
                }
            }
        });


        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(LoginActivity.this);
                pDialog.setMessage("Loading");
                pDialog.setCancelable(false);
                pDialog.show();
                Intent server_Connection = new Intent(LoginActivity.this,
                        ServerConnectActivity.class);
                pDialog.dismiss();
                startActivity(server_Connection);

            }
        });

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!password.hasFocus()) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!password.hasFocus()) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });


        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                username.setTextColor(Color.parseColor("#2980b9"));
                username.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                loginButton.setEnabled(true);
                loginButton.setTextColor(Color.parseColor("#ffffff"));
                loginButton.setBackgroundResource(R.drawable.btn_solid_square);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(username.getText().toString().trim().length() == 0){
                    username.getBackground().setColorFilter(Color.parseColor("#95a5a6"),PorterDuff.Mode.SRC_ATOP);
                    loginButton.setEnabled(false);
                    loginButton.setTextColor(Color.parseColor("#2980b9"));
                    loginButton.setBackgroundResource(R.drawable.btn_border_square);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                password.setTextColor(Color.parseColor("#2980b9"));
                password.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);

            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(password.getText().toString().trim().length() == 0){
                    password.getBackground().setColorFilter(Color.parseColor("#95a5a6"),PorterDuff.Mode.SRC_ATOP);
                }
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if server can connect and then move on
                if(verified.getConnectionVerified()){
                    boolean activeUser = employeeNameList.contains(username.getText().toString());

                    String resultHash = generateMD5Hash(password.getText().toString());
                    //Toast.makeText(LoginActivity.this, resultHash, Toast.LENGTH_LONG).show();

                    if(activeUser){
                        Toast.makeText(LoginActivity.this, "Employee Verified", Toast.LENGTH_LONG).show();
                        Globals glob = ((Globals)getApplicationContext());
                        GetEmployees emp = employeeMap.get(username.getText().toString());
                        glob.setEmployee(emp);
                        Intent taskChooser = new Intent(LoginActivity.this,TaskChooserActivity.class);
                        startActivity(taskChooser);
                        username.setText("");
                        password.setText("");
                        username.setTextColor(Color.parseColor("#2980b9"));
                        username.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                    }
                    else {
                        Toast.makeText(LoginActivity.this, "Employee Not Found", Toast.LENGTH_LONG).show();
                        username.setTextColor(Color.parseColor("#ff0000"));
                        username.getBackground().setColorFilter(Color.parseColor("#ff0000"), PorterDuff.Mode.SRC_ATOP);
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "Error Connecting", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void CheckServerConnected() {
        VerifyServerConnection verify = new VerifyServerConnection();
        SharedPrefs sharedPrefs = new SharedPrefs();
        IsConnected verified;

        String stringAddress = SharedPrefs.GetSavedServerAddress(this);
        try{
            URL servUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/IsConnected");
            new VerifyServerConnected().execute(servUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class VerifyServerConnected extends AsyncTask<URL, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Verifing Connection...");
            pDialog.setCancelable(true);
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
                Toast.makeText(LoginActivity.this, "Server Connection Needed", Toast.LENGTH_LONG).show();
                Intent server_Connect = new Intent(LoginActivity.this,
                        ServerConnectActivity.class);
                startActivity(server_Connect);
            } else {
                Toast.makeText(LoginActivity.this, "Server Connected", Toast.LENGTH_LONG).show();
                GetEmpData();
            }

        }

    }

    private void GetEmpData() {
        String stringAddress = SharedPrefs.GetSavedServerAddress(this);
        try {
            URL servUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/GetActiveEmployees");
            new LoginActivity.GetEmployeesFromServer().execute(servUrl);
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
                        employeeNameList.add(emp.getEmployeeNumber());
                        employeeMap.put(emp.getEmployeeNumber(),emp);
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

    private String generateMD5Hash(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }



}