package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.acusportrtg.axismobile.JSON_Classes.GetInventoryGroupProductID;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 4/20/2017.
 */

public class Inventory_Scan_Activity extends AppCompatActivity {
    private Button btn_search,btn_clear,btn_submit;
    private EditText edt_product_upc,edt_count_qty;
    private ConstraintLayout product_info;
    private ProgressDialog pDialog;
    private int invGroupID;
    private TextView txt_qoh_data,txt_upc_data,txt_price_data,txt_desc_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_scan);
        Globals glob = ((Globals)getApplicationContext());
        invGroupID = glob.getInvGroup().getInventoryGroupID();

        btn_search = (Button) findViewById(R.id.btn_search);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_submit = (Button) findViewById(R.id.btn_submit_count);
        edt_product_upc = (EditText) findViewById(R.id.edt_upc_field);
        edt_count_qty = (EditText) findViewById(R.id.edt_count_qty);
        product_info = (ConstraintLayout) findViewById(R.id.product_info);

        txt_desc_data = (TextView) findViewById(R.id.txt_desc_data);
        txt_price_data = (TextView) findViewById(R.id.txt_price_data);
        txt_qoh_data = (TextView) findViewById(R.id.txt_qoh_data);
        txt_upc_data = (TextView) findViewById(R.id.txt_upc_data);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_submit.setEnabled(false);
                if(!edt_product_upc.getText().toString().isEmpty()) {
                    try {
                        String productUPC = edt_product_upc.getText().toString().trim();
                        GetInventoryGroupProductID prod = new GetInventoryGroupProductID();
                        prod.setGroupID(invGroupID);
                        prod.setProductUPC(productUPC);
                        GetJSONStringWithPOSTData getJSONStringWithPOSTData = new GetJSONStringWithPOSTData();

                        String productInfo = getJSONStringWithPOSTData.VerifyProductInGroup(prod, Inventory_Scan_Activity.this);
                        Log.v(TAG, productInfo);
                        SendProductView prodInfo = new VerifyProductInGroup().execute(productInfo).get();
                        edt_count_qty.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Inventory_Scan_Activity.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(edt_count_qty, InputMethodManager.SHOW_IMPLICIT);

                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(TAG, e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                edt_count_qty.setText("");
                edt_product_upc.setText("");
                edt_product_upc.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Inventory_Scan_Activity.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edt_product_upc, InputMethodManager.SHOW_IMPLICIT);
                btn_submit.setEnabled(false);
                product_info.setVisibility(View.INVISIBLE);
            }
        });

        edt_count_qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                edt_count_qty.setTextColor(Color.parseColor("#2980b9"));
                edt_count_qty.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                btn_submit.setEnabled(true);
                btn_submit.setTextColor(Color.parseColor("#ffffff"));
                btn_submit.setBackgroundResource(R.drawable.solid_square_button);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(edt_count_qty.getText().toString().trim().length() == 0){
                    btn_submit.setEnabled(false);
                    btn_submit.setTextColor(Color.parseColor("#2980b9"));
                    btn_submit.setBackgroundResource(R.drawable.border_button);
                }
            }
        });

        getSupportActionBar().setTitle("Group: " + glob.getInvGroup().getGroupName());
    }

    private class VerifyProductInGroup extends AsyncTask<String,Void,SendProductView> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Inventory_Scan_Activity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }
        @Override
        protected SendProductView doInBackground(String... params) {
            String jsonStr = params[0];

            try {

                JSONObject obj = new JSONObject(jsonStr);
                Object json = obj;
                JSONObject prodObject;
                JSONArray prodArray;
                if (json instanceof JSONObject) {
                    SendProductView productView = new SendProductView();
                    prodObject = (JSONObject)json;
                    productView.setProductUPC(prodObject.getString("ProductUPC"));
                    productView.setItemNmbr(prodObject.optString("ItemNbr"));
                    productView.setMaxLevel(prodObject.getInt("MaxLevel"));
                    productView.setMinLevel(prodObject.getInt("MinLevel"));
                    productView.setPhysicalQoH(prodObject.getInt("PhysicalQoH"));
                    productView.setPrice(prodObject.getDouble("Price"));
                    productView.setProductID(prodObject.getLong("ProductID"));
                    productView.setShortDescription(prodObject.getString("ShortDescription"));
                    productView.setQtyOnOrder(prodObject.getInt("QtyOnOrder"));
                    productView.setQtyCommitted(prodObject.getInt("QtyCommitted"));

                    return productView;
                } else if (json instanceof  JSONArray) {
                    prodArray = (JSONArray)json;
                    for (int i=0; i < prodArray.length(); i++) {
                        JSONObject p = prodArray.getJSONObject(i);
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

                        return productView;
                }

                }
            } catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage() + e.getLocalizedMessage());
                    }
                });
            }
            return null;
        }

        @Override
        protected  void onPostExecute(SendProductView result) {
            super.onPostExecute(result);
            txt_upc_data.setText(result.getProductUPC());
            txt_price_data.setText(String.valueOf(result.getPrice()));
            txt_desc_data.setText(result.getShortDescription());
            txt_qoh_data.setText(String.valueOf(result.getPhysicalQoH()));

            product_info.setVisibility(View.VISIBLE);
            if(pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }
}
