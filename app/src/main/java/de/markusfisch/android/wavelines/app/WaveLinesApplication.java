package de.markusfisch.android.wavelines.app;

import de.markusfisch.android.wavelines.database.DataSource;

import android.app.Application;

public class WaveLinesApplication extends Application {
	public static final DataSource dataSource = new DataSource();

	@Override
	public void onCreate() {
		super.onCreate();
		dataSource.openAsync(this);
	}
}
