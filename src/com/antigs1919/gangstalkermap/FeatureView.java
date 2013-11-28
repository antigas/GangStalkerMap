package com.antigs1919.gangstalkermap;

import android.content.Context;
import android.widget.FrameLayout;
import android.view.LayoutInflater;
import android.widget.TextView;;

public final class FeatureView extends FrameLayout {

	public FeatureView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater layoutinflayter =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutinflayter.inflate(R.layout.feature, this);
	}
	
	public synchronized void setTitleId(int titleId){
		((TextView) (findViewById(R.id.title))).setText(titleId);
	}
	
	public synchronized void setDescriptionId(int descriptionId){
		((TextView) (findViewById(R.id.description))).setText(descriptionId);
	}
}
