package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.SendInventoryGroup;
import com.acusportrtg.axismobile.Methods.Inventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class Inventory_Task extends AppCompatActivity {
    private ProgressDialog pDialog;
    ArrayList<SendInventoryGroup> inventoryGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);
    }

    private class GetInventoryGroups extends AsyncTask<URL,Void,Void> {
        @Override
        protected Void doInBackground(URL... urls) {
            Inventory inv = new Inventory();

            String jsonStr = inv.GetInventoryGroups(urls[0]);


            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray inventoryGroups = jsonObj.getJSONArray("");

                    for (int i = 0; i < inventoryGroups.length(); i++) {
                        JSONObject c = inventoryGroups.getJSONObject(i);

                        SendInventoryGroup invGroup = new SendInventoryGroup();
                        invGroup.setInventoryGroupID(c.getInt("InventoryGroupID"));
                        invGroup.setGroupName(c.getString("GroupName"));

                        inventoryGroupList.add(invGroup);
                    }
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Inventory_Task.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

    }
}
