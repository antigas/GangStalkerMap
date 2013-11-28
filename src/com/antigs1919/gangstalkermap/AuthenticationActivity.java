package com.antigs1919.gangstalkermap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class AuthenticationActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		Intent intent = getIntent();
		
		WebView  webView = (WebView)findViewById(R.id.wv_authentication);
		webView.loadUrl(intent.getStringExtra("URL"));
	}
}
