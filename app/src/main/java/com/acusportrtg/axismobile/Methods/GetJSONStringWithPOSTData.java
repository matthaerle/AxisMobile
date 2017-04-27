package com.acusportrtg.axismobile.Methods;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by Matt on 4/12/2017.
 */

public class GetJSONStringWithPOSTData {
    private static String JSONReturnData = "";
    private String stringAddress;
    private AppCompatActivity activity;

public interface CallbackReceiver {
    public void receiveData(Object result);
}

    public GetJSONStringWithPOSTData(AppCompatActivity activity) {
        this.activity = activity;
    }



    public String GetProductInfoJsonString (SearchByUPC upc, Context context) {
        stringAddress = ServerAddress.GetSavedServerAddress(context);
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + stringAddress + ":8899/RestWCFServiceLibrary/GetProductsByUPC");
            postData.put("ProductUPC", upc.getProductUPC());
            GetJSONDataBack getJSONDataBack = new GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;
    }


    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException: " + e.getMessage());
            }
        }
        return sb.toString();
    }
    public abstract static class GetJSONDataBack extends AsyncTask<String,Void,String> implements CallbackReceiver{
        private Context context;
        ProgressDialog pDialog;

        public abstract void receiveData(Object object);

        protected GetJSONDataBack(Context cxt) {
            context = cxt;
            pDialog = new ProgressDialog(context);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, "GetJSONStringWithPostData doInbackround:\n"+params[1]);
            String data = "";
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                Writer writer = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8"));
                writer.write(params[1]);
                writer.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                data = convertStreamToString(inputStream);
                return data;
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
            Log.v("TAG","GetJSONStringWithPostData onPostExecute:\n" +result);
            if (result != null)
                receiveData(result);

            JSONReturnData = result;
            pDialog.dismiss();
        }
    }
}
