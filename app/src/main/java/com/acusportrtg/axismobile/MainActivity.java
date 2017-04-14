package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.IsConnected;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.ServerAddress;
import com.acusportrtg.axismobile.Methods.VerifyServerConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    IsConnected verified = new IsConnected();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);
        verified.setConnectionVerified(false);
        final Button setupButton = (Button) findViewById(R.id.btn_Setup);
        final Button loginButton = (Button) findViewById(R.id.btn_Login);
        setupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Loading");
                pDialog.setCancelable(false);
                pDialog.show();
                Intent server_Connection = new Intent(MainActivity.this,
                        ServerConnectActivity.class);
                pDialog.dismiss();
                startActivity(server_Connection);

            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if server can connect and then move on
                CheckServerConnected();
                Intent taskChooser = new Intent(MainActivity.this,Task_Chooser.class);
                startActivity(taskChooser);
            }
        });
    }

    //Comment

    private void CheckServerConnected() {
        VerifyServerConnection verify = new VerifyServerConnection();
        ServerAddress serverAddress = new ServerAddress();
        IsConnected verified;

        String stringAddress = ServerAddress.GetSavedServerAddress(this);
        try{
            URL servUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(this) + ":8899/RestWCFServiceLibrary/IsConnected");
            new VerifyServerConnected().execute(servUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private class VerifyServerConnected extends AsyncTask<URL, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Verifing Connection...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(URL... params) {
            GetJSONStringWithoutPostData serveConnect = new GetJSONStringWithoutPostData();
            String jsonStr = serveConnect.GetJSONString(params[0]);

            if(jsonStr != "") {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    verified.setConnectionVerified(jsonObj.getBoolean("ConnectionVerified"));

                } catch (final JSONException e) {
                    Log.e("Error connecting", e.getMessage(), e);
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


            if (!verified.getConnectionVerified()) {
                Toast.makeText(MainActivity.this, "Server Connection Needed", Toast.LENGTH_LONG).show();
                Intent server_Connect = new Intent(MainActivity.this,
                        ServerConnectActivity.class);
                startActivity(server_Connect);
            } else {
                Toast.makeText(MainActivity.this, "Server Connected", Toast.LENGTH_LONG).show();
            }

        }

    }

}
