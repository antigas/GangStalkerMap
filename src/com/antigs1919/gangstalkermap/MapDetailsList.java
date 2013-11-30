package com.antigs1919.gangstalkermap;


public final class MapDetailsList {
	/**
	 * not instantiate
	 */
	private MapDetailsList(){}
	
	public static final MapDetails[] MAPS = {
		new MapDetails(R.string.view_map_label,
				R.string.view_map_description,
				MapActivity.class),
		new MapDetails(R.string.regist_location_label,
				R.string.regist_location_description,
				RegistrationActivity.class),
		new MapDetails(R.string.region_level_label,
				R.string.region_level_description,
				RegionLevelActivity.class),
		new MapDetails(R.string.setting_label,
				R.string.setting_description,
				SettingActivity.class),
	};
}
