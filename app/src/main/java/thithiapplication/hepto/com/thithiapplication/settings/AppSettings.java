package thithiapplication.hepto.com.thithiapplication.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Balamurugan_G on 6/23/2016.
 */
public class AppSettings {
    public static final String PREF_NAME = "THITHIAPP";
    public static final String DB_STATUS = "DB_STATUS";

    public static boolean getDatabaseStatus(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);

        return pref.getBoolean(DB_STATUS, false);
    }

    public static void setDatabaseStatus(Context context,boolean status){
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(DB_STATUS, status);
        editor.commit();
    }


}
