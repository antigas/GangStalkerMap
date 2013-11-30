package com.antigs1919.gangstalkermap;


import com.parse.CountCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class RegionLevelActivity extends FragmentActivity implements LocationListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_region);
		
		LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = mLocationManager.getBestProvider(criteria, true);
		mLocationManager.requestLocationUpdates(provider, 0, 0, this);
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		ParseQuery<ParseObject> pq = ParseQuery.getQuery(getString(R.string.parse_class_locationinfo));
		pq.whereWithinKilometers(getString(R.string.parse_column_location)
				, new ParseGeoPoint(location.getLatitude(), location.getLongitude())
				, 15.0);
		
		pq.countInBackground(new CountCallback(){
			
			@Override
			public void done(int count, ParseException e) {
				// TODO Auto-generated method stub
				if(e==null){
					((TextView)findViewById(R.id.tv_region_lebel_value)).setText(String.valueOf(count));
				}else{
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
