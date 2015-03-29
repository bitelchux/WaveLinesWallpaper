package de.markusfisch.android.wavelines.activity;

import de.markusfisch.android.wavelines.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity
{
	public static final String SHARED_PREFERENCES_NAME =
		"de.markusfisch.android.wavelines.activity.SettingsActivity";

	@Override
	public void onCreate( Bundle state )
	{
		super.onCreate( state );

		getPreferenceManager().setSharedPreferencesName(
			SHARED_PREFERENCES_NAME );

		addPreferencesFromResource( R.xml.settings );
	}
}
