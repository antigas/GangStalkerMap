package com.antigs1919.gangstalkermap;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MarkerDetailActivity extends Activity {
	@Override
	protected void onCreate(Bundle saveInstanceState){
		super.onCreate(saveInstanceState);
		setContentView(R.layout.activity_marker_detail);
		Intent intent = getIntent();
		
		set_image();
		((TextView) findViewById(R.id.tv_detail_type)).setText(String.valueOf(intent.getStringExtra("Type")));
		((TextView) findViewById(R.id.tv_detail_comment)).setText(String.valueOf(intent.getStringExtra("Comment")));
		((EditText) findViewById(R.id.et_detail_email_value)).setText(intent.getStringExtra("Email"));
		((EditText) findViewById(R.id.et_detail_url_value)).setText(intent.getStringExtra("URL"));
		((TextView) findViewById(R.id.tv_detail_latitude_value)).setText(String.valueOf(intent.getDoubleExtra("Latitude", 0)));
		((TextView) findViewById(R.id.tv_detail_longitude_value)).setText(String.valueOf(intent.getDoubleExtra("Longitude",0)));
		((TextView) findViewById(R.id.tv_detail_datetime_value)).setText(intent.getStringExtra("DateTime"));
		((TextView) findViewById(R.id.tv_detail_address_label_value)).setText(
			get_location_name(intent.getDoubleExtra("Latitude", 0), intent.getDoubleExtra("Longitude",0))
		);
	}
	
	
	private void set_image() {
		AsyncTask task = new AsyncTask(){

			@Override
			protected Object doInBackground(Object... params) {
				try {
					Intent intent = getIntent();
					URL url = new URL(intent.getStringExtra("PictureURL"));
					InputStream is = url.openStream();
					Drawable drawable = Drawable.createFromStream(is, "web img");
					is.close();
					return drawable;

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			@Override  
			protected void onPostExecute(Object result){
				((ImageView) findViewById(R.id.iv_detail_picture)).setImageDrawable((Drawable)result);
			}
		};
		task.execute();
	}


	private String get_location_name(double latitude,double longitude){
		Geocoder geocoder = new Geocoder(this,Locale.getDefault());
		StringBuffer strbuf = new StringBuffer();
		try{
			List<Address> list_address = geocoder.getFromLocation(latitude,longitude,5);
			for(Address address:list_address){
				int max_i = address.getMaxAddressLineIndex();
				for(int i=0;i <= max_i;i++){
					strbuf.append(address.getAddressLine(i));
				}
			}
		}catch (IOException e) {
			e.printStackTrace();
		}

		return strbuf.toString();
	}
}
