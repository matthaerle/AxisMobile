package com.acusportrtg.axismobile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.acusportrtg.axismobile.JSON_Classes.IsConnected;
import com.acusportrtg.axismobile.Methods.GetJSONStringWithoutPostData;
import com.acusportrtg.axismobile.Methods.SharedPrefs;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhaerle on 4/6/2017.
 */

public class ServerConnectActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private ImageView imageView;
    private Bitmap bitMap;
    private EditText server_address_txtbox;
    private Button server_Connect_btn;
    private IsConnected verified = new IsConnected();
    private Drawer result = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LoadImage loadImg = new LoadImage(this);
        loadImg.execute();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_server_connection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        verified.setConnectionVerified(false);

        server_Connect_btn = (Button) findViewById(R.id.btn_Connect);
        server_address_txtbox = (EditText) findViewById(R.id.server_address_textbox);
        server_Connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VerifyConnection();
            }
        });


        server_address_txtbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                server_Connect_btn.setTextColor(Color.parseColor("#ffffff"));
                server_Connect_btn.setBackgroundResource(R.drawable.btn_solid_round);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if(server_address_txtbox.getText().toString().trim().length() == 0){
                    server_Connect_btn.setEnabled(false);
                    server_Connect_btn.setTextColor(Color.parseColor("#2980b9"));
                    server_Connect_btn.setBackgroundResource(R.drawable.btn_border_round);
                }
                else {
                    server_Connect_btn.setEnabled(true);
                }
            }
        });


        server_address_txtbox.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode ==  KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode ==  KeyEvent.KEYCODE_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    } else if (event.getAction() == KeyEvent.ACTION_UP) {
                        VerifyConnection();
                    }
                    return true;
                } else {
                    return false;
                }
            }

        });


        String serverIP = SharedPrefs.GetSavedServerAddress(this);
        if(!serverIP.isEmpty()){
            server_address_txtbox.setText(serverIP);
        }

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
            imageView = (ImageView)findViewById(R.id.serverIcon);
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
            pDialog.setCancelable(true);
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
                SharedPrefs.SetSavedServerAddress(server_address_txtbox.getText().toString().trim(),this);
                Intent employee_select = new Intent(ServerConnectActivity.this,
                        LoginActivity.class);
                startActivity(employee_select);
            } else {
                Toast.makeText(ServerConnectActivity.this
                        , "Invalid Connection",
                        Toast.LENGTH_LONG).show();
            }
        }


    }
}
