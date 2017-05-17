package com.acusportrtg.axismobile;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.os.Handler;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.acusportrtg.axismobile.ClearableEditText;

import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;

import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.Product_List_Adapter;
import com.acusportrtg.axismobile.Methods.SharedPrefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 4/14/2017.
 */

public class SearchProductsActivity extends AppCompatActivity {

    private ClearableEditText upc_Field;
    private TextView txt_sum_value, txt_total_header;
    private Button btn_clear_UPC_Field, btn_search_UPC, btn_clear_results_list;
    private ImageView horiz_rule;
    private ArrayList<SendProductView> productList = new ArrayList<>();
    private ProgressDialog pDialog;
    private ListView productListView;
    private String JSONReturnData = "";
    private Product_List_Adapter prodAdapter;
    private Switch swtch_multi_mode;
    private CheckBox chk_include_subtotal;
    private double sum_value = 0.00;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);
        getSupportActionBar().setTitle("Product Search");

       btn_search_UPC = (Button)findViewById(R.id.btn_search);
       upc_Field = (ClearableEditText)findViewById(R.id.edt_upc_field);
       productListView = (ListView)findViewById(R.id.list_product_search);
       horiz_rule = (ImageView) findViewById(R.id.horizontal_rule);
       btn_clear_results_list = (Button) findViewById(R.id.btn_clear_list);
       txt_sum_value = (TextView) findViewById(R.id.txt_sum_value);
       txt_total_header = (TextView) findViewById(R.id.txt_total_header);
       chk_include_subtotal = (CheckBox) findViewById(R.id.chk_include_subtotal);
       swtch_multi_mode = (Switch) findViewById(R.id.swtch_multi_mode);

        productListView.setVisibility(View.GONE);
        horiz_rule.setVisibility(View.GONE);
        btn_clear_results_list.setVisibility(View.GONE);
        txt_sum_value.setVisibility(View.GONE);
        txt_total_header.setVisibility(View.GONE);
        txt_sum_value.setText("$" + Double.toString(sum_value));
        chk_include_subtotal.setVisibility(View.GONE);


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
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                upc_Field.hideClearButton();
                upc_Field.clearFocus();
                SearchByUPC upc = new SearchByUPC();
                upc.setProductUPC(upc_Field.getText().toString());
                GetProductInfoJsonString(upc, SearchProductsActivity.this);
            }
        });

        swtch_multi_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
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
                if(isChecked && (productListView.getVisibility() == View.VISIBLE)){
                    txt_sum_value.setVisibility(View.VISIBLE);
                    txt_total_header.setVisibility(View.VISIBLE);
                }
                else if(!isChecked) {
                    txt_sum_value.setVisibility(View.GONE);
                    txt_total_header.setVisibility(View.GONE);
                }
            }
        });

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
            case R.id.action_input_method:
                showDialog();
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
                    if(swtch_multi_mode.isChecked()){
                        GetProductA(JSONReturnData);
                        prodAdapter = new Product_List_Adapter(SearchProductsActivity.this, productList);
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

    private void GetProductA(String jsonStr) {
        try{
            JSONArray productJson = new JSONArray(jsonStr);
            if(productJson.length() == 0){
                upc_Field.negativeFeedback();
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
                    sum_value += productView.getPrice();

                    if(productList.size() > 0){
                        for(int j = 0; j < productList.size(); j++){
                            if(!(productList.get(j).getProductID().equals(productView.getProductID()))){
                                productList.add(productView);
                                Double sum_rounded = (double) Math.round(sum_value * 100) / 100;
                                txt_sum_value.setText("$" + Double.toString(sum_rounded));
                                break;
                            }
                        }
                    }
                    else{
                        productList.add(productView);
                        Double sum_rounded = (double) Math.round(sum_value * 100) / 100;
                        txt_sum_value.setText("$" + Double.toString(sum_rounded));
                    }
                }
                upc_Field.positiveFeedback();
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
        prodAdapter.clear();
        prodAdapter.notifyDataSetChanged();
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





    /*private void searchByUPC(String upc){
        try {
            //String jsonReturn = GetProductInfoJsonString(upc, this);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        }
    }*/



}
