package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import static android.content.ContentValues.TAG;

import com.acusportrtg.axismobile.JSON_Classes.FirearmInfo;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockScan;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockUpdate;
import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.IsFirearmDisposed;
import com.acusportrtg.axismobile.JSON_Classes.UpdateStatus;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mhaerle on 4/21/2017.
 */

public class Firearm_Scan_Activity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private FirearmInfo fi;
    private GetEmployees emp;
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
        final TextView txt_manufacture_data = (TextView) findViewById(R.id.txtManufactureData);
        final TextView txt_serial_data = (TextView) findViewById(R.id.txt_serial_data);
        final TextView txt_upc_data = (TextView) findViewById(R.id.txt_upc_data);
        final ConstraintLayout firearm_view = (ConstraintLayout) findViewById(R.id.constraint_firearminfo);


        btn_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirearmStockUpdate fsu = new FirearmStockUpdate();
                fsu.setInventoryNumber(fi.getInventoryNumber());
                fsu.setEmployeeID(emp.getEmployeeID());
                fsu.setMachineName("Axis Mobile");

                try {
                    GetJSONStringWithPOSTData getJSONStringWithPOSTData = new GetJSONStringWithPOSTData(Firearm_Scan_Activity.this);
                    String postBack = getJSONStringWithPOSTData.UpdateFirearmScan(fsu,Firearm_Scan_Activity.this);
                    Log.v("PostBack\n", postBack);
                    UpdateStatus us = FirearmCounted(postBack);
                    if (us.isSuccesfull()) {
                        Toast.makeText(Firearm_Scan_Activity.this,"Count succesfull.",Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Firearm_Scan_Activity.this,"Unable to Update count.",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_input_scanned = edt_input_scanned.getText().toString().trim();
                FirearmStockScan fss = new FirearmStockScan();
                if(radio_log.isChecked()){
                    //Do Log number stuff

                    try {
                        Long inv_nbr = Long.parseLong(str_input_scanned);
                        fss.setLog(inv_nbr);
                        fss.setLogScanned(true);
                        fss.setSerialScanned(false);

                        //FirearmInfo fi = new GetMyFirearmInfo().execute(fss).get();

                        GetJSONStringWithPOSTData getJSONStringWithPOSTData = new GetJSONStringWithPOSTData(Firearm_Scan_Activity.this);
                        String postBack = getJSONStringWithPOSTData.GetFirearmDisposed(fss, Firearm_Scan_Activity.this);
                        Log.v("Line 59: ",postBack);
                        IsFirearmDisposed ifd = VerifyFirearmDisposedA(postBack);
                        if (ifd ==null) {
                            Toast.makeText(Firearm_Scan_Activity.this,"Invalid Log Number Scanned",Toast.LENGTH_LONG).show();
                        } else {
                            if (ifd.isDisposed()) {
                                Toast.makeText(Firearm_Scan_Activity.this,"Firearm Is Disposed",Toast.LENGTH_LONG).show();
                            } else if (!ifd.isDisposed()) {
                                String firearmInfoPostBack =  getJSONStringWithPOSTData.GetFirearmInfo(fss,Firearm_Scan_Activity.this);
                                Log.v("Line 65: ",firearmInfoPostBack);
                                fi = GetFirearmInfo(firearmInfoPostBack);
                                Log.v("Line 67: ",fi.getManufacturer());
                                txt_manufacture_data.setText(fi.getManufacturer());
                                txt_serial_data.setText(fi.getSerialNumber());
                                txt_upc_data.setText(fi.getUPC());
                                firearm_view.setVisibility(View.VISIBLE);
                            }
                        }

                    }  catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }


                } else if (radio_serial.isChecked()) {
                    //Do Serialnumber stuff
                    try {
                        String serialNumber = str_input_scanned;
                        fss.setSerialNumber(serialNumber);
                        fss.setLogScanned(false);
                        fss.setSerialScanned(true);
                        GetJSONStringWithPOSTData getJSONStringWithPOSTData = new GetJSONStringWithPOSTData(Firearm_Scan_Activity.this);
                        String postBack = getJSONStringWithPOSTData.GetFirearmDisposed(fss, Firearm_Scan_Activity.this);
                        Log.v("Line 92: ",postBack);
                        IsFirearmDisposed ifd = VerifyFirearmDisposedA(postBack);
                        if (ifd ==null) {
                            Toast.makeText(Firearm_Scan_Activity.this,"Invalid Serial Number Scanned",Toast.LENGTH_LONG).show();
                        } else {
                            if (ifd.isDisposed()) {
                                Toast.makeText(Firearm_Scan_Activity.this,"Firearm Is Disposed",Toast.LENGTH_LONG).show();
                            } else if (!ifd.isDisposed()) {
                                String firearmInfoPostBack =  getJSONStringWithPOSTData.GetFirearmInfo(fss,Firearm_Scan_Activity.this);
                                Log.v("Line 65: ",firearmInfoPostBack);
                                fi = GetFirearmInfo(firearmInfoPostBack);
                                Log.v("Line 67: ",fi.getManufacturer());
                                txt_manufacture_data.setText(fi.getManufacturer());
                                txt_serial_data.setText(fi.getSerialNumber());
                                txt_upc_data.setText(fi.getUPC());
                                firearm_view.setVisibility(View.VISIBLE);
                            }
                        }

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
                    }
                });
            }
        }
        return null;
    }
}
