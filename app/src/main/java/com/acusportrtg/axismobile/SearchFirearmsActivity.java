package com.acusportrtg.axismobile;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.symbol.emdk.*;
import com.symbol.emdk.EMDKManager.EMDKListener;


import com.acusportrtg.axismobile.JSON_Classes.EmployeeRoles;

import com.acusportrtg.axismobile.JSON_Classes.FirearmInfo;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockScan;
import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.Methods.CustomDrawerBuilder;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.SharedPrefs;

import com.mikepenz.materialdrawer.Drawer;

import org.json.JSONException;
import org.json.JSONObject;


import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 5/2/2017.
 */

public class SearchFirearmsActivity extends AppCompatActivity implements /*EMDKListener,*/ Firearm_Inv_Type_Dialog.OnFragmentInteractionListener{


    private String JSONReturnData = "";
    private FirearmInfo fi = new FirearmInfo();
    private FirearmStockScan fss;
    private EditText edt_input_scanned;
    private RadioButton radio_serial, radio_log;
    private String currentFirearmType;

    private Drawer result = null;
    private GetEmployees emp;

    //Assign the profile name used in EMDKConfig.xml
    //private String profileName = "Barcode_Read";

    //Declare a variable to store ProfileManager object
    //private ProfileManager mProfileManager = null;

    //Declare a variable to store EMDKManager object
    //private EMDKManager emdkManager = null;

    private EmployeeRoles empRoles = new EmployeeRoles();
    //private BarcodeReader barcodeReader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_search_firearms);

        Globals glob = ((Globals)getApplicationContext());
        emp = glob.getEmployee();

        //The EMDKManager object will be created and returned in the callback.
        /*EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

        //Check the return status of getEMDKManager
        if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
        {
            //Failed to create EMDKManager object

        }
        */

        empRoles = glob.getEmpRoles();


        if (currentFirearmType == null) {
            showDialog();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        CustomDrawerBuilder customDrawerBuilder = new CustomDrawerBuilder();
        customDrawerBuilder.CustomDrawer(SearchFirearmsActivity.this,SearchFirearmsActivity.this,toolbar,result,savedInstanceState,emp, empRoles);


        radio_serial = (RadioButton) findViewById(R.id.rdl_serial_number);
        radio_log = (RadioButton) findViewById(R.id.rdl_log_number);
        final Button btn_search = (Button) findViewById(R.id.btn_search);
        final Button btn_count = (Button) findViewById(R.id.btn_count_submit);
        edt_input_scanned = (EditText) findViewById(R.id.edt_firearm_scan);
        final Button btn_clear = (Button) findViewById(R.id.btn_clear);

        radio_log.setChecked(true);
        edt_input_scanned.setHint("Log Number");
        edt_input_scanned.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_input_scanned.requestFocus();

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout firearm_view = (ConstraintLayout) findViewById(R.id.constraint_firearminfo);
                firearm_view.setVisibility(View.INVISIBLE);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            SearchFirearm();
            }
        });

        edt_input_scanned.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == keyEvent.KEYCODE_ENTER) {
                    SearchFirearm();
                    return true;
                }
                return false;
            }
        });

        /*edt_input_scanned.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode ==  KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode ==  KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (edt_input_scanned.getText().toString().trim().length() == 0) {
                            edt_input_scanned.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(getCurrentFocus(),InputMethodManager.SHOW_IMPLICIT);
                        }
                        else
                            SearchFirearm();
                    }
                    return true;
                } else {
                    return false;
                }
            }

        });*/

        View.OnClickListener radio_serial_listener = new View.OnClickListener(){
            public void onClick(View v) {
                if(radio_serial.isChecked()){
                    edt_input_scanned.setInputType(InputType.TYPE_CLASS_TEXT);
                    edt_input_scanned.setHint("Serial Number");
                    edt_input_scanned.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
                }
                if(radio_log.isChecked()){
                    radio_log.setChecked(false);
                }
            }
        };
        View.OnClickListener radio_log_listener = new View.OnClickListener(){
            public void onClick(View v) {
                if(radio_log.isChecked()){
                    edt_input_scanned.setInputType(InputType.TYPE_CLASS_NUMBER);
                    edt_input_scanned.setHint("Log Number");
                    edt_input_scanned.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
                }
                if(radio_serial.isChecked()){
                    radio_serial.setChecked(false);
                }
            }
        };
        radio_serial.setOnClickListener(radio_serial_listener);
        radio_log.setOnClickListener(radio_log_listener);

        edt_input_scanned.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!edt_input_scanned.hasFocus()) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });


    }

    private void SearchFirearm() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        fss = new FirearmStockScan();
        String str_input_scanned = edt_input_scanned.getText().toString().trim();
        edt_input_scanned.setText("");
        if(radio_log.isChecked()){
            //Do Log number stuff
            try {
                Long inv_nbr = Long.parseLong(str_input_scanned);
                fss.setLog(inv_nbr);
                fss.setLogScanned(true);
                fss.setSerialScanned(false);
                fss.setSerialNumber("");
                fss.setBoundBookType(currentFirearmType);
                GetFirearmInfo(fss,SearchFirearmsActivity.this);

            }  catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else if (radio_serial.isChecked()) {
            //Do Serialnumber stuff
            try {
                fss.setSerialNumber(str_input_scanned);
                fss.setLogScanned(false);
                fss.setSerialScanned(true);
                fss.setLog((long) 1);
                fss.setBoundBookType(currentFirearmType);
                GetFirearmInfo(fss,SearchFirearmsActivity.this);
            }  catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Toast.makeText(SearchFirearmsActivity.this,"Please Select a scanning mode.",Toast.LENGTH_LONG).show();
        }

    }

    public void GetFirearmInfo (FirearmStockScan fss, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetFirearmInformation");
            postData.put("Log",fss.getLog());
            postData.put("SerialNumber", fss.getSerialNumber());
            postData.put("SerialScanned", fss.isSerialScanned());
            postData.put("LogScanned", fss.isLogScanned());
            postData.put("BoundBookType", fss.getBoundBookType());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    Log.v(TAG,"JSONReturnData GetFirearmInfo:\n<"+JSONReturnData + ">");
                    if (JSONReturnData.equals("")) {
                        Toast.makeText(SearchFirearmsActivity.this,"Invalid Scan",Toast.LENGTH_LONG).show();
                    } else {
                        fi = GetFirearmInfo(JSONReturnData);
                        fillFirearmInfo(fi);
                    }

                }};
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            Log.v(TAG,JSONReturnData);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }

    private void fillFirearmInfo(FirearmInfo fi) {
        TextView txt_manufacture_data = (TextView) findViewById(R.id.txtManufactureData),
                txt_serial_data = (TextView) findViewById(R.id.txt_serial_data),
                txt_upc_data = (TextView) findViewById(R.id.txt_upc_data),
                txt_firearm_desc_data = (TextView) findViewById(R.id.txt_firearm_desc_data),
                txt_log_data = (TextView) findViewById(R.id.txt_log_data),
                txt_type_of_action_data = (TextView) findViewById(R.id.txt_type_of_action_data),
                txt_new_or_used_data = (TextView) findViewById(R.id.txt_new_or_used_data),
                txt_model_data = (TextView) findViewById(R.id.txt_model_data),
                txt_importer_data = (TextView) findViewById(R.id.txt_importer_data),
                txt_gauge_data = (TextView) findViewById(R.id.txt_gauge_data);
        CheckBox chk_sold = (CheckBox) findViewById(R.id.chk_sold),
                chk_committed = (CheckBox) findViewById(R.id.chk_committed);

        ConstraintLayout firearm_view = (ConstraintLayout) findViewById(R.id.constraint_firearminfo);
        txt_manufacture_data.setText(fi.getManufacturer());
        txt_serial_data.setText(fi.getSerialNumber());
        txt_upc_data.setText(fi.getUPC());
        txt_firearm_desc_data.setText(fi.getDescription());
        txt_log_data.setText(String.valueOf(fi.getInvNbr()));
        txt_type_of_action_data.setText(fi.getTypeOfAction());
        txt_new_or_used_data.setText(fi.getNewUsed());
        txt_model_data.setText(fi.getModel());
        txt_importer_data.setText(fi.getImporter());
        txt_gauge_data.setText(fi.getGaugeCaliber());
        if(fi.getStatus().equals("O")){
            chk_sold.setChecked(true);
        }
        else{
            chk_sold.setChecked(false);
        }
        firearm_view.setVisibility(View.VISIBLE);
    }

    private FirearmInfo GetFirearmInfo(String jsonStr) {
        if (!jsonStr.equals("")) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                FirearmInfo fi = new FirearmInfo();
                fi.setSerialNumber(obj.getString("SerialNumber"));
                fi.setDescription(obj.getString("Description"));
                fi.setGaugeCaliber(obj.getString("GaugeCaliber"));
                fi.setInvNbr(obj.getLong("InvNbr"));
                fi.setManufacturer(obj.getString("Manufacturer"));
                fi.setModel(obj.getString("Model"));
                fi.setNewUsed(obj.getString("NewUsed"));
                fi.setStatus(obj.getString("Status"));
                fi.setUPC(obj.getString("UPC"));
                fi.setTypeOfAction(obj.getString("TypeOfAction"));
                fi.setInventoryNumber(obj.getInt("InventoryNumber"));
                fi.setImporter(obj.getString("Importer"));
                return fi;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.firearm_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case R.id.firearm_type:
                showDialog();
                break;
            default:
                break;
        }

        return true;
    }


    void showDialog() {
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("firearm_inv_type_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        Firearm_Inv_Type_Dialog newFragment = new Firearm_Inv_Type_Dialog();
        newFragment.setCancelable(false);
        newFragment.show(ft,"dialog");
    }

    @Override
    public void onFragmentInteraction(String firearmType) {
        //Toast.makeText(SearchFirearmsActivity.this,firearmType,Toast.LENGTH_SHORT).show();
        currentFirearmType = firearmType;
        getSupportActionBar().setTitle("Firearm Search"+ ": " + firearmType);
        if (currentFirearmType == null)
            showDialog();
    }



    public void playSuccess() {
        try {
            MediaPlayer mp = MediaPlayer.create(SearchFirearmsActivity.this, R.raw.snd_scan_success);
            mp.start();
        } catch (Exception e) {
            Log.e(TAG, "Error play sound: " + e);
            e.printStackTrace();
        }
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
