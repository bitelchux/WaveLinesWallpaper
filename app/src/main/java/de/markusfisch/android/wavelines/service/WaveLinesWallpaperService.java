package de.markusfisch.android.wavelines.service;

import de.markusfisch.android.wavelines.database.Theme;
import de.markusfisch.android.wavelines.graphics.WaveLinesRenderer;
import de.markusfisch.android.wavelines.R;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;

public class WaveLinesWallpaperService extends CanvasWallpaperService {
	@Override
	public Engine onCreateEngine() {
		return new WaveLinesEngine();
	}

	private class WaveLinesEngine
			extends CanvasWallpaperEngine
			//implements SharedPreferences.OnSharedPreferenceChangeListener
	{
		private final WaveLinesRenderer renderer = new WaveLinesRenderer();

		public WaveLinesEngine() {
			super();

			PreferenceManager.setDefaultValues(
					WaveLinesWallpaperService.this,
					R.xml.preferences,
					false);

			/*SharedPreferences preferences =
				WaveLinesWallpaperService.this.getSharedPreferences(
					//SettingsActivity.SHARED_PREFERENCES_NAME,
					"de.markusfisch.android.wavelines",
					0 );

			preferences.registerOnSharedPreferenceChangeListener(
				this );

			onSharedPreferenceChanged( preferences, null );
		}

		@Override
		public void onSharedPreferenceChanged(
			SharedPreferences preferences,
			String key )
		{
			delay = Integer.parseInt(
				preferences.getString( "delay", "100" ) );

			Theme theme = null;
			File file = new File(
				getApplicationContext().getDir(),
				preferences.getString( "theme" ) );

			if( file.exists() )
				theme = Theme.restore( file );

			renderer.reset( theme != null ?
				theme :
				new Theme() );*/

			delay = 100;
			renderer.init(new Theme());
		}

		@Override
		public void onSurfaceChanged(
				SurfaceHolder holder,
				int format,
				int width,
				int height) {
			super.onSurfaceChanged(
					holder,
					format,
					width,
					height);

			renderer.setup(width, height);
		}

		@Override
		protected void drawFrame(Canvas canvas, long dt) {
			canvas.save();
			renderer.draw(canvas, dt);
			canvas.restore();
		}
	}
}
