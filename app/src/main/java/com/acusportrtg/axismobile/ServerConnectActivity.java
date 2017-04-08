package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.IsConnected;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.ServerAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class ServerConnectActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    EditText server_address_txtbox;
    Button server_Connect_btn;
    IsConnected verified = new IsConnected();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_connect);
        verified.setConnectionVerified(false);
        server_Connect_btn = (Button) findViewById(R.id.btn_Connect);
        server_address_txtbox = (EditText) findViewById(R.id.server_address_textbox);
        server_Connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyConnection();
            }
        });
    }

    private void VerifyConnection() {
        String addressInput = server_address_txtbox.getText().toString().trim();
        try {
            URL connectionUrl = new URL("http://" + addressInput + ":8899/RestWCFServiceLibrary/IsConnected");
            new VerifyServerConnected().execute(connectionUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class VerifyServerConnected extends AsyncTask<URL, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ServerConnectActivity.this);
            pDialog.setMessage("Verifing Connection...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(URL... params) {
            GetJSONStringWithoutPostData serveConnect = new GetJSONStringWithoutPostData();
            String jsonStr = serveConnect.GetJSONString(params[0]);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    verified.setConnectionVerified(jsonObj.getBoolean("ConnectionVerified"));

                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(pDialog.isShowing())
                pDialog.dismiss();
            finishVerify();
        }


    }
    private void finishVerify() {
        if(verified.getConnectionVerified() != null) {
            Toast.makeText(ServerConnectActivity.this
                    , verified.getConnectionVerified().toString(),
                    Toast.LENGTH_LONG).show();
        }
        if(verified.getConnectionVerified()) {
            ServerAddress.SetSavedServerAddress(server_address_txtbox.getText().toString().trim(),this);
            Intent task_chooser = new Intent(ServerConnectActivity.this,
                    Task_Chooser.class);
            startActivity(task_chooser);
        }

    }
}
