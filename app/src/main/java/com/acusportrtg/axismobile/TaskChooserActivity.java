package com.acusportrtg.axismobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by mhaerle on 4/7/2017.
 */

public class TaskChooserActivity extends AppCompatActivity{
    private TextView txt_Emp;
    private Button btn_Product_Search,btn_Inventory,btn_Update_MinMax,btn_Firearm_Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_chooser);
        txt_Emp = (TextView)findViewById(R.id.txtEmpId);
        getSupportActionBar().setTitle("Main Menu");

        Globals glob = ((Globals)getApplicationContext());
        txt_Emp.setText(String.valueOf(glob.getEmployee().getEmployeeNumber()));
        btn_Product_Search = (Button)findViewById(R.id.btn_product_search);
        btn_Firearm_Search = (Button)findViewById(R.id.btn_firearm_search);
        btn_Inventory = (Button)findViewById(R.id.btn_inventory);
        btn_Update_MinMax = (Button)findViewById(R.id.btn_update_minmax);

        btn_Product_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Item_Search = new Intent(TaskChooserActivity.this,SearchProductsActivity.class);
                startActivity(Item_Search);
            }
        });

        btn_Firearm_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent firearm_search = new Intent(TaskChooserActivity.this,SearchFirearmsActivity.class);
                startActivity(firearm_search);
            }
        });

        btn_Inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventory_activity = new Intent(TaskChooserActivity.this, InventoryTasksActivity.class);
                startActivity(inventory_activity);
            }
        });

        btn_Update_MinMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Update_Min_Max = new Intent(TaskChooserActivity.this, UpdateMinMaxActivity.class);
                startActivity(Update_Min_Max);
            }
        });
    }
}
