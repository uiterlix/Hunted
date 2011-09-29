package nl.uiterlinden.android.hunted.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import nl.uiterlinden.android.hunted.Preferences;
import nl.uiterlinden.android.hunted.R;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Client {
	
	public static final String FETCH_CLUES_PATH = "/rest/fetchClues";
	public static final String MESSAGE_PATH = "/rest/message";
	
	private static final String LOG_TAG = "Client";
	private final Context context;
	
	public Client(Context context) {
		this.context = context;
	}
	
	public String retrieve(String path, Properties params) throws HuntedIOException {
		// read settings from preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String username = preferences.getString(Preferences.PREF_TEAMNUMBER, context.getString(R.string.defaultTeamNummer));
        String password = preferences.getString(Preferences.PREF_TEAMPASSWORD, context.getString(R.string.defaultTeamPassword));
        String hostName = preferences.getString(Preferences.PREF_HUNTEDHQHOST, context.getString(R.string.defaultHqHost));
        int port = Integer.parseInt(preferences.getString(Preferences.PREF_HUNTEDHQPORT, context.getString(R.string.defaultHqPort)));
		
		Log.d(LOG_TAG, "Retrieve " + path);
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = 10000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 10000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpHost host = new HttpHost(hostName, port);
		// append params to path
		StringBuilder pathBuilder = new StringBuilder(path);
		int paramPos = 0;
			if (params != null) {
			for (Object keyObject : params.keySet()) {
				String key = (String) keyObject;
				String value = params.getProperty(key);
				// URL Encode the value
				String encodedValue = URLEncoder.encode(value);
				// append parameter to get request
				if (paramPos == 0) {
					pathBuilder.append("?");
				} else {
					pathBuilder.append("&");
				}
				pathBuilder.append(key);
				pathBuilder.append("=");
				pathBuilder.append(encodedValue);
				paramPos ++;
			}
		}
		HttpGet get = new HttpGet(pathBuilder.toString());
		
		try {
			HttpResponse response = httpClient.execute(host, get);
			String content = getResponseContent(response);
			Log.d(LOG_TAG, "Result: " + content);
			// login
			Log.d(LOG_TAG, "Login");
			HttpPost post = new HttpPost("/huntedhq/j_spring_security_check");
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("j_username", username));
			formparams.add(new BasicNameValuePair("j_password", password));
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			post.setEntity(entity);
			
			response = httpClient.execute(host, post);
			// Expect a 302
			int statusCode = response.getStatusLine().getStatusCode();
			Log.d(LOG_TAG, "Status: " + statusCode);
			content = getResponseContent(response);
			Log.d(LOG_TAG, "Result: " + content);
			return content;
		} catch (ClientProtocolException e) {
			throw new HuntedIOException("Probleem met de verbinding.", e);
		} catch (IOException e) {
			throw new HuntedIOException("Probleem met de verbinding.", e);
		}
	}

	public static String getResponseContent(HttpResponse response) {
		StringBuilder resultBuilder = new StringBuilder();
		try {
			InputStream in = response.getEntity().getContent();
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line = read.readLine()) != null) {
				resultBuilder.append(line);
			}
 		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultBuilder.toString();
	}
}
