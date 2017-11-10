package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.SendInventoryGroup;
import com.acusportrtg.axismobile.Methods.CustomDrawerBuilder;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.Inventory_List_Adapter;
import com.acusportrtg.axismobile.Methods.SharedPrefs;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;

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
    private Inventory_List_Adapter invAdapter;
    private Drawer result = null;
    private GetEmployees emp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_products_groups);
        Globals glob = ((Globals)getApplicationContext());
        emp = glob.getEmployee();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        CustomDrawerBuilder customDrawerBuilder = new CustomDrawerBuilder();
        customDrawerBuilder.CustomDrawer(Inventory_Task.this,Inventory_Task.this,toolbar,result,savedInstanceState,emp);

        try {
            URL url = new URL("http://" + SharedPrefs.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/GetActiveInventoryGroups");
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
                        invGroup.setProductCount(c.getInt("ProductCount"));
                        inventoryGroupList.add(invGroup);

                        /*if(inventoryGroupList.size() > 0){
                            for(int j = 0; j < inventoryGroupList.size() - 1; j++){
                                if(!(inventoryGroupList.get(j).getInventoryGroupID() == invGroup.getInventoryGroupID())){
                                    inventoryGroupList.add(invGroup);
                                }
                            }
                        }
                        else{
                            inventoryGroupList.add(invGroup);
                        }*/
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

            invAdapter = new Inventory_List_Adapter(Inventory_Task.this,inventoryGroupList);
            inventoryGroupListView = (ListView)findViewById(R.id.Inventory_ListView);
            inventoryGroupListView.setAdapter(invAdapter);
            inventoryGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                    SendInventoryGroup inv = new SendInventoryGroup();
                    inv = invAdapter.getGroup(position);
                    Globals glob = ((Globals)getApplicationContext());
                    glob.setInvGroup(inv);
                    //Toast.makeText(Inventory_Task.this,"Inventory GroupID: " + String.valueOf(inv.getInventoryGroupID()),Toast.LENGTH_LONG).show();

                    Log.v(TAG,inv.getGroupName());
                    Intent invScanIntent = new Intent(Inventory_Task.this,InventoryProductsActivity.class);
                    startActivity(invScanIntent);
                }
            });

        }

    }
}
