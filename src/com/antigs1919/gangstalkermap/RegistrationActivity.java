package com.antigs1919.gangstalkermap;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import twitter4j.GeoLocation;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends FragmentActivity implements OnClickListener {
    static final int REQUEST_CAPTURE_IMAGE = 100;
	private GoogleMap m_gmap;
	private Location m_lastlocation;
	private Bitmap m_picture;
	
	@Override
	protected void onCreate(Bundle saveInstanceState){
		super.onCreate(saveInstanceState);
		setContentView(R.layout.activity_regist);
		m_gmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_regist)).getMap();		
		m_gmap.setMyLocationEnabled(true);
		
		((Button) findViewById(R.id.btn_regist)).setOnClickListener(this);;
		((Button) findViewById(R.id.btn_picture)).setOnClickListener(this);;
		
		SharedPreferences spf = getSharedPreferences("pref",MODE_PRIVATE);
		if(spf.getBoolean(getResources().getResourceEntryName(R.id.cb_extend_data),false)){
			((TextView)findViewById(R.id.et_attach_url)).setText(spf.getString(getResources().getResourceEntryName(R.id.et_url),null));
		}

		Spinner sp = (Spinner) findViewById(R.id.sp_stalktype);
		set_stalktype(sp);
	}
	
	private void set_stalktype(Spinner sp) {
		// TODO Auto-generated method stub
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ParseQuery<ParseObject> pq = ParseQuery.getQuery(getString(R.string.parse_class_stalk_type));
		pq.whereContains(getString(R.string.parse_column_country),getString(R.string.language));
		pq.orderByAscending(getString(R.string.parse_column_type_number));
		
		try {
			List<ParseObject> list = pq.find();
			for(ParseObject po:list){
				adapter.add((String) po.get(getString(R.string.parse_column_type)));
			}
			sp.setAdapter(adapter);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
		m_gmap.setOnMyLocationChangeListener(
			new OnMyLocationChangeListener(){
			
				@Override
				public void onMyLocationChange(Location location) {
					CameraUpdate cu;
					LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
					cu = CameraUpdateFactory.newLatLngZoom(latLng,15);
					m_gmap.animateCamera(cu);
					((TextView) findViewById(R.id.tv_latitude_value)).setText(Double.toString(location.getLatitude()) );
					((TextView) findViewById(R.id.tv_longitude_value)).setText(Double.toString(location.getLongitude()) );
					m_lastlocation = location;
				}
			}
		);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btn_regist:
				if(m_lastlocation == null){
					Toast.makeText(this,getString(R.string.no_location_label), Toast.LENGTH_LONG).show();
				}else{
					ParseObject po = new ParseObject(getString(R.string.parse_class_locationinfo));
					po.put(getString(R.string.parse_column_date),new Date());
					po.put(getString(R.string.parse_column_location),new ParseGeoPoint(m_lastlocation.getLatitude(),m_lastlocation.getLongitude()));
					po.put(getString(R.string.parse_column_comment),((EditText) findViewById(R.id.et_comment)).getText().toString());
					po.put(getString(R.string.parse_column_type),(String)((Spinner) findViewById(R.id.sp_stalktype)).getSelectedItem());
					po.put(getString(R.string.parse_column_url),((EditText) findViewById(R.id.et_attach_url)).getText().toString());
					put_extend_data(po);
					tweet_comment();
					if (m_picture != null){
						po.put(getString(R.string.parse_column_picture),get_picture());
					}
					try {
						po.save();
						Toast.makeText(this,getString(R.string.regist_complete_label), Toast.LENGTH_LONG).show();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					finish();
				}
				break;
			case R.id.btn_picture:
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
			break;
		}
		
	}
	
	private ParseFile get_picture() {
			String file_name = new SimpleDateFormat(getString(R.string.file_date_format)).format(new Date()) + getString(R.string.file_extention);

			ByteArrayOutputStream baops = new ByteArrayOutputStream();
			m_picture.compress(Bitmap.CompressFormat.JPEG, 100, baops);
			ParseFile image_file = new ParseFile(file_name,baops.toByteArray());
			try {
				image_file.save();
				return image_file;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_CAPTURE_IMAGE == requestCode && resultCode == Activity.RESULT_OK) {
			m_picture = (Bitmap) data.getExtras().get("data");
			((TextView) findViewById(R.id.tv_picture_value)).setText(getString(R.string.picture_yes_label));
		}
	}
	
	private void put_extend_data(ParseObject po){
		SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
		
		if(sp.getBoolean(getResources().getResourceEntryName(R.id.cb_extend_data),false)){
			po.put(getString(R.string.parse_column_email),sp.getString(getResources().getResourceEntryName(R.id.et_email),null));
		}
	}
	private void tweet_comment(){
		AsyncTask task = new AsyncTask(){
			@Override
			protected Object doInBackground(Object... params) {
				boolean ret_value = false;
				
				SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
				if(sp.getBoolean(getResources().getResourceEntryName(R.id.cb_twitter),false)){
					StatusUpdate status = new StatusUpdate(
							"Tweet By " + getString(R.string.app_name) + "\n"
							+ (String)((Spinner) findViewById(R.id.sp_stalktype)).getSelectedItem() + "\n"
							+ ((EditText) findViewById(R.id.et_comment)).getText().toString()
					);
					status.setLocation(new GeoLocation(m_lastlocation.getLatitude(),m_lastlocation.getLongitude()));
					
					Twitter tw = new TwitterFactory().getInstance();
					AccessToken token = new AccessToken(
							sp.getString("access_token",null )
							,sp.getString("access_token_secret",null));
					tw.setOAuthConsumer("Consumer key","Consumer key secret");
					tw.setOAuthAccessToken(token);
					
					try {
						tw.updateStatus(status);
						ret_value = true;
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				}
				return ret_value;
			}
			@Override
			protected void onPostExecute(Object object) {
				SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
				if(sp.getBoolean(getResources().getResourceEntryName(R.id.cb_twitter),false)){
					if((Boolean)object){
						Toast.makeText(RegistrationActivity.this,  getString(R.string.tweet_complete_label), Toast.LENGTH_LONG).show();
					}else{
						Toast.makeText(RegistrationActivity.this, getString(R.string.tweet_err_label), Toast.LENGTH_LONG).show();
					}
					
				}
			}
		};
		task.execute();
	}
}
