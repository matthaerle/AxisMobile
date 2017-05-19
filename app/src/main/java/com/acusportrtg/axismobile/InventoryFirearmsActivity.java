package com.acusportrtg.axismobile;

import android.content.DialogInterface;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import static android.content.ContentValues.TAG;
import android.widget.CompoundButton;
import android.app.AlertDialog;
import com.acusportrtg.axismobile.JSON_Classes.FirearmInfo;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockScan;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockUpdate;
import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.IsFirearmDisposed;
import com.acusportrtg.axismobile.JSON_Classes.UpdateStatus;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhaerle on 4/21/2017.
 */

public class InventoryFirearmsActivity extends AppCompatActivity {
    private FirearmInfo fi = new FirearmInfo();
    private GetEmployees emp;
    private boolean displayedHint = false;
    private FirearmStockScan fss;

    private String JSONReturnData = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals glob = ((Globals)getApplicationContext());
        getSupportActionBar().setTitle("Firearm Count");
        emp = glob.getEmployee();
        setContentView(R.layout.activity_inventory_firearms);
        final RadioButton radio_serial = (RadioButton) findViewById(R.id.rdl_serial_number);
        final RadioButton radio_log = (RadioButton) findViewById(R.id.rdl_log_number);
        final Button btn_search = (Button) findViewById(R.id.btn_search);
        final Button btn_count = (Button) findViewById(R.id.btn_count_submit);
        final ClearableEditText edt_input_scanned = (ClearableEditText) findViewById(R.id.edt_firearm_scan);
        final Switch switch_continuous_mode = (Switch) findViewById(R.id.swtch_continuous_mode);

        radio_log.setChecked(true);
        edt_input_scanned.SetHint("Log Number");
        edt_input_scanned.SetInputTypeDecimal();

        switch_continuous_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btn_search.setText(R.string.btn_count);
                    if(!displayedHint){
                        new AlertDialog.Builder(InventoryFirearmsActivity.this)
                                .setTitle("Continuous Mode")
                                .setMessage("With this active you can scan continuously without needing to click search or count after scanning.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        displayedHint = true;
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch_continuous_mode.setChecked(false);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }}
            else {
                btn_search.setText(R.string.btn_firearm_scan_search);
            }
            }});

        btn_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirearmStockUpdate fsu = new FirearmStockUpdate();
                fsu.setInventoryNumber(fi.getInventoryNumber());
                fsu.setEmployeeID(emp.getEmployeeID());
                fsu.setMachineName("Axis Mobile");
                try {
                    UpdateFirearmScan(fsu,InventoryFirearmsActivity.this);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }    }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fss = new FirearmStockScan();
                String str_input_scanned = edt_input_scanned.getText().toString().trim();
                edt_input_scanned.setText("");
                if(radio_log.isChecked()){
                    //Do Log number stuff
                    try {
                        Long inv_nbr = Long.parseLong(str_input_scanned);
                        fss.setLog(inv_nbr);
                        fss.setLogScanned(true);
                        fss.setSerialScanned(false);
                        fss.setSerialNumber("");
                        GetFirearmDisposed(fss, InventoryFirearmsActivity.this);

                    }  catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else if (radio_serial.isChecked()) {
                    //Do Serialnumber stuff
                    try {
                        fss.setSerialNumber(str_input_scanned);
                        fss.setLogScanned(false);
                        fss.setSerialScanned(true);
                        fss.setLog((long) 1);
                        GetFirearmDisposed(fss, InventoryFirearmsActivity.this);

                    }  catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    Toast.makeText(InventoryFirearmsActivity.this,"Please Select a scanning mode.",Toast.LENGTH_LONG).show();
                }
            }
        });

        View.OnClickListener radio_serial_listener = new View.OnClickListener(){
            public void onClick(View v) {
                if(radio_serial.isChecked()){
                    edt_input_scanned.SetInputTypeText();
                    edt_input_scanned.SetHint("Serial Number");
                }
                if(radio_log.isChecked()){
                    radio_log.setChecked(false);
                }
            }
        };
        View.OnClickListener radio_log_listener = new View.OnClickListener(){
            public void onClick(View v) {
                if(radio_log.isChecked()){
                    edt_input_scanned.SetInputTypeDecimal();
                    edt_input_scanned.SetHint("Log Number");
                }
                if(radio_serial.isChecked()){
                    radio_serial.setChecked(false);
                }
            }
        };
        radio_serial.setOnClickListener(radio_serial_listener);
        radio_log.setOnClickListener(radio_log_listener);
    }

    private UpdateStatus FirearmCounted(String jsonStr) {
        if (!jsonStr.equals("")) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                UpdateStatus us = new UpdateStatus();
                us.setSuccesfull(obj.getBoolean("IsSuccesfull"));
                return us;
            } catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage() + e.getLocalizedMessage());
                    }
                });
            }
        }
        return null;
    }

    private FirearmInfo GetFirearmInfo(String jsonStr) {
        if (!jsonStr.equals("")) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                FirearmInfo fi = new FirearmInfo();
                fi.setSerialNumber(obj.getString("SerialNumber"));
                fi.setDescription(obj.getString("Description"));
                fi.setGaugeCaliber(obj.getString("GaugeCaliber"));
                fi.setInvNbr(obj.getLong("InvNbr"));
                fi.setManufacturer(obj.getString("Manufacturer"));
                fi.setModel(obj.getString("Model"));
                fi.setNewUsed(obj.getString("NewUsed"));
                fi.setStatus(obj.getString("Status"));
                fi.setUPC(obj.getString("UPC"));
                fi.setTypeOfAction(obj.getString("TypeOfAction"));
                fi.setInventoryNumber(obj.getInt("InventoryNumber"));
                fi.setImporter(obj.getString("Importer"));
                return fi;
            } catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage() + e.getLocalizedMessage());
                    }
                });
            }
        }
        return null;
    }

    private IsFirearmDisposed VerifyFirearmDisposedA(String jsonStr) {
        if(!jsonStr.equals("")) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                IsFirearmDisposed ifd = new IsFirearmDisposed();
                ifd.setDisposed(obj.getBoolean("Disposed"));
                ifd.setInventoryNumber(obj.getInt("InventoryNumber"));
                return ifd;
            }catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage() + e.getLocalizedMessage());
                    }});
            }}return null;
    }

    public void UpdateFirearmScan (FirearmStockUpdate fsu, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/CountFirearm");
            postData.put("InventoryNumber", fsu.getInventoryNumber());
            postData.put("EmployeeID", fsu.getEmployeeID());
            postData.put("MachineName", fsu.getMachineName());
            Log.v("PostData to Send", postData.toString());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context){
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    Log.v(TAG, "Update FirearmScan JSONReturnData:\n"+JSONReturnData);
                    UpdateStatus us = FirearmCounted(JSONReturnData);
                    if (us != null) {
                        if (us.isSuccesfull()){
                            Toast.makeText(InventoryFirearmsActivity.this,"Count succesfull.",Toast.LENGTH_LONG).show();
                            hideFirearmInfo();
                        }
                        else
                            Toast.makeText(InventoryFirearmsActivity.this,"Unable to Update count.",Toast.LENGTH_LONG).show();
                    }}};
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            Log.v("GetJSONWithPostData ",JSONReturnData);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }

    public void GetFirearmDisposed (final FirearmStockScan fss, final Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/VerifyFirearmNotDisposed");
            postData.put("Log",fss.getLog());
            postData.put("SerialNumber", fss.getSerialNumber());
            postData.put("SerialScanned", fss.isSerialScanned());
            postData.put("LogScanned", fss.isLogScanned());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    Log.v(TAG,"GetFirearm Disposed, JSONReturnData"+JSONReturnData);
                    IsFirearmDisposed ifd = VerifyFirearmDisposedA(JSONReturnData);
                    if (ifd ==null) {
                        Toast.makeText(InventoryFirearmsActivity.this,"Invalid Log Number Scanned",Toast.LENGTH_SHORT).show();
                    } else {
                        if (ifd.isDisposed()) {
                            Toast.makeText(InventoryFirearmsActivity.this,"Firearm Is Disposed",Toast.LENGTH_SHORT).show();
                        } else if (!ifd.isDisposed()) {
                            //Toast.makeText(InventoryFirearmsActivity.this,"Firearm is In-Stock", Toast.LENGTH_SHORT).show();
                            GetFirearmInfo(fss, InventoryFirearmsActivity.this);
                        }}}};
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            Log.v("GetJSONWithPostData ",JSONReturnData);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }

    private void fillFirearmInfo(FirearmInfo fi) {
        TextView txt_manufacture_data = (TextView) findViewById(R.id.txtManufactureData),
            txt_serial_data = (TextView) findViewById(R.id.txt_serial_data),
            txt_upc_data = (TextView) findViewById(R.id.txt_upc_data),
            txt_firearm_desc_data = (TextView) findViewById(R.id.txt_firearm_desc_data),
            txt_log_data = (TextView) findViewById(R.id.txt_log_data),
                txt_type_of_action_data = (TextView) findViewById(R.id.txt_type_of_action_data),
                txt_new_or_used_data = (TextView) findViewById(R.id.txt_new_or_used_data),
                txt_model_data = (TextView) findViewById(R.id.txt_model_data),
                txt_importer_data = (TextView) findViewById(R.id.txt_importer_data),
                txt_status_data = (TextView) findViewById(R.id.txt_status_data),
                txt_gauge_data = (TextView) findViewById(R.id.txt_gauge_data);

        ConstraintLayout firearm_view = (ConstraintLayout) findViewById(R.id.constraint_firearminfo);
        txt_manufacture_data.setText(fi.getManufacturer());
        txt_serial_data.setText(fi.getSerialNumber());
        txt_upc_data.setText(fi.getUPC());
        txt_firearm_desc_data.setText(fi.getDescription());
        txt_log_data.setText(String.valueOf(fi.getInvNbr()));
        txt_type_of_action_data.setText(fi.getTypeOfAction());
        txt_new_or_used_data.setText(fi.getNewUsed());
        txt_model_data.setText(fi.getModel());
        txt_importer_data.setText(fi.getImporter());
        txt_status_data.setText(fi.getImporter());
        txt_gauge_data.setText(fi.getGaugeCaliber());
        firearm_view.setVisibility(View.VISIBLE);
    }
    private void hideFirearmInfo() {
        ConstraintLayout firearm_view = (ConstraintLayout) findViewById(R.id.constraint_firearminfo);
        firearm_view.setVisibility(View.INVISIBLE);
    }

    public void GetFirearmInfo (FirearmStockScan fss,Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetFirearmInformation");
            postData.put("Log",fss.getLog());
            postData.put("SerialNumber", fss.getSerialNumber());
            postData.put("SerialScanned", fss.isSerialScanned());
            postData.put("LogScanned", fss.isLogScanned());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    Log.v(TAG,"JSONReturnData GetFirearmInfo:\n"+JSONReturnData);
                    fi = GetFirearmInfo(JSONReturnData);
                    Switch switch_continuous_mode = (Switch) findViewById(R.id.swtch_continuous_mode);
                    if (switch_continuous_mode.isChecked()) {
                        FirearmStockUpdate fsu = new FirearmStockUpdate();
                        fsu.setInventoryNumber(fi.getInventoryNumber());
                        fsu.setEmployeeID(emp.getEmployeeID());
                        fsu.setMachineName("Axis Mobile");
                        try {
                            UpdateFirearmScan(fsu,InventoryFirearmsActivity.this);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        if (fi != null) {
                            fillFirearmInfo(fi);
                        } else {
                            Log.e(TAG,"Error filling firearm info");
                        }
                    }
                    }};
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            Log.v(TAG,JSONReturnData);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }
}
