package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.os.AsyncTask;
import android.util.Log;

import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;

import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.Product_List_Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 4/14/2017.
 */

public class Item_Search_Activity extends AppCompatActivity {

    private EditText upc_Field;
    private Button btn_clear_UPC_Field, btn_search_UPC;
    private ArrayList<SendProductView> productList = new ArrayList<>();
    private ProgressDialog pDialog;
    private ListView productListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_search_by_upc);
        getSupportActionBar().setTitle("Item Search");

       btn_clear_UPC_Field = (Button)findViewById(R.id.btn_clear);
       btn_search_UPC = (Button)findViewById(R.id.btn_search);
       upc_Field = (EditText)findViewById(R.id.text_upc_field);
       productListView = (ListView)findViewById(R.id.list_product_search);

        productListView.setVisibility(View.GONE);

       btn_clear_UPC_Field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upc_Field.setText("");
            }
        });

        btn_search_UPC.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                SearchByUPC upc = new SearchByUPC();
                upc.setProductUPC(upc_Field.getText().toString());
                GetJSONStringWithPOSTData getProdData = new GetJSONStringWithPOSTData();

                String productInfo = getProdData.GetProductInfoJsonString(upc, Item_Search_Activity.this);
                Log.v(TAG,productInfo);
                new GetProduct().execute(productInfo);
            }
        });
    }


    private class GetProduct extends AsyncTask<String,Void,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Item_Search_Activity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            String jsonStr = params[0];
            try{
                JSONArray productJson = new JSONArray(jsonStr);
                for (int i=0; i <productJson.length(); i++) {
                    JSONObject p = productJson.getJSONObject(i);
                    SendProductView productView = new SendProductView();

                    productView.setItemNmbr(p.optString("ItemNbr"));
                    productView.setMaxLevel(p.getInt("MaxLevel"));
                    productView.setMinLevel(p.getInt("MinLevel"));
                    productView.setPhysicalQoH(p.getInt("PhysicalQoH"));
                    productView.setPrice(p.getDouble("Price"));
                    productView.setProductID(p.getLong("ProductID"));
                    productView.setShortDescription(p.getString("ShortDescription"));
                    productView.setQtyOnOrder(p.getInt("QtyOnOrder"));
                    productView.setQtyCommitted(p.getInt("QtyCommitted"));

                    productList.add(productView);
                }
            } catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Product_List_Adapter prodAdapter = new Product_List_Adapter(Item_Search_Activity.this,productList);
            productListView.setVisibility(View.VISIBLE);
            productListView.setAdapter(prodAdapter);
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }

    }

    /*private void searchByUPC(String upc){
        try {
            //String jsonReturn = GetProductInfoJsonString(upc, this);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }
    }*/



}
