package de.markusfisch.android.wavelines.service;

import de.markusfisch.android.wallpaper.service.WallpaperService;

import de.markusfisch.android.wavelines.activity.CompositorActivity;
import de.markusfisch.android.wavelines.activity.SettingsActivity;
import de.markusfisch.android.wavelines.graphics.WaveLinesRenderer;
import de.markusfisch.android.wavelines.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;

public class WaveLinesWallpaperService extends WallpaperService
{
	@Override
	public Engine onCreateEngine()
	{
		return new WaveLinesEngine();
	}

	private class WaveLinesEngine
		extends WallpaperEngine
		implements SharedPreferences.OnSharedPreferenceChangeListener
	{
		private final WaveLinesRenderer renderer = new WaveLinesRenderer();

		public WaveLinesEngine()
		{
			super();

			PreferenceManager.setDefaultValues(
				WaveLinesWallpaperService.this,
				R.xml.settings,
				false );

			SharedPreferences preferences =
				WaveLinesWallpaperService.this.getSharedPreferences(
					SettingsActivity.SHARED_PREFERENCES_NAME, 0 );

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

			renderer.uniform =
				preferences.getBoolean( "uniform", false );
			renderer.coupled =
				preferences.getBoolean( "coupled", true );
			renderer.uniform =
				preferences.getBoolean( "uniform", false );
			renderer.lines = Integer.parseInt(
				preferences.getString( "lines", "24" ) );
			renderer.waves = Integer.parseInt(
				preferences.getString( "waves", "3" ) );
			renderer.relativeAmplitude = Float.parseFloat(
				preferences.getString( "amplitude", ".02" ) );
			renderer.colors =
				WaveLinesWallpaperService.getThemeColors(
					getApplicationContext(),
					preferences );

			renderer.reset();
		}

		@Override
		public void onSurfaceChanged(
			SurfaceHolder holder,
			int format,
			int width,
			int height )
		{
			super.onSurfaceChanged( holder, format, width, height );

			renderer.setup( width, height );
		}

		@Override
		protected void drawFrame( Canvas canvas, long e )
		{
			canvas.save();
			renderer.draw( canvas, e );
			canvas.restore();
		}
	}

	public static int[] getThemeColors(
		Context context,
		SharedPreferences preferences )
	{
		String theme = preferences.getString( "theme", "blue" );

		if( theme.equals( "custom" ) )
			return CompositorActivity.getCustomColors( preferences );

		int themeId = context.getResources().getIdentifier(
			theme+"_colors",
			"array",
			context.getPackageName() );

		if( themeId < 1 )
			return null;

		TypedArray a = context.getResources().obtainTypedArray(
			themeId );
		int colors[] = new int[a.length()];

		for( int n = 0, l = a.length();
			n < l;
			++n )
			colors[n] = a.getColor( n, 0 );

		a.recycle();

		return colors;
	}
}
