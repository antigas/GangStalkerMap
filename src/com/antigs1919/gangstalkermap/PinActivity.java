package com.antigs1919.gangstalkermap;

import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationContext;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PinActivity extends Activity implements OnClickListener {
	OAuthAuthorization m_Oauth;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin);
		((Button)findViewById(R.id.btn_get_token)).setOnClickListener(this);
		
		open_authorization_url();
	}
	
	private void open_authorization_url(){
		AsyncTask task = new AsyncTask(){

			@Override
			protected Object doInBackground(Object... params) {
				// TODO Auto-generated method stub
				RequestToken sRequestToken;
				m_Oauth = new OAuthAuthorization(ConfigurationContext.getInstance());
				m_Oauth.setOAuthConsumer("consumerKey","consumerSecret");

				try {
					sRequestToken = m_Oauth.getOAuthRequestToken();
				} catch (TwitterException e) {
				    e.printStackTrace();
				    return null;
				}
				String url = sRequestToken.getAuthorizationURL();
				Intent intent = new Intent(PinActivity.this,AuthenticationActivity.class);
				intent.putExtra("URL", url);
				startActivityForResult(intent, 0);
				return null;
			}
			@Override
			protected void onPostExecute(Object object) {
				//Toast.makeText(SettingActivity.this,token +"\n"+ secret, Toast.LENGTH_LONG).show();
			}
		};
		task.execute();
	}

	
	@Override
	public void onClick(View v) {
		AsyncTask task = new AsyncTask(){
			@Override
			protected Boolean doInBackground(Object... params) {
				boolean ret_value = false;
				String pin;
				try {
					pin =((EditText)findViewById(R.id.et_pin)).getText().toString();
					AccessToken sAccessToken = m_Oauth.getOAuthAccessToken(pin);
					
					SharedPreferences sp = getSharedPreferences("pref", MODE_PRIVATE);
					Editor edit = sp.edit();
					edit.putString("access_token", sAccessToken.getToken());
					edit.putString("access_token_secret",sAccessToken.getTokenSecret());
					edit.commit();
					ret_value = true;
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				return ret_value;
			}
			@Override
			protected void onPostExecute(Object object) {
				if((Boolean)object){
					setResult(RESULT_OK);
					Toast.makeText(PinActivity.this,  getString(R.string.authentication_complete_label), Toast.LENGTH_LONG).show();
					finish();
				}else{
					Toast.makeText(PinActivity.this, getString(R.string.authentication_err_label), Toast.LENGTH_LONG).show();
				}
			}
		};
		task.execute();
	}
}
