package com.acusportrtg.axismobile;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.EmployeeRoles;

/**
 * Created by mhaerle on 4/7/2017.
 */

public class TaskChooserActivity extends AppCompatActivity{
    private TextView txt_Emp;
    private Button btn_Product_Search,btn_Inventory,btn_Update_MinMax,btn_Firearm_Search;
    private EmployeeRoles empRoles = new EmployeeRoles();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_chooser);
        txt_Emp = (TextView)findViewById(R.id.txtEmpId);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setTitle("Main Menu");

        Globals glob = ((Globals)getApplicationContext());
        txt_Emp.setText(String.valueOf(glob.getEmployee().getEmployeeNumber()));
        btn_Product_Search = (Button)findViewById(R.id.btn_product_search);
        btn_Firearm_Search = (Button)findViewById(R.id.btn_firearm_search);
        btn_Inventory = (Button)findViewById(R.id.btn_inventory);
        btn_Update_MinMax = (Button)findViewById(R.id.btn_update_minmax);

        empRoles = glob.getEmpRoles();


        if(empRoles != null){
            if(!empRoles.getInventoryManagementPermission()){
                btn_Product_Search.setAlpha(.3f);
                //btn_Product_Search.setEnabled(false);
            }
            if(!empRoles.getFirearmsPermission()){
                btn_Firearm_Search.setAlpha(.3f);
                //btn_Firearm_Search.setEnabled(false);
            }
            if(!empRoles.getFirearmPhysicalInventory() && !empRoles.getIMInvStockTaking()){
                btn_Inventory.setAlpha(.3f);
            }
            if(!empRoles.getIMUpdatePermission()){
                btn_Update_MinMax.setAlpha(.3f);
            }
        }

        btn_Product_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!empRoles.getInventoryManagementPermission()){
                    Toast toast = Toast.makeText(TaskChooserActivity.this, "InventoryManagement\nPermission Required", Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Intent Item_Search = new Intent(TaskChooserActivity.this,SearchProductsActivity.class);
                    startActivity(Item_Search);
                }
            }
        });

        btn_Firearm_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!empRoles.getFirearmsPermission()){
                    Toast.makeText(TaskChooserActivity.this, "Firearms Permission Required", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent firearm_search = new Intent(TaskChooserActivity.this,SearchFirearmsActivity.class);
                    startActivity(firearm_search);
                }

            }
        });

        btn_Inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!empRoles.getFirearmPhysicalInventory() && !empRoles.getIMInvStockTaking()){
                    Toast toast = Toast.makeText(TaskChooserActivity.this, "FirearmPhysicalInventory &\nIMInvStockTaking\nPermission Required", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    Intent inventory_activity = new Intent(TaskChooserActivity.this, InventoryTasksActivity.class);
                    startActivity(inventory_activity);
                }

            }
        });

        btn_Update_MinMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!empRoles.getIMUpdatePermission()) {
                    Toast.makeText(TaskChooserActivity.this, "IMUpdate Permission Required", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent Update_Min_Max = new Intent(TaskChooserActivity.this, UpdateMinMaxActivity.class);
                    startActivity(Update_Min_Max);
                }

            }
        });
    }
}
