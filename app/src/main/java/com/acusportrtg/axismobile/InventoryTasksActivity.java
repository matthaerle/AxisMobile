package com.acusportrtg.axismobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.EmployeeRoles;
import com.acusportrtg.axismobile.JSON_Classes.GetEmployees;
import com.acusportrtg.axismobile.Methods.CustomDrawerBuilder;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;

/**
 * Created by jpederson on 4/15/2017.
 */

public class InventoryTasksActivity extends AppCompatActivity {
    //private TextView txt_Emp;
    private Button btn_Non_Firearm_Inventory, btn_Firearm_Inventory;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] mTaskTitles;
    private String myActivityTitle;
    private Drawer result = null;
    private AccountHeader headerResult = null;
    private GetEmployees emp;
    private EmployeeRoles empRoles = new EmployeeRoles();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_tasks);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);
        Globals glob = ((Globals)getApplicationContext());
        emp = glob.getEmployee();
        empRoles = glob.getEmpRoles();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        CustomDrawerBuilder customDrawerBuilder = new CustomDrawerBuilder();
        customDrawerBuilder.CustomDrawer(InventoryTasksActivity.this,InventoryTasksActivity.this,toolbar,result,savedInstanceState,emp, empRoles);
        //getSupportActionBar().setTitle("Inventory Type");

        myActivityTitle = getSupportActionBar().getTitle().toString();

        TextView txt_Emp = (TextView)findViewById(R.id.txtEmpId);
        txt_Emp.setText(String.valueOf(glob.getEmployee().getEmployeeNumber()));
        btn_Non_Firearm_Inventory = (Button)findViewById(R.id.btn_non_firearm_inventory);
        btn_Firearm_Inventory = (Button)findViewById(R.id.btn_firearm_inventory);


        if(empRoles != null){
            if(!empRoles.getIMInvStockTaking()){
                btn_Non_Firearm_Inventory.setAlpha(.3f);
                //btn_Product_Search.setEnabled(false);
            }
            if(!empRoles.getFirearmPhysicalInventory()){
                btn_Firearm_Inventory.setAlpha(.3f);
            }
        }

        btn_Non_Firearm_Inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!empRoles.getIMInvStockTaking()){
                    Toast.makeText(InventoryTasksActivity.this, "IMInvStockTaking\nPermission Required", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent non_firearm_activity = new Intent(InventoryTasksActivity.this,Inventory_Task.class);
                    startActivity(non_firearm_activity);
                }

            }
        });

        btn_Firearm_Inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!empRoles.getFirearmPhysicalInventory()){
                    Toast.makeText(InventoryTasksActivity.this, "FirearmPhysicalInventory\nPermission Required", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent firearm_inventory_activity = new Intent(InventoryTasksActivity.this,InventoryFirearmsActivity.class);
                    startActivity(firearm_inventory_activity);
                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer_, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
