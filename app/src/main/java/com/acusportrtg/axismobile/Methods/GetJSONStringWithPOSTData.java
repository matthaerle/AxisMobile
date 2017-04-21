package com.acusportrtg.axismobile.Methods;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.acusportrtg.axismobile.JSON_Classes.GetInventoryGroupProductID;
import com.acusportrtg.axismobile.JSON_Classes.SearchByUPC;
import com.acusportrtg.axismobile.JSON_Classes.SubmitItemCount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;

import static android.content.ContentValues.TAG;

/**
 * Created by Matt on 4/12/2017.
 */

public class GetJSONStringWithPOSTData {
    private String JSONReturnData = "";
    private String stringAddress;

    public String GetProductInfoJsonString (SearchByUPC upc, Context context) {
        stringAddress = ServerAddress.GetSavedServerAddress(context);
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + stringAddress + ":8899/RestWCFServiceLibrary/GetProductsByUPC");
            postData.put("ProductUPC", upc.getProductUPC());
            JSONReturnData = new GetJSONDataBack().execute(reqUrl.toString(), postData.toString()).get();
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;
    }

    public String UpdateInventoryCount (SubmitItemCount count, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/InsertProductCountInventory");
            postData.put("ProductUPC", count.getProductUPC());
            postData.put("EmployeeID", count.getEmployeeID());
            postData.put("CountQty", count.getCountQty());
            postData.put("GroupID", count.getGroupID());
            JSONReturnData = new GetJSONDataBack().execute(reqUrl.toString(), postData.toString()).get();
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;
    }

    public String VerifyProductInGroup (GetInventoryGroupProductID prod, Context context) {
        stringAddress = ServerAddress.GetSavedServerAddress(context);
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + stringAddress + ":8899/RestWCFServiceLibrary/InventoryGroupProductID");
            postData.put("ProductUPC", prod.getProductUPC());
            postData.put("GroupID", prod.getGroupID());
            JSONReturnData = new GetJSONDataBack().execute(reqUrl.toString(), postData.toString()).get();
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;

    }

    private String convertStreamToString(InputStream is) {
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
    private class GetJSONDataBack extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... params) {
            Log.v(TAG, params[1]);
            String data = "";
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader;
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
            JSONReturnData = data;
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.v("TAG",result);
            JSONReturnData = result;
        }
    }
}
