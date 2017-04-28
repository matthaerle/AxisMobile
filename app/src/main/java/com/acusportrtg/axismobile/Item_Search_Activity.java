package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.os.AsyncTask;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.os.Handler;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;

import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.Product_List_Adapter;
import com.acusportrtg.axismobile.Methods.ServerAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
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
    private String JSONReturnData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_search_by_upc);
        getSupportActionBar().setTitle("Product Search");

       btn_clear_UPC_Field = (Button)findViewById(R.id.btn_clear);
       btn_search_UPC = (Button)findViewById(R.id.btn_search);
       upc_Field = (EditText)findViewById(R.id.edt_upc_field);
       productListView = (ListView)findViewById(R.id.list_product_search);

        productListView.setVisibility(View.GONE);

        upc_Field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                upc_Field.setTextColor(Color.parseColor("#2980b9"));
                upc_Field.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(upc_Field.getText().toString().trim().length() == 0){
                    upc_Field.setTextColor(Color.parseColor("#95a5a6"));
                    upc_Field.getBackground().setColorFilter(Color.parseColor("#95a5a6"), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

       btn_clear_UPC_Field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upc_Field.setText("");
            }
        });

        btn_search_UPC.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Item_Search_Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                SearchByUPC upc = new SearchByUPC();
                upc.setProductUPC(upc_Field.getText().toString());
                GetProductInfoJsonString(upc, Item_Search_Activity.this);
            }
        });
    }

    public String GetProductInfoJsonString (SearchByUPC upc, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetProductsByUPC");
            postData.put("ProductUPC", upc.getProductUPC());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String) object;
                    Log.v(TAG, "GetProductInfoJSONString JSONReturnData:\n");
                    GetProductA(JSONReturnData);
                    Product_List_Adapter prodAdapter = new Product_List_Adapter(Item_Search_Activity.this, productList);
                    productListView.setVisibility(View.VISIBLE);
                    productListView.setAdapter(prodAdapter);
                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return null;
    }

    private void GetProductA(String jsonStr) {
        try{
            JSONArray productJson = new JSONArray(jsonStr);
            if(productJson.length() == 0){
                negativeFeedback();
            }
            else{
                for (int i=0; i <productJson.length(); i++) {
                    JSONObject p = productJson.getJSONObject(i);
                    SendProductView productView = new SendProductView();

                    productView.setProductUPC(p.getString("ProductUPC"));
                    productView.setItemNmbr(p.optString("ItemNbr"));
                    productView.setMaxLevel(p.getInt("MaxLevel"));
                    productView.setMinLevel(p.getInt("MinLevel"));
                    productView.setPhysicalQoH(p.getInt("PhysicalQoH"));
                    productView.setPrice(p.getDouble("Price"));
                    productView.setProductID(p.getLong("ProductID"));
                    productView.setShortDescription(p.getString("ShortDescription"));
                    productView.setQtyOnOrder(p.getInt("QtyOnOrder"));
                    productView.setQtyCommitted(p.getInt("QtyCommitted"));
                    productView.setDepartment(p.getString("Department"));
                    productView.setManufacture(p.getString("Manufacturer"));

                    if(productList.size() > 0){
                        for(int j = 0; j < productList.size(); j++){
                            if(!(productList.get(j).getProductID().equals(productView.getProductID()))){
                                productList.add(productView);
                                break;
                            }
                        }
                    }
                    else{
                        productList.add(productView);
                    }
                }
                positiveFeedback();
            }
        } catch (final JSONException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                }
            });
        }
    }



    private void positiveFeedback(){
        upc_Field.getBackground().setColorFilter(Color.parseColor("#27ae60"), PorterDuff.Mode.SRC_ATOP);
        upc_Field.setTextColor(Color.parseColor("#27ae60"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                upc_Field.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                upc_Field.setTextColor(Color.parseColor("#2980b9"));
                upc_Field.setText("");
            }
        }, 1000);
    }

    private void negativeFeedback(){
        upc_Field.getBackground().setColorFilter(Color.parseColor("#c0392b"), PorterDuff.Mode.SRC_ATOP);
        upc_Field.setTextColor(Color.parseColor("#c0392b"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                upc_Field.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                upc_Field.setTextColor(Color.parseColor("#2980b9"));
            }
        }, 1000);
    }

    /*private void searchByUPC(String upc){
        try {
            //String jsonReturn = GetProductInfoJsonString(upc, this);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }
    }*/



}
