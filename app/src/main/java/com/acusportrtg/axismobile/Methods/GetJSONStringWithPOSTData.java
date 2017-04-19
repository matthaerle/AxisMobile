package com.acusportrtg.axismobile.Methods;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by Matt on 4/12/2017.
 */

public class GetJSONStringWithPOSTData {
    private String JSONReturnData = "";

    public String GetProductInfoJsonString (SearchByUPC upc, Context context) {
        String result = "";
        String stringAddress = ServerAddress.GetSavedServerAddress(context);
        JSONObject postData = new JSONObject();
        GetJSONDataBack get = new GetJSONDataBack();
        try {
            URL reqUrl = new URL("http://" + stringAddress + ":8899/RestWCFServiceLibrary/GetProductsByUPC");
            postData.put("ProductUPC", upc.getProductUPC());

            new GetJSONDataBack().execute(reqUrl.toString(), postData.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return JSONReturnData;
    }

    private class GetJSONDataBack extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... params) {
            String data = "";
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes("PostData="+params[1]);
                wr.flush();
                wr.close();

                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);

                int inputStreamData = inputStreamReader.read();
                while (inputStreamData != -1) {
                    char current = (char) inputStreamData;
                    inputStreamData = inputStreamReader.read();
                    data += current;
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException: " + e.getMessage());

            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("TAG",result);
            JSONReturnData = result;
        }
    }
}
