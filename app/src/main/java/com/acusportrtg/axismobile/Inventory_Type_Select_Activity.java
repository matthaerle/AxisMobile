package com.acusportrtg.axismobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.acusportrtg.axismobile.Methods.MyDrawerLayout;

import com.acusportrtg.axismobile.Methods.Globals;

import org.w3c.dom.Text;
/**
 * Created by jpederson on 4/15/2017.
 */

public class Inventory_Type_Select_Activity extends AppCompatActivity {
    //private TextView txt_Emp;
    private Button btn_Non_Firearm_Inventory, btn_Firearm_Inventory;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String myActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_task_chooser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        myActivityTitle = getSupportActionBar().getTitle().toString();


        /*txt_Emp = (TextView)findViewById(R.id.txtEmpId);

        Globals glob = ((Globals)getApplicationContext());
        txt_Emp.setText(String.valueOf(glob.getEmployee_Id()));*/
        btn_Non_Firearm_Inventory = (Button)findViewById(R.id.btn_non_firearm_inventory);
        btn_Firearm_Inventory = (Button)findViewById(R.id.btn_firearm_inventory);

        btn_Non_Firearm_Inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent non_firearm_activity = new Intent(Inventory_Type_Select_Activity.this,Inventory_Task.class);
                startActivity(non_firearm_activity);
            }
        });


    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close) {
            public void OnDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu();
            }
            public void OnDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(myActivityTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
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
