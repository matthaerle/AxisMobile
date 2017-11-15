package com.acusportrtg.axismobile;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import static android.content.ContentValues.TAG;
import android.widget.CompoundButton;
import android.app.AlertDialog;

import com.acusportrtg.axismobile.JSON_Classes.EmployeeRoles;
import com.acusportrtg.axismobile.JSON_Classes.FirearmInfo;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockScan;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockUpdate;
import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.IsFirearmDisposed;
import com.acusportrtg.axismobile.JSON_Classes.UpdateStatus;
import com.acusportrtg.axismobile.Methods.CustomDrawerBuilder;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithPOSTData;
import com.acusportrtg.axismobile.Methods.SharedPrefs;

import com.symbol.emdk.*;
import com.symbol.emdk.EMDKManager.EMDKListener;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhaerle on 4/21/2017.
 */

public class InventoryFirearmsActivity extends AppCompatActivity  implements EMDKListener, Firearm_Inv_Type_Dialog.OnFragmentInteractionListener{
    private FirearmInfo fi = new FirearmInfo();
    private GetEmployees emp;
    private boolean displayedHint = false;
    private FirearmStockScan fss;
    private RadioButton radio_serial, radio_log;
    private EditText edt_input_scanned;
    private String currentFirearmType;
    private Switch switch_continuous_mode;
    private Drawer result = null;
    private Toolbar toolbar;


    //Assign the profile name used in EMDKConfig.xml
    private String profileName = "Barcode_Read";

    //Declare a variable to store ProfileManager object
    private ProfileManager mProfileManager = null;

    //Declare a variable to store EMDKManager object
    private EMDKManager emdkManager = null;

    private EmployeeRoles empRoles = new EmployeeRoles();


    private String JSONReturnData = "";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Globals glob = ((Globals)getApplicationContext());
        emp = glob.getEmployee();
        empRoles = glob.getEmpRoles();
        showDialog();


        //The EMDKManager object will be created and returned in the callback.
        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);

//Check the return status of getEMDKManager
        if(results.statusCode == EMDKResults.STATUS_CODE.FAILURE)
        {
            //Failed to create EMDKManager object

        }

        setContentView(R.layout.activity_inventory_firearms);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d("Device", Build.MODEL);



        CustomDrawerBuilder customDrawerBuilder = new CustomDrawerBuilder();
        customDrawerBuilder.CustomDrawer(InventoryFirearmsActivity.this,InventoryFirearmsActivity.this,toolbar,result,savedInstanceState,emp, empRoles);
        radio_serial = (RadioButton) findViewById(R.id.rdl_serial_number);
        radio_log = (RadioButton) findViewById(R.id.rdl_log_number);
        final Button btn_search = (Button) findViewById(R.id.btn_search);
        final Button btn_count = (Button) findViewById(R.id.btn_count_submit);
        edt_input_scanned = (EditText) findViewById(R.id.edt_firearm_scan);
        edt_input_scanned.requestFocus();
        switch_continuous_mode = (Switch) findViewById(R.id.swtch_continuous_mode);

        radio_log.setChecked(true);
        edt_input_scanned.setHint("Log Number");
        edt_input_scanned.setInputType(InputType.TYPE_CLASS_NUMBER);

        switch_continuous_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    btn_search.setText(R.string.btn_count);
                    if(!displayedHint){
                        new AlertDialog.Builder(InventoryFirearmsActivity.this)
                                .setTitle("Continuous Mode")
                                .setMessage("With this active you can scan continuously without needing to click search or count after scanning.")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        displayedHint = true;
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch_continuous_mode.setChecked(false);
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }}
            else {
                btn_search.setText(R.string.btn_firearm_scan_search);
            }
            }});

        btn_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirearmStockUpdate fsu = new FirearmStockUpdate();
                fsu.setInventoryNumber(fi.getInventoryNumber());
                fsu.setEmployeeID(emp.getEmployeeID());
                fsu.setMachineName("Axis Mobile");
                fsu.setBoundBookType(currentFirearmType);
                try {
                    UpdateFirearmScan(fsu,InventoryFirearmsActivity.this);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }    }
        });

        edt_input_scanned.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (s.length() > 0) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    edt_input_scanned.setTextColor(Color.parseColor("#2980b9"));
                    edt_input_scanned.getBackground().setColorFilter(Color.parseColor("#2980b9"), PorterDuff.Mode.SRC_ATOP);
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
                if(edt_input_scanned.getText().toString().trim().length() == 0){
                    edt_input_scanned.setTextColor(Color.parseColor("#95a5a6"));
                    edt_input_scanned.getBackground().setColorFilter(Color.parseColor("#95a5a6"), PorterDuff.Mode.SRC_ATOP);
                }
            }
        });

        edt_input_scanned.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == keyEvent.KEYCODE_ENTER) {
                    Search();
                    return true;
                }
                return false;
            }
        });

        edt_input_scanned.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!edt_input_scanned.hasFocus()) {
                    InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(LoginActivity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        View.OnClickListener radio_serial_listener = new View.OnClickListener(){
            public void onClick(View v) {
                if(radio_serial.isChecked()){
                    edt_input_scanned.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
                    edt_input_scanned.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
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
    }

    private void Search() {
        if(!switch_continuous_mode.isChecked()){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
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
                GetFirearmDisposed(fss, InventoryFirearmsActivity.this);

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
                GetFirearmDisposed(fss, InventoryFirearmsActivity.this);

            }  catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            Toast.makeText(InventoryFirearmsActivity.this,"Please Select a scanning mode.",Toast.LENGTH_LONG).show();
        }
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

    private UpdateStatus FirearmCounted(String jsonStr) {
        if (!jsonStr.equals("")) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                UpdateStatus us = new UpdateStatus();
                us.setSuccesfull(obj.getBoolean("IsSuccesfull"));
                return us;
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



    private IsFirearmDisposed VerifyFirearmDisposedA(String jsonStr) {
        if(!jsonStr.equals("")) {
            try {
                JSONObject obj = new JSONObject(jsonStr);
                IsFirearmDisposed ifd = new IsFirearmDisposed();
                ifd.setDisposed(obj.getBoolean("Disposed"));
                ifd.setInventoryNumber(obj.getInt("InventoryNumber"));
                return ifd;
            }catch (final JSONException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage() + e.getLocalizedMessage());
                    }});
            }}return null;
    }

    public void UpdateFirearmScan (FirearmStockUpdate fsu, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/CountFirearm");
            postData.put("InventoryNumber", fsu.getInventoryNumber());
            postData.put("EmployeeID", fsu.getEmployeeID());
            postData.put("MachineName", fsu.getMachineName());
            postData.put("BoundBookType", fsu.getBoundBookType());
            Log.v("PostData to Send", postData.toString());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context){
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    Log.v(TAG, "Update FirearmScan JSONReturnData:\n"+JSONReturnData);
                    UpdateStatus us = FirearmCounted(JSONReturnData);
                    if (us != null) {
                        if (us.isSuccesfull()){
                            if(radio_log.isChecked()){
                                Toast.makeText(InventoryFirearmsActivity.this,"Log " + Long.toString(fi.getInvNbr()) + " counted!",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(InventoryFirearmsActivity.this,"Serial " + fi.getSerialNumber() + " counted!",Toast.LENGTH_LONG).show();
                            }
                            hideFirearmInfo();
                        }
                        else
                            Toast.makeText(InventoryFirearmsActivity.this,"Unable to Update count.",Toast.LENGTH_LONG).show();
                    }}};
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            Log.v("GetJSONWithPostData ",JSONReturnData);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
    }

    public void GetFirearmDisposed (final FirearmStockScan fss, final Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/VerifyFirearmNotDisposed");
            postData.put("Log",fss.getLog());
            postData.put("SerialNumber", fss.getSerialNumber());
            postData.put("SerialScanned", fss.isSerialScanned());
            postData.put("LogScanned", fss.isLogScanned());
            postData.put("BoundBookType",fss.getBoundBookType());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    Log.v(TAG,"GetFirearm Disposed, JSONReturnData"+JSONReturnData);
                    IsFirearmDisposed ifd = VerifyFirearmDisposedA(JSONReturnData);
                    if (ifd ==null) {
                        Toast.makeText(InventoryFirearmsActivity.this,"Invalid Scan",Toast.LENGTH_SHORT).show();
                    } else {
                        if (ifd.isDisposed()) {
                            Toast.makeText(InventoryFirearmsActivity.this,"Firearm Is Disposed",Toast.LENGTH_SHORT).show();
                        } else if (!ifd.isDisposed()) {
                            //Toast.makeText(InventoryFirearmsActivity.this,"Firearm is In-Stock", Toast.LENGTH_SHORT).show();
                            GetFirearmInfo(fss, InventoryFirearmsActivity.this);
                        }}}};
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            Log.v("GetJSONWithPostData ",JSONReturnData);
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
        firearm_view.setVisibility(View.VISIBLE);

    }
    private void hideFirearmInfo() {
        ConstraintLayout firearm_view = (ConstraintLayout) findViewById(R.id.constraint_firearminfo);
        firearm_view.setVisibility(View.INVISIBLE);
    }



    public void GetFirearmInfo (FirearmStockScan fss,Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetFirearmInformation");
            postData.put("Log",fss.getLog());
            postData.put("SerialNumber", fss.getSerialNumber());
            postData.put("SerialScanned", fss.isSerialScanned());
            postData.put("LogScanned", fss.isLogScanned());
            postData.put("BoundBookType",fss.getBoundBookType());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    Log.v(TAG,"JSONReturnData GetFirearmInfo:\n"+JSONReturnData);
                    fi = GetFirearmInfo(JSONReturnData);
                    Switch switch_continuous_mode = (Switch) findViewById(R.id.swtch_continuous_mode);
                    if (switch_continuous_mode.isChecked()) {
                        FirearmStockUpdate fsu = new FirearmStockUpdate();
                        fsu.setInventoryNumber(fi.getInventoryNumber());
                        fsu.setEmployeeID(emp.getEmployeeID());
                        fsu.setMachineName("Axis Mobile");
                        fsu.setBoundBookType(currentFirearmType);
                        edt_input_scanned.requestFocus();
                        edt_input_scanned.setText("");
                        try
                        {
                            UpdateFirearmScan(fsu,InventoryFirearmsActivity.this);
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    } else {
                        if (fi != null) {
                            fillFirearmInfo(fi);
                        } else {
                            Log.e(TAG,"Error filling firearm info");
                            edt_input_scanned.requestFocus();
                            edt_input_scanned.setText("");
                        }
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

    @Override
    public void onFragmentInteraction(String firearmType) {
        //Toast.makeText(InventoryFirearmsActivity.this,firearmType,Toast.LENGTH_SHORT).show();
        currentFirearmType = firearmType;
        toolbar.setTitle("Firearm Inventory"+ ": " + firearmType);
    }





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
}
