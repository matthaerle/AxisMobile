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
    private Button btn_Item_Search,btn_Inventory,btn_Update_MinMax,btn_Firearm_Inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_chooser);
        /*txt_Emp = (TextView)findViewById(R.id.txtEmpId);

        Globals glob = ((Globals)getApplicationContext());
        txt_Emp.setText(String.valueOf(glob.getEmployee_Id()));*/
        btn_Inventory = (Button)findViewById(R.id.btn_Inventory);
        btn_Item_Search = (Button)findViewById(R.id.btn_Item_Search);
        btn_Firearm_Inventory = (Button)findViewById(R.id.btn_Firearm_Inventory);
        btn_Update_MinMax = (Button)findViewById(R.id.btn_Update_MinMax);

        btn_Update_MinMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_Firearm_Inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_Item_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btn_Inventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inventory_activity = new Intent(Task_Chooser.this, Inventory_Task.class);
                startActivity(inventory_activity);
            }
        });
    }
}
