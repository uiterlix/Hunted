package nl.uiterlinden.android.hunted.io;

import nl.uiterlinden.android.hunted.Preferences;
import nl.uiterlinden.android.hunted.data.DataProvider;
import nl.uiterlinden.android.hunted.domain.LogItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
	
	public static final String SMS_RECEIVED = "nl.uiterlinden.android.hunted.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// handle incoming sms
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String hqPhoneNumber = normalizeNumber(preferences.getString(Preferences.PREF_HQPHONENUMBER, null));
		
		Bundle bundle = intent.getExtras();
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
			String originator = normalizeNumber(smsMessage[n].getOriginatingAddress());
			if (hqPhoneNumber.equals(originator)) {
				Toast toast = Toast.makeText(context,
						"Hunted SMS ontvangen: " + smsMessage[n].getMessageBody(), Toast.LENGTH_LONG);
						toast.show();
				// create new log item and store in the database
				LogItem logItem = new LogItem(LogItem.Direction.IN, smsMessage[n].getMessageBody());
				DataProvider provider = new DataProvider(context);
				provider.storeLogItem(logItem);
				String catchCountString = provider.getStatus(DataProvider.CATCH_COUNT);
				if (smsMessage[n].getMessageBody().contains("zojuist gepakt")) {
					// update latest catch
					provider.updateStatus(DataProvider.LAST_CATCH_TIME, "" + System.currentTimeMillis());
					int catchCount = 0;
					if (catchCountString != null) {
						catchCount = Integer.parseInt(catchCountString);
					} 
					catchCount ++;
					provider.updateStatus(DataProvider.CATCH_COUNT, "" + catchCount);
				}
				provider.close();
				Intent i = new Intent();
		        i.setAction(SMS_RECEIVED);
		        context.sendBroadcast(i);
			}
		}
	}
	
	private String normalizeNumber(String number) {
		String result = null;
		// 0626868669
		// 0031626868669
		// +31626868669
		if (number.startsWith("00") && number.length() == 13) {
			return number;
		}
		if (number.startsWith("+") && number.length() == 12) {
			return "00" + number.substring(1);
		}
		if (number.startsWith("0") && number.length() == 10) {
			return "0031" + number.substring(1);
		}
		
		return number;
	}
	
}
