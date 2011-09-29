package nl.uiterlinden.android.hunted;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {
	
	public static final String PREF_PHONENUMBER = "telefoonNummer";
	public static final String PREF_TEAMPASSWORD = "teamPassword";
	public static final String PREF_HQPHONENUMBER = "headquartersSms";
	public static final String PREF_TEAMNUMBER = "teamNummer";
	public static final String PREF_HUNTEDHQHOST = "hqHost";
	public static final String PREF_HUNTEDHQPORT = "hqPort";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.layout.preferences);
    }
}