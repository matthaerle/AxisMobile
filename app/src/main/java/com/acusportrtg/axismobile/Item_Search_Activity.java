package com.acusportrtg.axismobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.MalformedJsonException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;

import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.Globals;
import com.acusportrtg.axismobile.Methods.ServerAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

import org.w3c.dom.Text;
/**
 * Created by mhaerle on 4/14/2017.
 */

public class Item_Search_Activity extends AppCompatActivity {

    private EditText upc_Field;
    private Button btn_clear_UPC_Field, btn_search_UPC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_search_by_upc);

        btn_clear_UPC_Field = (Button)findViewById(R.id.btn_clear);
        btn_search_UPC = (Button)findViewById(R.id.btn_search);
        upc_Field = (EditText)findViewById(R.id.text_upc_field);


        btn_clear_UPC_Field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upc_Field.setText("");
            }
        });

        btn_search_UPC.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                searchByUPC(upc_Field.getText().toString());
            }
        });
    }

    private void searchByUPC(String upc){
        String stringAddress = ServerAddress.GetSavedServerAddress(this);
        try {
            URL servUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/GetProductsByUPC");
            new getUPCDataFromServer().execute(servUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }
    }

    private class getUPCDataFromServer extends AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground(URL... params) {
            GetJSONStringWithoutPostData prodget = new GetJSONStringWithoutPostData();
            String jsonStr = prodget.GetJSONString(params[0]);

            if(jsonStr != "") {
                try {
                    JSONArray jsonAr = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonAr.length(); i++) {
                        JSONObject obj = jsonAr.getJSONObject(i);
                        SendProductView prod = new SendProductView();
                        prod.setProductID(obj.getLong("ProductID"));
                        prod.setProductUPC(obj.getString("ProductUPC"));
                        prod.setShortDescription(obj.getString("ShortDescription"));
                        prod.setPhysicalQoH(obj.getInt("PhysicalQoH"));
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


}
