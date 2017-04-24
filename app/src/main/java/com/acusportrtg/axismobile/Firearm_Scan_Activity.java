package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import static android.content.ContentValues.TAG;

import com.acusportrtg.axismobile.JSON_Classes.FirearmInfo;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockScan;
import com.acusportrtg.axismobile.JSON_Classes.IsFirearmDisposed;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by mhaerle on 4/21/2017.
 */

public class Firearm_Scan_Activity extends AppCompatActivity {
    private ProgressDialog pDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firearm_inventory_scan);

        final RadioButton radio_serial = (RadioButton) findViewById(R.id.rdl_serial_number);
        final RadioButton radio_log = (RadioButton) findViewById(R.id.rdl_log_number);
        final Button btn_count = (Button) findViewById(R.id.btn_count);
        final EditText edt_input_scanned = (EditText) findViewById(R.id.edt_firearm_scan);

        btn_count.setOnClickListener(new View.OnClickListener() {
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
                        GetJSONStringWithPOSTData getJSONStringWithPOSTData = new GetJSONStringWithPOSTData();
                        String postBack = getJSONStringWithPOSTData.GetFirearmDisposed(fss, Firearm_Scan_Activity.this);
                        Log.v(TAG,postBack);
                        IsFirearmDisposed ifd = new VerifyFirearmDisposed().execute(postBack).get();
                        if (ifd.isDisposed()) {
                            Toast.makeText(Firearm_Scan_Activity.this,"Firearm Is Disposed",Toast.LENGTH_LONG).show();
                        } else if (!ifd.isDisposed()) {
                            String firearmInfoPostBack =  getJSONStringWithPOSTData.GetFirearmInfo(fss,Firearm_Scan_Activity.this);
                            Log.v(TAG,firearmInfoPostBack);
                            FirearmInfo fi = new GetFirearmInfo().execute(firearmInfoPostBack).get();
                            Log.v(TAG,fi.getManufacturer());
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }


                } else if (radio_serial.isChecked()) {
                    //Do Serialnumber stuff
                    String serial_number = str_input_scanned;
                    fss.setLogScanned(false);
                    fss.setSerialScanned(true);
                    fss.setSerialNumber(serial_number);


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
    private class GetFirearmInfo extends AsyncTask<String,Void,FirearmInfo> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Firearm_Scan_Activity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected FirearmInfo doInBackground(String... params) {
            String jsonStr = params[0];
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
        @Override
        protected  void onPostExecute(FirearmInfo result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    private class VerifyFirearmDisposed extends AsyncTask<String,Void,IsFirearmDisposed> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Firearm_Scan_Activity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected IsFirearmDisposed doInBackground(String... params) {
            String jsonStr = params[0];
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
        @Override
        protected  void onPostExecute(IsFirearmDisposed result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
