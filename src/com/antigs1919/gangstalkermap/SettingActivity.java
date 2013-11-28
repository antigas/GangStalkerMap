package com.antigs1919.gangstalkermap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity extends FragmentActivity implements OnCheckedChangeListener {
	private final static int ACTIVITY_AUTH = 0;
	private final static int ACTIVITY_PIN = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
		
		((CheckBox) findViewById(R.id.cb_extend_data)).setChecked(
			sp.getBoolean(getResources().getResourceEntryName(R.id.cb_extend_data),false)
		);
		((EditText) findViewById(R.id.et_email)).setText(
				sp.getString(getResources().getResourceEntryName(R.id.et_email),null)
		);
		((EditText) findViewById(R.id.et_url)).setText(
				sp.getString(getResources().getResourceEntryName(R.id.et_url),null)
		);
		set_view_extenddata(
				((CheckBox) findViewById(R.id.cb_extend_data)).isChecked()
		);
		
		
		((CheckBox) findViewById(R.id.cb_twitter)).setChecked(
				sp.getBoolean(getResources().getResourceEntryName(R.id.cb_twitter),false)
			);
		
		((CheckBox) findViewById(R.id.cb_extend_data)).setOnCheckedChangeListener(this);
		((CheckBox) findViewById(R.id.cb_twitter)).setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
		// TODO Auto-generated method stub
		switch(cb.getId()){
			case R.id.cb_extend_data:
				set_view_extenddata(isChecked);
				break;
			case R.id.cb_twitter:

				if(isChecked){
					new AlertDialog.Builder(this)
					.setTitle(getString(R.string.authentication_label))
					.setPositiveButton(
						getString(android.R.string.yes), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								Intent intent = new Intent(SettingActivity.this,PinActivity.class);
								startActivityForResult(intent, 0);
								
							}
						}
					)
					.setNeutralButton(
						getString(android.R.string.no), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								((CheckBox) findViewById(R.id.cb_twitter)).setChecked(false);
								
							}
						}
					)
					.show();
				}
				break;
		}
	}
	
	private void set_view_extenddata(Boolean bool){
		((EditText) findViewById(R.id.et_email)).setEnabled(bool);
		((EditText) findViewById(R.id.et_url)).setEnabled(bool);
		((TextView) findViewById(R.id.tv_email_label)).setEnabled(bool);
		((TextView) findViewById(R.id.tv_url_label)).setEnabled(bool);
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
		Editor ed = sp.edit();
		
		ed.putBoolean(
			getResources().getResourceEntryName(R.id.cb_extend_data)
			,((CheckBox) findViewById(R.id.cb_extend_data)).isChecked()
		);
		ed.putString(
			getResources().getResourceEntryName(R.id.et_email)
			,((TextView) findViewById(R.id.et_email)).getText().toString()
		);
		ed.putString(
			getResources().getResourceEntryName(R.id.et_url)
			,((TextView) findViewById(R.id.et_url)).getText().toString()
		);
		
		ed.putBoolean(
				getResources().getResourceEntryName(R.id.cb_twitter)
				,((CheckBox) findViewById(R.id.cb_twitter)).isChecked()
		);
		ed.commit();
	}
	
	
	 @Override
	 protected void onActivityResult(int requestCode,int resultCode,Intent data) {
		
		if(resultCode != RESULT_OK){
			((CheckBox) findViewById(R.id.cb_twitter)).setChecked(false);
		}else{
			((CheckBox) findViewById(R.id.cb_twitter)).setChecked(true);
		}
	 }
}
