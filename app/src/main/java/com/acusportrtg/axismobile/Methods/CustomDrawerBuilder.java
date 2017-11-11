package com.acusportrtg.axismobile.Methods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.acusportrtg.axismobile.AppSettingsActivity;
import com.acusportrtg.axismobile.Globals;
import com.acusportrtg.axismobile.InventoryFirearmsActivity;
import com.acusportrtg.axismobile.InventoryProductsActivity;
import com.acusportrtg.axismobile.Inventory_Task;
import com.acusportrtg.axismobile.JSON_Classes.EmployeeRoles;
import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.JSON_Classes.SendProductView;
import com.acusportrtg.axismobile.LoginActivity;
import com.acusportrtg.axismobile.R;
import com.acusportrtg.axismobile.SearchFirearmsActivity;
import com.acusportrtg.axismobile.SearchProductsActivity;
import com.acusportrtg.axismobile.UpdateMinMaxActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by mhaerle on 10/31/2017.
 */

public class CustomDrawerBuilder {
    private IProfile profile;
    private AccountHeader headerResult = null;
    private String JSONReturnData = "";
    private EmployeeRoles empRoles = new EmployeeRoles();
    public Drawer CustomDrawer(Context context, Activity activity, Toolbar toolbar, Drawer result, Bundle savedInstanceState, GetEmployees employee) {
        final Context context1 = context;
        final Globals globals = new Globals();

        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + SharedPrefs.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetEmployeeRoles");
            postData.put("EmployeeID",employee.getEmployeeID());
            GetJSONStringWithPOSTData.GetJSONDataBack getJSONDataBack = new GetJSONStringWithPOSTData.GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                    try {
                        JSONArray productJson = new JSONArray(JSONReturnData);
                        empRoles = GetEmployeeRoles(productJson);

                    } catch (final JSONException e) {
                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                    }

                }};
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            Log.v(TAG,JSONReturnData);

        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }


        profile = new ProfileDrawerItem().withName(employee.getFirstName() + ' ' + employee.getLastName());
        buildHeader(false, savedInstanceState, activity);
        result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.btn_product_search).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.btn_firearm_search).withIdentifier(2).withSelectable(false),
                        new ExpandableDrawerItem().withName(R.string.btn_Inventory).withIdentifier(3).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName(R.string.firearm_inventory_title).withLevel(2).withIdentifier(2000),
                                new SecondaryDrawerItem().withName(R.string.inv_stocktaking).withLevel(2).withIdentifier(2001)
                        ),
                        new PrimaryDrawerItem().withName(R.string.btn_Update_MinMax).withIdentifier(4).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1)
                                intent = new Intent(context1,SearchProductsActivity.class);
                            else if (drawerItem.getIdentifier() == 2 )
                                intent = new Intent(context1,SearchFirearmsActivity.class);
                            else if (drawerItem.getIdentifier() == 2000)
                                intent = new Intent(context1,InventoryFirearmsActivity.class);
                            else if (drawerItem.getIdentifier() == 2001)
                                intent = new Intent(context1,Inventory_Task.class);
                            else if (drawerItem.getIdentifier() == 4)
                                intent = new Intent(context1,UpdateMinMaxActivity.class);
                            else if (drawerItem.getIdentifier() == 5 ) {
                                intent = new Intent(context1, LoginActivity.class);
                                globals.setEmployee(null);
                            }
                            if (intent != null)
                                context1.startActivity(intent);
                        }
                        return false;
                    }
                })
                .addStickyDrawerItems(
                        new SecondaryDrawerItem().withName(R.string.logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(5)
                )
                .withSavedInstance(savedInstanceState)
                .build();
        if(!empRoles.getInventoryManagementPermission()){
            result.removeItem(1);
        }
        if(!empRoles.getFirearmsPermission()){
            Log.v(TAG,empRoles.getFirearmsPermission().toString());
            result.removeItem(2);
        }
        if(!empRoles.getFirearmPhysicalInventory() && !empRoles.getIMProdMaintAdjQoHPermission()){
            result.removeItem(3);
        }

        return result;
    }

    private EmployeeRoles GetEmployeeRoles(JSONArray employeeRolesJson){
        EmployeeRoles empR = new EmployeeRoles();
        try{
            //JSONArray productJson = new JSONArray(jsonStr);
            if(employeeRolesJson.length() == 1){
                for (int i=0; i <employeeRolesJson.length(); i++) {
                    JSONObject p = employeeRolesJson.getJSONObject(i);

                    empR.setFirearmBoundBookPermission(p.has("FirearmBoundBook"));
                    empR.setFirearmsPermission(p.has("Firearms"));
                    empR.setFirearmPhysicalInventoryPermission(p.has("FirearmPhysicalInventory"));
                    empR.setFirearmViewCostPermission(p.has("FirearmViewCost"));
                    empR.setPricingPermission(p.has("Pricing"));
                    empR.setProductCostPermission(p.has("ProductCost"));
                    empR.setIMProdMaintAdjQoHPermission(p.has("IMProdMaintAdjQoH"));
                    empR.setIMUpdatePermission(p.has("IMUpdate"));
                    empR.setInventoryManagementPermission(p.has("InventoryManagement"));
                }
            }

        } catch (final JSONException e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
        }
        return empR;
    }

    private void buildHeader(boolean b, Bundle savedInstanceState,Activity activity) {
        headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withTranslucentStatusBar(false)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .addProfiles(profile)
                .build();
    }
}
