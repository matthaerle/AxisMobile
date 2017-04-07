package com.acusportrtg.axismobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.acusportrtg.axismobile.JSON_Classes.IsConnected;
import com.acusportrtg.axismobile.Methods.ServerAddress;
import com.acusportrtg.axismobile.Methods.VerifyServerConnection;

import java.net.URL;

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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if server can connect and then move on
                CheckServerConnected();
            }
        });
    }

    private void CheckServerConnected() {
        VerifyServerConnection verify = new VerifyServerConnection();
        ServerAddress serverAddress = new ServerAddress();
        IsConnected verified;

        verified =  verify.VerifyConnection(this);
            if (verified.getConnectionVerified()) {
                Intent task_chooser = new Intent(MainActivity.this,
                        Task_Chooser.class);
                startActivity(task_chooser);
            }

    }

}
