package com.acusportrtg.axismobile.Methods;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mhaerle on 4/7/2017.
 */

public class SharedPrefs {
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static String GetSavedServerAddress(Context context) {
        return getPrefs(context).getString("Server_Address", "");
    }
    public static void SetSavedServerAddress(String serverAddress, Context context) {
        android.content.SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString("Server_Address", serverAddress);
        editor.commit();
    }
    private static android.content.SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }


}
