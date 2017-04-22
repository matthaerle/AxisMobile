package com.acusportrtg.axismobile;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

/**
 * Created by mhaerle on 4/21/2017.
 */

public class Firearm_Scan_Activity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firearm_inventory_scan);

        final RadioButton radio_serial = (RadioButton) findViewById(R.id.rdl_serial_number);
        final RadioButton radio_log = (RadioButton) findViewById(R.id.rdl_log_number);



        View.OnClickListener radio_serial_listener = new View.OnClickListener(){
            public void onClick(View v) {
                if(radio_log.isChecked()){
                    radio_log.setChecked(false);
                }
            }
        };
        View.OnClickListener radio_log_listener = new View.OnClickListener(){
            public void onClick(View v) {
                if(radio_serial.isChecked()){
                    radio_serial.setChecked(false);
                }
            }
        };

        radio_serial.setOnClickListener(radio_serial_listener);
        radio_log.setOnClickListener(radio_log_listener);

    }
}
