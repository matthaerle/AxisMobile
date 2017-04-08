package com.acusportrtg.axismobile.Methods;

import android.content.Context;
import android.os.AsyncTask;

import com.acusportrtg.axismobile.JSON_Classes.IsConnected;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mhaerle on 4/7/2017.
 */

public class VerifyServerConnection {
    IsConnected verified = new IsConnected();
    Context context;

    public  IsConnected VerifyConnection(Context context) {
        String address = ServerAddress.GetSavedServerAddress(context);
        if (address != "") {
            try {
                URL connectionUrl = new URL("http://" + address + ":8899/RestWCFServiceLibrary/IsConnected");
                new VerifyServerConnected().execute(connectionUrl);
                if (verified.getConnectionVerified() != null) {

                }
                return verified;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                verified.setConnectionVerified(false);
                return verified;
            }
        }
        verified.setConnectionVerified(false);
        return verified;
    }
    private class VerifyServerConnected extends AsyncTask<URL, Void, Void> {


        @Override
        protected Void doInBackground(URL... params) {
            GetJSONStringWithoutPostData serveConnect = new GetJSONStringWithoutPostData();
            String jsonStr = serveConnect.GetJSONString((URL)params[0]);

            if(jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    verified.setConnectionVerified(jsonObj.getBoolean("ConnectionVerified"));

                } catch (final JSONException e) {
                    verified.setConnectionVerified(false);
                }
            }
            return null;
        }



    }
}
