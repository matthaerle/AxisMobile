package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.SendInventoryGroup;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.Inventory_List_Adapter;
import com.acusportrtg.axismobile.Methods.ServerAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class Inventory_Task extends AppCompatActivity {
    private ProgressDialog pDialog;
    private ArrayList<SendInventoryGroup> inventoryGroupList = new ArrayList<>();
    private ListView inventoryGroupListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);

        getSupportActionBar().setTitle("Product Inventory");

        try {
            URL url = new URL("http://" + ServerAddress.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/GetActiveInventoryGroups");
            new GetInventoryGroups().execute(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }

    }

    private class GetInventoryGroups extends AsyncTask<URL,Void,Void> {
        @Override
        protected Void doInBackground(URL... urls) {
            GetJSONStringWithoutPostData inv = new GetJSONStringWithoutPostData();

            String jsonStr = inv.GetJSONString(urls[0]);


            if (jsonStr != "") {
                try {

                    JSONArray inventoryGroups = new JSONArray(jsonStr);

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
                            Log.e(TAG,"Error:" + e.getMessage());
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
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            Inventory_List_Adapter invAdapter = new Inventory_List_Adapter(Inventory_Task.this,inventoryGroupList);
            inventoryGroupListView = (ListView)findViewById(R.id.Inventory_ListView);
            inventoryGroupListView.setAdapter(invAdapter);

        }

    }
}
