package com.acusportrtg.axismobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        final Button setupButton = (Button) findViewById(R.id.btn_Setup);
        final Button loginButton = (Button) findViewById(R.id.btn_Login);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent server_Connection = new Intent(MainActivity.this,
                        ServerConnectActivity.class);
                startActivity(server_Connection);
            }
        });
    }

}
