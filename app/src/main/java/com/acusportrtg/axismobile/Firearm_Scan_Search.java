package com.acusportrtg.axismobile;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

public class Firearm_Scan_Search extends AppCompatActivity {
    private String JSONReturnData = "";
    private FirearmInfo fi = new FirearmInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firearm_search_scan);
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
                    Log.v(TAG,"JSONReturnData GetFirearmInfo:\n"+JSONReturnData);
                    fi = GetFirearmInfo(JSONReturnData);
                    fillFirearmInfo(fi);

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
