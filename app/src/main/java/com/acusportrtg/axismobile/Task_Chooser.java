package com.acusportrtg.axismobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.acusportrtg.axismobile.Methods.Globals;

import org.w3c.dom.Text;

/**
 * Created by mhaerle on 4/7/2017.
 */

public class Task_Chooser extends AppCompatActivity{
    private TextView txt_Emp;
    private Button btn_Product_Search,btn_Inventory,btn_Update_MinMax,btn_Firearm_Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_chooser);
        txt_Emp = (TextView)findViewById(R.id.txtEmpId);

        Globals glob = ((Globals)getApplicationContext());
        txt_Emp.setText(String.valueOf(glob.getEmployee_Id()));
        btn_Product_Search = (Button)findViewById(R.id.btn_product_search);
        btn_Firearm_Search = (Button)findViewById(R.id.btn_firearm_search);
        btn_Inventory = (Button)findViewById(R.id.btn_inventory);
        btn_Update_MinMax = (Button)findViewById(R.id.btn_update_minmax);

        btn_Product_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Item_Search = new Intent(Task_Chooser.this,Item_Search_Activity.class);
                startActivity(Item_Search);
            }
        });

        btn_Firearm_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_Inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventory_activity = new Intent(Task_Chooser.this, Inventory_Type_Select_Activity.class);
                startActivity(inventory_activity);
            }
        });

        btn_Update_MinMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Update_Min_Max = new Intent(Task_Chooser.this, Update_Min_Max_Activity.class);
                startActivity(Update_Min_Max);
            }
        });
    }
}
