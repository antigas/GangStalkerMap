package com.antigs1919.gangstalkermap;


import com.parse.Parse;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	private static class CustomArrayAdapter extends ArrayAdapter<MapDetails>{

		public CustomArrayAdapter(Context context, MapDetails[] maps) {
			super(context, R.layout.feature,R.id.title, maps);
		}
		
		@Override
		public View getView(int position,View convView,ViewGroup parent){
			FeatureView featureview;
			if(convView instanceof FeatureView){
				featureview = (FeatureView) convView;
			}else{
				featureview = new FeatureView(getContext());
			}
			MapDetails map = getItem(position);
			
			featureview.setTitleId(map.titleId);
			featureview.setDescriptionId(map.descriptionId);
			
			return featureview;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ListAdapter adapter = new CustomArrayAdapter(this,MapDetailsList.MAPS);
		setListAdapter(adapter);
		
		Parse.initialize(this, "applicationId", "clientKey");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onListItemClick(ListView listview,View view ,int position,long id){
		MapDetails map_details = (MapDetails) getListAdapter().getItem(position);
		startActivity(new Intent(this,map_details.activity_class));
	}
}
