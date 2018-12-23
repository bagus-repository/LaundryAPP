package laundry.aslijempolcustomer.activity;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Bagus on 24/01/2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared Preferences file name
    private static final String PREF_NAME = "welcome-slider";

    private  static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private String M_ID;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch(){
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
