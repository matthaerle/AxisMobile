package com.acusportrtg.axismobile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;
import com.acusportrtg.axismobile.JSON_Classes.UpdateMinMax;
import com.acusportrtg.axismobile.JSON_Classes.UpdateStatus;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 4/14/2017.
 */

public class UpdateMinMaxActivity extends AppCompatActivity {
    private SearchByUPC upc;
    private String JSONReturnData = "";
    private GetEmployees emp;
    private SendProductView productView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_minmax);
        Globals glob = ((Globals)getApplicationContext());
        getSupportActionBar().setTitle("Update Min/Max");
        emp = glob.getEmployee();
        
        final Button btn_clear = (Button) findViewById(R.id.btn_clear);
        final Button btn_search = (Button) findViewById(R.id.btn_search);
        final EditText edt_upc_field = (EditText) findViewById(R.id.edt_upc_field);
        final Button btn_update = (Button) findViewById(R.id.btn_submit_change);
        final EditText edt_min_value = (EditText) findViewById(R.id.edt_min_value),
                edt_max_value = (EditText) findViewById(R.id.edt_max_value);
        
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout product_view = (ConstraintLayout)findViewById(R.id.const_item_info);
                product_view.setVisibility(View.INVISIBLE);
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateMinMax upd = new UpdateMinMax();
                upd.setEmployeeID(emp.getEmployeeID());
                if (!edt_min_value.getText().toString().trim().equals("")
                        && !edt_max_value.getText().toString().trim().equals("") ) {
                    upd.setMinLevel(Integer.parseInt(edt_min_value.getText().toString()));
                    upd.setMaxLevel(Integer.parseInt(edt_max_value.getText().toString()));
                    upd.setProductID(productView.getProductID());
                    Update_Product_Min_Max(upd,UpdateMinMaxActivity.this);
                } else {
                    Toast.makeText(UpdateMinMaxActivity.this,"Invalid Min or Max",Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upc = new SearchByUPC();
                String upc_scanned = edt_upc_field.getText().toString().trim();
                edt_upc_field.setText("");
                if (!upc_scanned.equals("")) {
                    upc.setProductUPC(upc_scanned);
                    GetProductInfo(upc,UpdateMinMaxActivity.this);
                } else 
                    Toast.makeText(UpdateMinMaxActivity.this,"Invalid Item Scanned", Toast.LENGTH_SHORT).show();
            }
        });
        
    }

    private void Update_Product_Min_Max(UpdateMinMax upd, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/UpdateMinMax");
            postData.put("ProductID", upd.getProductID());
            postData.put("MinLevel", upd.getMinLevel());
            postData.put("MaxLevel", upd.getMaxLevel());
            postData.put("EmployeeID", upd.getEmployeeID());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String) object;
                    Log.v(TAG, "GetProductInfoJSONString JSONReturnData:\n" + JSONReturnData);
                    Verify_Updated(JSONReturnData);
                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }



    private void GetProductInfo(SearchByUPC upc, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetProductsByUPC");
            postData.put("ProductUPC", upc.getProductUPC());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String) object;
                    Log.v(TAG, "GetProductInfoJSONString JSONReturnData:\n");
                    GetProduct(JSONReturnData);
                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }

    private void Verify_Updated(String jsonStr) {
        try {
            UpdateStatus upd_status = new UpdateStatus();
            JSONObject updateJson = new JSONObject(jsonStr);
            if (updateJson.length() == 0) {
                Toast.makeText(UpdateMinMaxActivity.this,"Error Updating Min and Max", Toast.LENGTH_SHORT).show();
                upd_status.setSuccesfull(false);
            } else {

                upd_status.setSuccesfull(updateJson.getBoolean("IsSuccesfull"));
            }
            if (upd_status.isSuccesfull())
                Toast.makeText(UpdateMinMaxActivity.this,"Update Complete", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(UpdateMinMaxActivity.this,"Update Failed", Toast.LENGTH_SHORT).show();


        } catch (final JSONException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "JSON parsing error: " + e.getMessage());
                }
            });
        }
    }

    private void GetProduct(String jsonStr) {
        try{
            JSONArray productJson = new JSONArray(jsonStr);
            if(productJson.length() == 0){
                negativeFeedback();
            }
            else{
                productView = new SendProductView();
                for (int i=0; i <productJson.length(); i++) {
                    JSONObject p = productJson.getJSONObject(i);


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


                }
                positiveFeedback();
                FillProductInfo(productView);
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

    private void FillProductInfo(SendProductView productView) {
        TextView txt_upc_data = (TextView) findViewById(R.id.txt_upc_data),
                txt_manufacture_data = (TextView) findViewById(R.id.txt_manufacture_data),
                txt_qty_on_order_data = (TextView) findViewById(R.id.txt_qty_on_order_data),
                txt_qty_committed_data = (TextView) findViewById(R.id.txt_qty_committed_data),
                txt_qoh_data = (TextView) findViewById(R.id.txt_qoh_data),
                txt_price_data = (TextView) findViewById(R.id.txt_price_data),
                txt_department_data = (TextView) findViewById(R.id.txt_department_data),
                txt_description_data = (TextView) findViewById(R.id.txt_description_data);
        EditText edt_min_value = (EditText) findViewById(R.id.edt_min_value),
                edt_max_value = (EditText) findViewById(R.id.edt_max_value);

        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        String pattern = "###,###,###";
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        df.applyPattern(pattern);

        txt_upc_data.setText(productView.getProductUPC());
        txt_manufacture_data.setText(productView.getManufacture());
        txt_qty_on_order_data.setText(df.format(productView.getQtyOnOrder()));
        txt_qty_committed_data.setText(df.format(productView.getQtyCommitted()));
        txt_qoh_data.setText(df.format(productView.getPhysicalQoH()));
        txt_price_data.setText(nf.format(productView.getPrice()));
        txt_department_data.setText(productView.getDepartment());
        txt_description_data.setText(productView.getShortDescription());

        edt_min_value.setHint(df.format(productView.getMinLevel()));
        edt_max_value.setHint(df.format(productView.getMaxLevel()));

        ConstraintLayout product_view = (ConstraintLayout)findViewById(R.id.const_item_info);
        product_view.setVisibility(View.VISIBLE);
    }

    private void negativeFeedback(){
        final EditText edt_upc_field = (EditText) findViewById(R.id.edt_upc_field);
        edt_upc_field.getBackground().setColorFilter(Color.parseColor("#c0392b"), PorterDuff.Mode.SRC_ATOP);
        edt_upc_field.setTextColor(Color.parseColor("#c0392b"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edt_upc_field.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                edt_upc_field.setTextColor(Color.parseColor("#2980b9"));
            }
        }, 1000);
    }
    private void positiveFeedback(){
        final EditText edt_upc_field = (EditText) findViewById(R.id.edt_upc_field);
        edt_upc_field.getBackground().setColorFilter(Color.parseColor("#27ae60"), PorterDuff.Mode.SRC_ATOP);
        edt_upc_field.setTextColor(Color.parseColor("#27ae60"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                edt_upc_field.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                edt_upc_field.setTextColor(Color.parseColor("#2980b9"));
                edt_upc_field.setText("");
            }
        }, 1000);
    }

}
