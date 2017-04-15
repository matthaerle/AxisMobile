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
 * Created by jpederson on 4/15/2017.
 */

public class Inventory_Type_Select_Activity extends AppCompatActivity {
    //private TextView txt_Emp;
    private Button btn_Non_Firearm_Inventory, btn_Firearm_Inventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_task_chooser);
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

}
