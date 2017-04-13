package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.IsConnected;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.ServerAddress;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawableResource;

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
    private ImageView imageView;
    private Bitmap bitMap;
    EditText server_address_txtbox;
    Button server_Connect_btn;
    IsConnected verified = new IsConnected();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_connect);
        imageView = (ImageView)findViewById(R.id.serverIcon);
        verified.setConnectionVerified(false);
        LoadImage loadImg = new LoadImage(this);
        loadImg.execute();
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
    private void DisplayImage(){


    }

    private class LoadImage extends AsyncTask<Void,String,String> {
        private Context context;
        private ProgressDialog progressBar;

        public LoadImage(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressBar = new ProgressDialog(context);
            progressBar.setMessage("Loading.");
            progressBar.show();
        }
        @Override
        protected String doInBackground(Void... params) {
            bitMap = BitmapFactory.decodeResource(ServerConnectActivity.this.getResources(),R.mipmap.server_icon);
            return "finish";
        }
        @Override
        protected void onPostExecute(String result) {
            imageView.setImageBitmap(bitMap);
            progressBar.dismiss();
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
            if(verified.getConnectionVerified()) {
                Toast.makeText(ServerConnectActivity.this
                        , "Successful Connection",
                        Toast.LENGTH_LONG).show();
                ServerAddress.SetSavedServerAddress(server_address_txtbox.getText().toString().trim(),this);
                Intent employee_select = new Intent(ServerConnectActivity.this,
                        Employee_Select_Activity.class);
                startActivity(employee_select);
            } else {
                Toast.makeText(ServerConnectActivity.this
                        , "Invalid Connection",
                        Toast.LENGTH_LONG).show();
            }
        }


    }
}
