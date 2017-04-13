package de.markusfisch.android.wavelines.fragment;

import de.markusfisch.android.wavelines.app.WaveLinesApp;
import de.markusfisch.android.wavelines.database.Theme;
import de.markusfisch.android.wavelines.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ThemeEditorFragment extends Fragment {
	private static final String ID = "id";

	private long themeId;

	public static ThemeEditorFragment newInstance(long id) {
		Bundle args = new Bundle();
		args.putLong(ID, id);

		ThemeEditorFragment fragment = new ThemeEditorFragment();
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public View onCreateView(
			LayoutInflater inflater,
			ViewGroup container,
			Bundle state) {
		View view = inflater.inflate(
				R.layout.fragment_theme_editor,
				container,
				false);

		Theme theme;
		Bundle args = getArguments();
		if (args != null &&
				(themeId = args.getLong(ID)) > 0 &&
				(theme = WaveLinesApp.dataSource.getTheme(
						themeId)) != null) {
			setTheme(theme);
		}

		return view;
	}

	private void setTheme(Theme theme) {
	}
}
