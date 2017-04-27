package com.acusportrtg.axismobile.Methods;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.acusportrtg.axismobile.JSON_Classes.FirearmInfo;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockScan;
import com.acusportrtg.axismobile.JSON_Classes.FirearmStockUpdate;
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
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

/**
 * Created by Matt on 4/12/2017.
 */

public class GetJSONStringWithPOSTData {
    private String JSONReturnData = "";
    private String stringAddress;
    private AppCompatActivity activity;
    private ProgressDialog progressDialog;

public interface CallbackReceiver {
    public void receiveData(Object result);
}

    public GetJSONStringWithPOSTData(AppCompatActivity activity) {
        this.activity = activity;
    }

    public String UpdateFirearmScan (FirearmStockUpdate fsu, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/CountFirearm");
            postData.put("InventoryNumber", fsu.getInventoryNumber());
            postData.put("EmployeeID", fsu.getEmployeeID());
            postData.put("MachineName", fsu.getMachineName());
            Log.v("PostData to Send", postData.toString());


            GetJSONDataBack getJSONDataBack = new GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString());


            Log.v("GetJSONWithPostData ",JSONReturnData);
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;
    }

    public String GetFirearmDisposed (FirearmStockScan fss, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/VerifyFirearmNotDisposed");
            postData.put("Log",fss.getLog());
            postData.put("SerialNumber", fss.getSerialNumber());
            postData.put("SerialScanned", fss.isSerialScanned());
            postData.put("LogScanned", fss.isLogScanned());
            GetJSONDataBack getJSONDataBack = new GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString()).wait(200);
            for(long stop = System.nanoTime()+ TimeUnit.SECONDS.toNanos(5);stop>System.nanoTime();) {
                if (!JSONReturnData.equals(""))
                    return JSONReturnData;
            }
            Log.v("GetJSONWithPostData ",JSONReturnData);
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;
    }

    public String GetFirearmInfo (FirearmStockScan fss,Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/GetFirearmInformation" +
                    "");
            postData.put("Log",fss.getLog());
            postData.put("SerialNumber", fss.getSerialNumber());
            postData.put("SerialScanned", fss.isSerialScanned());
            postData.put("LogScanned", fss.isLogScanned());
            GetJSONDataBack getJSONDataBack = new GetJSONDataBack(context) {
                @Override
                public void receiveData(Object object) {
                    JSONReturnData = (String)object;
                }
            };
            getJSONDataBack.execute(reqUrl.toString(), postData.toString()).wait(200);
            for(long stop = System.nanoTime()+ TimeUnit.SECONDS.toNanos(5);stop>System.nanoTime();) {
                if (!JSONReturnData.equals(""))
                    return JSONReturnData;
            }
            Log.v(TAG,JSONReturnData);
            return JSONReturnData;
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage() + "\n" + e.getLocalizedMessage());
        }
        return JSONReturnData;
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

    public String UpdateInventoryCount (SubmitItemCount count, Context context) {
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + ServerAddress.GetSavedServerAddress(context) + ":8899/RestWCFServiceLibrary/InsertProductCountInventory");
            postData.put("ProductUPC", count.getProductUPC());
            postData.put("EmployeeID", count.getEmployeeID());
            postData.put("CountQty", count.getCountQty());
            postData.put("GroupID", count.getGroupID());
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

    public String VerifyProductInGroup (GetInventoryGroupProductID prod, Context context) {
        stringAddress = ServerAddress.GetSavedServerAddress(context);
        JSONObject postData = new JSONObject();
        try {
            URL reqUrl = new URL("http://" + stringAddress + ":8899/RestWCFServiceLibrary/InventoryGroupProductID");
            postData.put("ProductUPC", prod.getProductUPC());
            postData.put("GroupID", prod.getGroupID());
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
    private abstract class GetJSONDataBack extends AsyncTask<String,Void,String> implements CallbackReceiver{
        private Context context;
        ProgressDialog pDialog;

        public abstract void receiveData(Object object);

        public GetJSONDataBack(Context cxt) {
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
            Log.v("TAG","GetJSONStringWithPostData onPostExecute:\n" +result);
            if (result != null) {

                receiveData(result);
            }
            JSONReturnData = result;
            pDialog.dismiss();
        }
    }
}
