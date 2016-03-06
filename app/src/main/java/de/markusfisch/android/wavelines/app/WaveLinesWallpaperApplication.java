package de.markusfisch.android.wavelines.app;

import de.markusfisch.android.wavelines.database.DataSource;
//import de.markusfisch.android.wavelines.preference.Preferences;
import de.markusfisch.android.wavelines.R;

import android.app.Application;
//import android.support.v7.preference.PreferenceManager;

public class WaveLinesWallpaperApplication extends Application
{
	public static final DataSource dataSource = new DataSource();
	//public static boolean batteryLow = false;

	@Override
	public void onCreate()
	{
		super.onCreate();

		/*PreferenceManager.setDefaultValues(
			this,
			R.xml.preferences,
			false );

		preferences = new Preferences( this );*/
		dataSource.openAsync( this );
	}
}
