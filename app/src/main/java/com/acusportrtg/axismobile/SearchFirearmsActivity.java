package com.acusportrtg.axismobile;

import android.content.Context;
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

import com.acusportrtg.axismobile.JSON_Classes.FirearmInfo;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockScan;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.SharedPrefs;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 5/2/2017.
 */

public class SearchFirearmsActivity extends AppCompatActivity {
    private String JSONReturnData = "";
    private FirearmInfo fi = new FirearmInfo();
    private FirearmStockScan fss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_firearms);
        getSupportActionBar().setTitle("Firearm Search");

        final RadioButton radio_serial = (RadioButton) findViewById(R.id.rdl_serial_number);
        final RadioButton radio_log = (RadioButton) findViewById(R.id.rdl_log_number);
        final Button btn_search = (Button) findViewById(R.id.btn_search);
        final Button btn_count = (Button) findViewById(R.id.btn_count_submit);
        final EditText edt_input_scanned = (EditText) findViewById(R.id.edt_firearm_scan);
        final Button btn_clear = (Button) findViewById(R.id.btn_clear);

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout firearm_view = (ConstraintLayout) findViewById(R.id.constraint_firearminfo);
                firearm_view.setVisibility(View.INVISIBLE);
            }
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
                        GetFirearmInfo(fss,SearchFirearmsActivity.this);

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
                        GetFirearmInfo(fss,SearchFirearmsActivity.this);
                    }  catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    Toast.makeText(SearchFirearmsActivity.this,"Please Select a scanning mode.",Toast.LENGTH_LONG).show();
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

    public void GetFirearmInfo (FirearmStockScan fss, Context context) {
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
                    Log.v(TAG,"JSONReturnData GetFirearmInfo:\n<"+JSONReturnData + ">");
                    if (JSONReturnData.equals("")) {
                        Toast.makeText(SearchFirearmsActivity.this,"Invalid Scan",Toast.LENGTH_LONG).show();
                    } else {
                        fi = GetFirearmInfo(JSONReturnData);
                        fillFirearmInfo(fi);
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
}
