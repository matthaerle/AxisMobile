package com.acusportrtg.axismobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.acusportrtg.axismobile.Methods.Globals;

import org.w3c.dom.Text;

/**
 * Created by mhaerle on 4/7/2017.
 */

public class Task_Chooser extends AppCompatActivity{
    private TextView txt_Emp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_chooser);
        txt_Emp = (TextView)findViewById(R.id.txtEmpId);

        Globals glob = ((Globals)getApplicationContext());
        txt_Emp.setText(String.valueOf(glob.getEmployee_Id()));
    }
}
