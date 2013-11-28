package com.antigs1919.gangstalkermap;

import android.support.v4.app.FragmentActivity;

public final class MapDetails {
	/**
	 * resource of title
	 */
	public final int titleId;
	/**
	 * resource of description
	 */
	public final int descriptionId;
	/**
	 * activity's
	 */
	public final Class<? extends FragmentActivity>activity_class;
	/**
	 * constructor
	 */
	public MapDetails(int titleId,int descriptionId,Class<? extends FragmentActivity> activity_class){
		this.titleId = titleId;
		this.descriptionId = descriptionId;
		this.activity_class = activity_class;
	}
}
