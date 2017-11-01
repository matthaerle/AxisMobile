package com.acusportrtg.axismobile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.GetInventoryGroupProductID;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;
import com.acusportrtg.axismobile.JSON_Classes.SubmitItemCount;
import com.acusportrtg.axismobile.JSON_Classes.UpdateStatus;
import com.acusportrtg.axismobile.Methods.CustomDrawerBuilder;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.SharedPrefs;
import com.alien.barcode.BarcodeReader;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 4/20/2017.
 */

public class InventoryProductsActivity extends AppCompatActivity {
    private Button btn_search,btn_clear,btn_submit;
    private SubmitItemCount sub = new SubmitItemCount();
    private String JSONReturnData = "";
    private EditText edt_product_upc;
    private EditText edt_count_qty;
    private ConstraintLayout product_info;
    private int invGroupID;
    private GetEmployees emp;
    private TextView txt_qoh_data,txt_upc_data,txt_price_data,txt_desc_data;
    private BarcodeReader barcodeReader;
    private Drawer result = null;
    private AccountHeader headerResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_products);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(false)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .build();

        CustomDrawerBuilder customDrawerBuilder = new CustomDrawerBuilder();
        customDrawerBuilder.CustomDrawer(InventoryProductsActivity.this,InventoryProductsActivity.this,headerResult,toolbar,result,savedInstanceState);
        Globals glob = ((Globals)getApplicationContext());
        invGroupID = glob.getInvGroup().getInventoryGroupID();
        emp = glob.getEmployee();


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

        edt_product_upc.setHint("UPC");

        if ( Build.MODEL == "Alien")
            barcodeReader = new BarcodeReader(this);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search();
            }
        });
        edt_product_upc.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() > 0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    edt_product_upc.setTextColor(Color.parseColor("#2980b9"));
                    edt_product_upc.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                    if(!imm.isAcceptingText()) {
                        imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
                    }
                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }
            @Override
            public void afterTextChanged(Editable s)
            {
                if(edt_product_upc.getText().toString().trim().length() == 0){
                    edt_product_upc.setTextColor(Color.parseColor("#95a5a6"));
                    edt_product_upc.getBackground().setColorFilter(Color.parseColor("#95a5a6"), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        edt_product_upc.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode ==  KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode ==  KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        if(edt_product_upc.getText().toString().trim().length() == 0){
                            Toast.makeText(InventoryProductsActivity.this, "UPC field cannot be blank", Toast.LENGTH_LONG).show();
                            edt_product_upc.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
                        }
                        else{
                            Search();
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }

        });

        edt_product_upc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!edt_product_upc.hasFocus()) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sub = new SubmitItemCount();
                sub.setProductUPC(txt_upc_data.getText().toString().trim());
                sub.setGroupID(invGroupID);
                sub.setCountQty(Integer.parseInt(edt_count_qty.getText().toString().trim()));
                sub.setEmployeeID(emp.getEmployeeID());
                try{
                    UpdateInventoryCount(sub, InventoryProductsActivity.this);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                edt_count_qty.setText("");
                edt_product_upc.setText("");
                edt_product_upc.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(InventoryProductsActivity.INPUT_METHOD_SERVICE);
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
                btn_submit.setBackgroundResource(R.drawable.btn_solid_square);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(edt_count_qty.getText().toString().trim().length() == 0){
                    btn_submit.setEnabled(false);
                    btn_submit.setTextColor(Color.parseColor("#2980b9"));
                    btn_submit.setBackgroundResource(R.drawable.btn_border_square);
                }
            }
        });

        getSupportActionBar().setTitle("Group: " + glob.getInvGroup().getGroupName());
    }

    public void Search() {
        btn_submit.setEnabled(false);
        if(!edt_product_upc.getText().toString().isEmpty()) {
            try {
                String productUPC = edt_product_upc.getText().toString().trim();
                GetInventoryGroupProductID prod = new GetInventoryGroupProductID();
                prod.setGroupID(invGroupID);
                prod.setProductUPC(productUPC);


                VerifyProductInGroup(prod, InventoryProductsActivity.this);


            }  catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public String UpdateInventoryCount (SubmitItemCount count, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/InsertProductCountInventory");
            postData.put("ProductUPC", count.getProductUPC());
            postData.put("EmployeeID", count.getEmployeeID());
            postData.put("CountQty", count.getCountQty());
            postData.put("GroupID", count.getGroupID());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    Log.v(TAG, "UpdateInventoryCount JSONReturnData\n"+JSONReturnData);
                    UpdateStatus status = UpdateInventoryCountA(JSONReturnData);
                    if (status.isSuccesfull()) {
                        Toast.makeText(InventoryProductsActivity.this,"UPC: " + sub.getProductUPC() + " Updated",Toast.LENGTH_LONG).show();
                        Log.v(TAG,"UPC: " + sub.getProductUPC() + "Updated to Qty: " + String.valueOf(sub.getCountQty()));
                    }
                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;
    }

    public String VerifyProductInGroup (GetInventoryGroupProductID prod, Context context) {

        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/InventoryGroupProductID");
            postData.put("ProductUPC", prod.getProductUPC());
            postData.put("GroupID", prod.getGroupID());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    if (!JSONReturnData.equals("")) {
                        Log.v(TAG, "VerifyProductInGroup JSONReturnData\n"+JSONReturnData);
                        SendProductView prodInfo = VerifyProductInGroupA(JSONReturnData);
                        edt_count_qty.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(InventoryProductsActivity.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(edt_count_qty, InputMethodManager.SHOW_IMPLICIT);
                        txt_desc_data = (TextView) findViewById(R.id.txt_desc_data);
                        txt_price_data = (TextView) findViewById(R.id.txt_price_data);
                        txt_qoh_data = (TextView) findViewById(R.id.txt_qoh_data);
                        txt_upc_data = (TextView) findViewById(R.id.txt_upc_data);

                        txt_upc_data.setText(prodInfo.getProductUPC());
                        txt_price_data.setText(String.valueOf(prodInfo.getPrice()));
                        txt_desc_data.setText(prodInfo.getShortDescription());
                        txt_qoh_data.setText(String.valueOf(prodInfo.getPhysicalQoH()));

                        product_info.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(InventoryProductsActivity.this,"Invalid Product Scanned", Toast.LENGTH_SHORT).show();
                    }

                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;

    }

    private UpdateStatus UpdateInventoryCountA (String jsonStr) {
        if (!jsonStr.equals("")) {
            try {

                JSONObject statusObject = new JSONObject(jsonStr);
                    UpdateStatus upd = new UpdateStatus();
                    upd.setSuccesfull(statusObject.getBoolean("IsSuccesfull"));

                    return upd;

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

    private SendProductView VerifyProductInGroupA (String jsonStr) {
        if (!jsonStr.equals("")) {
            try {
                JSONObject prodObject = new JSONObject(jsonStr);

                    SendProductView productView = new SendProductView();
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
