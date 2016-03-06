package de.markusfisch.android.wavelines.activity;

import de.markusfisch.android.wavelines.adapter.ThemeAdapter;
import de.markusfisch.android.wavelines.app.WaveLinesWallpaperApplication;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

public class ThemesActivity extends ListActivity
{
	@Override
	protected void onCreate( Bundle state )
	{
		super.onCreate( state );
		requestWindowFeature( Window.FEATURE_NO_TITLE );

		setListAdapter( new ThemeAdapter(
			this,
			WaveLinesWallpaperApplication
				.dataSource
				.queryThemes() ) );

		final ListView listView = getListView();

		listView.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
		listView.setOnItemClickListener(
			new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(
					AdapterView<?> parent,
					View view,
					int position,
					long id )
				{
				}
			} );
	}
}
