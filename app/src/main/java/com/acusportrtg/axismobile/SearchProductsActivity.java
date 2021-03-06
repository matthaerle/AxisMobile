package com.acusportrtg.axismobile;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.symbol.emdk.*;
import com.symbol.emdk.EMDKManager.EMDKListener;


import com.acusportrtg.axismobile.JSON_Classes.EmployeeRoles;

import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.Methods.CustomDrawerBuilder;


import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;

import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.ProductListAdapter;
import com.acusportrtg.axismobile.Methods.SharedPrefs;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static android.R.attr.tag;
import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 4/14/2017.
 */

public class SearchProductsActivity extends AppCompatActivity /*implements EMDKListener*/{

    private EditText upc_Field;
    private TextView txt_sum_value, txt_total_header;
    private Button btn_clear_UPC_Field, btn_search_UPC, btn_clear_results_list;
    private ImageView horiz_rule;
    private ArrayList<SendProductView> productList = new ArrayList<>();
    private ProgressDialog pDialog;
    private ListView productListView;
    private String JSONReturnData = "";
    private ProductListAdapter prodAdapter;
    private Switch swtch_multi_mode;
    private CheckBox chk_include_subtotal;
    private double sum_value = 0.00;
    private ConstraintLayout constraintLayout;
    private NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
    private Drawer result = null;
    private GetEmployees emp;
    private EmployeeRoles empRoles = new EmployeeRoles();

    //Assign the profile name used in EMDKConfig.xml
    //private String profileName = "Barcode_Read";

    //Declare a variable to store ProfileManager object
    //private ProfileManager mProfileManager = null;

    //Declare a variable to store EMDKManager object
    //private EMDKManager emdkManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        Globals glob = ((Globals)getApplicationContext());
        emp = glob.getEmployee();
        empRoles = glob.getEmpRoles();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //The EMDKManager object will be created and returned in the callback.
        //EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        //Check the return status of getEMDKManager
        /*
        if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
        {
            //Failed to create EMDKManager object

        }
        */


        CustomDrawerBuilder customDrawerBuilder = new CustomDrawerBuilder();
        customDrawerBuilder.CustomDrawer(SearchProductsActivity.this,SearchProductsActivity.this,toolbar,result,savedInstanceState,emp, empRoles);
        constraintLayout = (ConstraintLayout) findViewById(R.id.SearchProductLayout);
        btn_search_UPC = (Button)findViewById(R.id.btn_search);
        upc_Field = (EditText)findViewById(R.id.edt_upc_field);
        productListView = (ListView)findViewById(R.id.list_product_search);
        horiz_rule = (ImageView) findViewById(R.id.horizontal_rule);
        btn_clear_results_list = (Button) findViewById(R.id.btn_clear_list);
        txt_sum_value = (TextView) findViewById(R.id.txt_sum_value);
        txt_total_header = (TextView) findViewById(R.id.txt_total_header);
        chk_include_subtotal = (CheckBox) findViewById(R.id.chk_include_subtotal);
        swtch_multi_mode = (Switch) findViewById(R.id.swtch_multi_mode);

        upc_Field.setHint("UPC");
        productListView.setVisibility(View.GONE);
        horiz_rule.setVisibility(View.GONE);
        btn_clear_results_list.setVisibility(View.GONE);
        txt_sum_value.setVisibility(View.GONE);
        txt_total_header.setVisibility(View.GONE);
        txt_sum_value.setText("$" + Double.toString(sum_value));
        chk_include_subtotal.setVisibility(View.GONE);

        upc_Field.requestFocus();

        upc_Field.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == keyEvent.KEYCODE_ENTER) {
                    SearchProduct();
                    return true;
                }
                return false;
            }
        });

        btn_clear_results_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodAdapter.clear();
                prodAdapter.notifyDataSetChanged();
                productListView.setVisibility(View.GONE);
                horiz_rule.setVisibility(View.GONE);
                btn_clear_results_list.setVisibility(View.GONE);
                txt_sum_value.setVisibility(View.GONE);
                txt_total_header.setVisibility(View.GONE);
                sum_value = 0.00;
            }
        });

        btn_search_UPC.setOnClickListener(new View.OnClickListener (){
            @Override
            public void onClick(View v) {
                SearchProduct();
            }
        });

        swtch_multi_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ClearMultiProducts();
                    chk_include_subtotal.setVisibility(View.VISIBLE);
                }
                else{
                    ClearMultiProducts();
                }
            }
        });

        chk_include_subtotal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                ConstraintSet set = new ConstraintSet();
                if(isChecked && (productListView.getVisibility() == View.VISIBLE)){
                    txt_sum_value.setVisibility(View.VISIBLE);
                    txt_total_header.setVisibility(View.VISIBLE);
                    set.clone(constraintLayout);
                    set.setGuidelinePercent(R.id.guideline36, 0.95f);
                    set.applyTo(constraintLayout);
                }
                else if(!isChecked) {
                    txt_sum_value.setVisibility(View.GONE);
                    txt_total_header.setVisibility(View.GONE);
                    set.clone(constraintLayout);
                    set.setGuidelinePercent(R.id.guideline36, 1f);
                    set.applyTo(constraintLayout);
                }
            }
        });

       /* upc_Field.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode ==  KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode ==  KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        if(upc_Field.getText().toString().trim().length() == 0){
                            Toast.makeText(SearchProductsActivity.this, "UPC field cannot be blank", Toast.LENGTH_LONG).show();
                            upc_Field.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
                        }
                        else{
                            SearchProduct();
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }

        });*/

        upc_Field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!upc_Field.hasFocus()) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        upc_Field.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() > 0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    upc_Field.setTextColor(Color.parseColor("#2980b9"));
                    upc_Field.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
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
                if(upc_Field.getText().toString().trim().length() == 0){
                    upc_Field.setTextColor(Color.parseColor("#95a5a6"));
                    upc_Field.getBackground().setColorFilter(Color.parseColor("#95a5a6"), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

    }




    public void playSuccess() {
        try {
            MediaPlayer mp = MediaPlayer.create(SearchProductsActivity.this, R.raw.snd_scan_success);
            mp.start();
        } catch (Exception e) {
            Log.e(TAG, "Error play sound: " + e);
            e.printStackTrace();
        }
    }


    private void SearchProduct(){
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        //upc_Field.hideClearButton();
        upc_Field.clearFocus();
        SearchByUPC upc = new SearchByUPC();
        upc.setProductUPC(upc_Field.getText().toString());
        GetProductInfoJsonString(upc, SearchProductsActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drawer_product_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_clear_results:
                ClearMultiProducts();
                break;
            default:
                break;
        }

        return true;
    }

    void showDialog() {
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("Input_Type_Dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Input_Type_Dialog newFragment = new Input_Type_Dialog();
        newFragment.show(ft, "dialog");
    }

    public String GetProductInfoJsonString (SearchByUPC upc, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetProductsByUPC");
            postData.put("ProductUPC", upc.getProductUPC());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String) object;
                    Log.v(TAG, "GetProductInfoJSONString JSONReturnData:\n");
                    try {
                        JSONArray productJson = new JSONArray(JSONReturnData);
                        if(productJson.length() > 1){
                            swtch_multi_mode.setChecked(true);
                            GetProductA(productJson);
                            prodAdapter = new ProductListAdapter(SearchProductsActivity.this, productList,swtch_multi_mode.isChecked());
                            productListView.setVisibility(View.VISIBLE);
                            horiz_rule.setVisibility(View.VISIBLE);
                            productListView.setAdapter(prodAdapter);
                            if(chk_include_subtotal.isChecked()){
                                txt_sum_value.setVisibility(View.VISIBLE);
                                txt_total_header.setVisibility((View.VISIBLE));
                            }
                        }
                        if(swtch_multi_mode.isChecked()){
                            GetProductA(productJson);
                            prodAdapter = new ProductListAdapter(SearchProductsActivity.this, productList, swtch_multi_mode.isChecked());
                            productListView.setVisibility(View.VISIBLE);
                            horiz_rule.setVisibility(View.VISIBLE);
                            btn_clear_results_list.setVisibility(View.VISIBLE);
                            productListView.setAdapter(prodAdapter);
                            if(chk_include_subtotal.isChecked()){
                                txt_sum_value.setVisibility(View.VISIBLE);
                                txt_total_header.setVisibility((View.VISIBLE));
                            }
                        }
                        else{
                            if(prodAdapter != null){
                                prodAdapter.clear();
                                prodAdapter.notifyDataSetChanged();
                            }
                            GetProductA(productJson);
                            prodAdapter = new ProductListAdapter(SearchProductsActivity.this, productList,swtch_multi_mode.isChecked());
                            productListView.setVisibility(View.VISIBLE);
                            horiz_rule.setVisibility(View.VISIBLE);
                            productListView.setAdapter(prodAdapter);
                            if(chk_include_subtotal.isChecked()){
                                txt_sum_value.setVisibility(View.VISIBLE);
                                txt_total_header.setVisibility((View.VISIBLE));
                            }
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

    private void GetProductA(JSONArray productJson) {
        try{
            //JSONArray productJson = new JSONArray(jsonStr);
            if(productJson.length() == 0){
                upc_Field.getBackground().setColorFilter(Color.parseColor("#c0392b"), PorterDuff.Mode.SRC_ATOP);
                upc_Field.setTextColor(Color.parseColor("#c0392b"));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        upc_Field.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                        upc_Field.setTextColor(Color.parseColor("#2980b9"));
                        upc_Field.setText("");
                        upc_Field.requestFocus();
                    }
                }, 1000);
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
                    productView.setIsActive(p.getBoolean("Active"));
                    productView.setIsFirearm(p.getBoolean("IsFirearm"));
                    productView.setIsStockItem(p.getBoolean("IsStock"));
                    productView.setIsSerialized(p.getBoolean("IsSerialNonFirearm"));
                    sum_value += productView.getPrice();

                    if(productList.size() > 0){
                        for(int j = 0; j < productList.size(); j++){
                            if(!(productList.get(j).getProductID().equals(productView.getProductID()))){
                                productList.add(productView);
                                Double sum_rounded = (double) Math.round(sum_value * 100) / 100;
                                txt_sum_value.setText(nf.format(sum_rounded));
                                break;
                            }
                        }
                    }
                    else{
                        productList.add(productView);
                        Double sum_rounded = (double) Math.round(sum_value * 100) / 100;
                        txt_sum_value.setText(nf.format(sum_rounded));
                    }
                }
                upc_Field.getBackground().setColorFilter(Color.parseColor("#27ae60"), PorterDuff.Mode.SRC_ATOP);
                upc_Field.setTextColor(Color.parseColor("#27ae60"));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        upc_Field.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
                        upc_Field.setTextColor(Color.parseColor("#2980b9"));
                        upc_Field.setText("");
                        upc_Field.requestFocus();
                    }
                }, 1000);
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

    private void ClearMultiProducts(){
        if(prodAdapter != null){
            prodAdapter.clear();
            prodAdapter.notifyDataSetChanged();
        }
        productListView.setVisibility(View.GONE);
        horiz_rule.setVisibility(View.GONE);
        btn_clear_results_list.setVisibility(View.GONE);
        txt_sum_value.setVisibility(View.GONE);
        txt_total_header.setVisibility(View.GONE);
        chk_include_subtotal.setChecked(false);
        chk_include_subtotal.setVisibility(View.GONE);
        txt_total_header.setVisibility((View.GONE));
        sum_value = 0.00;
    }

    /*
    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;
//Get the ProfileManager object to process the profiles
        mProfileManager = (ProfileManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);
        if(mProfileManager != null)
        {
            try{

                String[] modifyData = new String[1];
                //Call processPrfoile with profile name and SET flag to create the profile. The modifyData can be null.

                EMDKResults results = mProfileManager.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, modifyData);
                if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
                {
                    //Failed to set profile
                }
            }catch (Exception ex){
                // Handle any exception
            }


        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //Clean up the objects created by EMDK manager
        emdkManager.release();
    }

    @Override
    public void onClosed() {

    }
    */
}
