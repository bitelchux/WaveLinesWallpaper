package de.markusfisch.android.wavelines.fragment;

import de.markusfisch.android.wavelines.adapter.ThemeAdapter;
import de.markusfisch.android.wavelines.app.WaveLinesApplication;
import de.markusfisch.android.wavelines.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class ThemesFragment extends Fragment {
	private final Runnable queryThemesRunnable = new Runnable() {
		@Override
		public void run() {
			if (!WaveLinesApplication.dataSource.isOpen()) {
				listView.postDelayed(queryThemesRunnable, 100);
				return;
			}

			Context context = getActivity();
			if (context != null) {
				listView.setAdapter(new ThemeAdapter(
						context,
						WaveLinesApplication.dataSource.queryThemes()));
			}
		}
	};

	private ListView listView;

	@Override
	public View onCreateView(
			LayoutInflater inflater,
			ViewGroup container,
			Bundle state) {
		View view = inflater.inflate(
				R.layout.fragment_themes,
				container,
				false );

		listView = (ListView) view.findViewById(R.id.themes);
		listView.post(queryThemesRunnable);
		listView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(
					AdapterView<?> parent,
					View view,
					int position,
					long id) {
				// edit theme
			}
		});

		return view;
	}
}
