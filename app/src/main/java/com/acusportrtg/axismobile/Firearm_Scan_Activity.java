package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
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
import com.acusportrtg.axismobile.Methods.ServerAddress;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhaerle on 4/21/2017.
 */

public class Firearm_Scan_Activity extends AppCompatActivity {
    private FirearmInfo fi = new FirearmInfo();
    private GetEmployees emp;
    private boolean displayedHint = false;
    private FirearmStockScan fss;

    private String JSONReturnData = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals glob = ((Globals)getApplicationContext());
        emp = glob.getEmployee();
        setContentView(R.layout.firearm_inventory_scan);

        final RadioButton radio_serial = (RadioButton) findViewById(R.id.rdl_serial_number);
        final RadioButton radio_log = (RadioButton) findViewById(R.id.rdl_log_number);
        final Button btn_search = (Button) findViewById(R.id.btn_search);
        final Button btn_count = (Button) findViewById(R.id.btn_count_submit);
        final EditText edt_input_scanned = (EditText) findViewById(R.id.edt_firearm_scan);
        final Switch switch_continuous_mode = (Switch) findViewById(R.id.swtch_continuous_mode);

        radio_log.setChecked(true);

        switch_continuous_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!displayedHint){
                        new AlertDialog.Builder(Firearm_Scan_Activity.this)
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
                    }}}});

        btn_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirearmStockUpdate fsu = new FirearmStockUpdate();
                fsu.setInventoryNumber(fi.getInventoryNumber());
                fsu.setEmployeeID(emp.getEmployeeID());
                fsu.setMachineName("Axis Mobile");

                try {
                    UpdateFirearmScan(fsu,Firearm_Scan_Activity.this);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }    }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fss = new FirearmStockScan();
                String str_input_scanned = edt_input_scanned.getText().toString().trim();
                if(radio_log.isChecked()){
                    //Do Log number stuff

                    try {
                        Long inv_nbr = Long.parseLong(str_input_scanned);
                        fss.setLog(inv_nbr);
                        fss.setLogScanned(true);
                        fss.setSerialScanned(false);
                        fss.setSerialNumber("");
                        GetFirearmDisposed(fss, Firearm_Scan_Activity.this);
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
                        GetFirearmDisposed(fss, Firearm_Scan_Activity.this);
                    }  catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    Toast.makeText(Firearm_Scan_Activity.this,"Please Select a scanning mode.",Toast.LENGTH_LONG).show();
                }

            }
        });
        View.OnClickListener radio_serial_listener = new View.OnClickListener(){
            public void onClick(View v) {
                if(radio_log.isChecked()){
                    radio_log.setChecked(false);
                }
            }
        };
        View.OnClickListener radio_log_listener = new View.OnClickListener(){
            public void onClick(View v) {
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
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/CountFirearm");
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
                        if (us.isSuccesfull())
                            Toast.makeText(Firearm_Scan_Activity.this,"Count succesfull.",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Firearm_Scan_Activity.this,"Unable to Update count.",Toast.LENGTH_LONG).show();
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
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/VerifyFirearmNotDisposed");
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
                        Toast.makeText(Firearm_Scan_Activity.this,"Invalid Log Number Scanned",Toast.LENGTH_SHORT).show();
                    } else {
                        if (ifd.isDisposed()) {
                            Toast.makeText(Firearm_Scan_Activity.this,"Firearm Is Disposed",Toast.LENGTH_SHORT).show();
                        } else if (!ifd.isDisposed()) {
                            Toast.makeText(Firearm_Scan_Activity.this,"Firearm is In-Stock", Toast.LENGTH_SHORT).show();
                            GetFirearmInfo(fss, Firearm_Scan_Activity.this);
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
        TextView txt_manufacture_data = (TextView) findViewById(R.id.txtManufactureData);
        TextView txt_serial_data = (TextView) findViewById(R.id.txt_serial_data);
        TextView txt_upc_data = (TextView) findViewById(R.id.txt_upc_data);
        ConstraintLayout firearm_view = (ConstraintLayout) findViewById(R.id.constraint_firearminfo);
        txt_manufacture_data.setText(fi.getManufacturer());
        txt_serial_data.setText(fi.getSerialNumber());
        txt_upc_data.setText(fi.getUPC());
        firearm_view.setVisibility(View.VISIBLE);
    }

    public void GetFirearmInfo (FirearmStockScan fss,Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetFirearmInformation");
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
                    if (fi != null) {
                        fillFirearmInfo(fi);
                    } else {
                        Log.e(TAG,"Error filling firearm info");
                    }}};
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            Log.v(TAG,JSONReturnData);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }
}
