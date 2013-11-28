package com.antigs1919.gangstalkermap;

import java.text.SimpleDateFormat;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MapActivity extends FragmentActivity implements OnMarkerClickListener{
	private GoogleMap m_gmap;
	private Location m_last_disp_location,m_temp_location;
	private List<ParseObject> m_polist;
	private boolean m_picture_finish;
	
	private class CustomInfoWindowAdapter implements InfoWindowAdapter,OnInfoWindowClickListener{
		private View m_infowindow_view;
		private String m_date_str,m_type,m_email,m_url,m_comment;
		private Bitmap m_bitmap;
		private Marker m_marker;
		private String m_image_url;
		
		CustomInfoWindowAdapter(){
			m_infowindow_view = getLayoutInflater().inflate(R.layout.info_window, null);
			m_gmap.setOnInfoWindowClickListener(this);
		}
		@Override
		public View getInfoContents(Marker marker) {
			if(m_picture_finish == true){
				return m_infowindow_view;
			}
			for(ParseObject po:m_polist){
				
				if(po.getObjectId().equals(marker.getTitle())){
					m_date_str = new SimpleDateFormat(getString(R.string.label_date_format)).format(po.getCreatedAt());
					m_type = (String)po.get(getString(R.string.parse_column_type));
					m_email = (String)po.get(getString(R.string.parse_column_email));
					m_url = (String)po.get(getString(R.string.parse_column_url));
					m_comment = (String)po.get(getString(R.string.parse_column_comment));
					ParseFile image_file = (ParseFile)po.get(getString(R.string.parse_column_picture));
					if(image_file != null){
						m_image_url = image_file.getUrl();
						m_marker = marker;
						image_file.getDataInBackground(
								new GetDataCallback(){
								@Override
								public void done(byte[] data, ParseException e) {
									m_bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
									((ImageView)(m_infowindow_view.findViewById(R.id.iv_picture))).setImageBitmap(m_bitmap);
									m_picture_finish = true;
									m_marker.showInfoWindow();
									
								}
							}
						);
					}
					break;
				}
			}
			
			((TextView)(m_infowindow_view.findViewById(R.id.tv_title))).setText(
					m_date_str + ":" + m_type
				);
			((TextView)(m_infowindow_view.findViewById(R.id.tv_snippet))).setText(
					m_comment + "\n" + m_email
			);
			return m_infowindow_view;
		}
		
		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}
		
		@Override
		public void onInfoWindowClick(Marker marker) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(MapActivity.this,MarkerDetailActivity.class);
			intent.putExtra("Type", m_type);
			intent.putExtra("Comment", m_comment);
			intent.putExtra("Email", m_email);
			intent.putExtra("URL", m_url);
			intent.putExtra("Latitude", marker.getPosition().latitude);
			intent.putExtra("Longitude", marker.getPosition().longitude);
			intent.putExtra("DateTime", m_date_str);
			intent.putExtra("DateTime", m_date_str);
			intent.putExtra("PictureURL",m_image_url);
			startActivity(intent);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle saveInstanceState){
		super.onCreate(saveInstanceState);
		setContentView(R.layout.activity_map);
		m_gmap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();		
		m_gmap.setMyLocationEnabled(true);
		m_gmap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		m_gmap.setOnMarkerClickListener(this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		m_gmap.setOnMyLocationChangeListener(
			new OnMyLocationChangeListener(){
				
				@Override
				public void onMyLocationChange(Location location) {
					if(m_last_disp_location == null ){
						LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
						CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(latlng,15);
						m_gmap.animateCamera(cu);
						
						set_locationinfo(location);
					}else if(location.distanceTo(m_last_disp_location) > 1000){
						m_gmap.clear();
						set_locationinfo(location);
					}
				}
				
			}
		);
	}
	
	void set_locationinfo(Location location){
		m_temp_location = location;
		ParseQuery<ParseObject> pq = ParseQuery.getQuery(getString(R.string.parse_class_locationinfo));
		pq.whereWithinKilometers(getString(R.string.parse_column_location)
				, new ParseGeoPoint(location.getLatitude(), location.getLongitude())
				, 2.0);
		
		pq.findInBackground(
				new FindCallback<ParseObject>(){
					@Override
					public void done(List<ParseObject> list, ParseException e) {
						
						if(e==null){
							m_polist = list;
							
							for(ParseObject po:list){
								MarkerOptions mo = new MarkerOptions();
								ParseGeoPoint pgp = (ParseGeoPoint)po.get(getString(R.string.parse_column_location));
								mo.position(new LatLng(pgp.getLatitude(),pgp.getLongitude()));
								mo.title(po.getObjectId());
								m_gmap.addMarker(mo);
							}
							m_last_disp_location = m_temp_location;
							
						}else{
							e.printStackTrace();
						}
					}
				}
		);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		m_picture_finish = false;
		return false;
	}

}
