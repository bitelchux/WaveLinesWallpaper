package de.markusfisch.android.wavelines.app;

import de.markusfisch.android.wavelines.database.DataSource;
import de.markusfisch.android.wavelines.preference.Preferences;

import android.app.Application;

public class WaveLinesApp extends Application {
	public static final Preferences preferences = new Preferences();
	public static final DataSource dataSource = new DataSource();

	@Override
	public void onCreate() {
		super.onCreate();
		preferences.init(this);
		dataSource.openAsync(this);
	}
}
