package de.markusfisch.android.wavelines.activity;

import de.markusfisch.android.colorcompositor.ColorCompositor;
import de.markusfisch.android.wavelines.service.WaveLinesWallpaperService;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class CompositorActivity extends ColorCompositor
{
	private SharedPreferences preferences = null;

	static public int[] getCustomColors( SharedPreferences preference )
	{
		final int l = preference.getInt( "custom_colors", 0 );

		if( l < 1 )
			return null;

		final int colors[] = new int[l];

		for( int n = 0; n < l; ++n )
			colors[n] = preference.getInt( "custom_color"+n, 0 );

		return colors;
	}

	@Override
	public void onCreate( Bundle state )
	{
		super.onCreate( state );

		if( (preferences = getSharedPreferences(
				SettingsActivity.SHARED_PREFERENCES_NAME,
				0 )) == null )
			return;

		loadColors();
	}

	@Override
	public void onPause()
	{
		super.onPause();

		saveColors();
	}

	private void loadColors()
	{
		int colors[] = getCustomColors( preferences );

		if( colors == null )
			colors = WaveLinesWallpaperService.getThemeColors(
				getApplicationContext(),
				preferences );

		if( colors == null )
			return;

		for( int n = 0, l = colors.length;
			n < l;
			++n )
			addColor( colors[n] );
	}

	private void saveColors()
	{
		final int count = colorList.getChildCount();
		final SharedPreferences.Editor editor = preferences.edit();

		editor.putInt( "custom_colors", count );

		for( int n = 0; n < count; ++n )
		{
			View v = colorList.getChildAt( n );

			if( v instanceof ColorLayout )
			{
				editor.remove( "custom_color"+n );
				editor.putInt( "custom_color"+n, ((ColorLayout) v).color );
			}
		}

		editor.putString( "theme", count > 0 ? "custom" : "blue" );
		editor.commit();
	}
}
