package de.markusfisch.android.wavelines.app;

import de.markusfisch.android.wavelines.database.DataSource;
//import de.markusfisch.android.wavelines.preference.Preferences;
import de.markusfisch.android.wavelines.R;

import android.app.Application;
import android.database.SQLException;
import android.os.AsyncTask;
//import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

public class WaveLinesWallpaperApplication extends Application
{
	public static DataSource dataSource;
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
		dataSource = new DataSource( this );

		openDataSourceAsync();
	}

	private void openDataSourceAsync()
	{
		new AsyncTask<Void, Void, Boolean>()
		{
			@Override
			protected Boolean doInBackground( Void... nothings )
			{
				try
				{
					return dataSource.open();
				}
				catch( SQLException e )
				{
					return false;
				}
			}

			@Override
			protected void onPostExecute( Boolean success )
			{
				if( success )
					return;

				Toast.makeText(
					WaveLinesWallpaperApplication.this,
					R.string.error_database,
					Toast.LENGTH_LONG ).show();
			}
		}.execute();
	}
}
