package de.markusfisch.android.wavelines.preference;

import de.markusfisch.android.wavelines.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import java.lang.NumberFormatException;

public class Preferences {
	public static final String THEME_ID = "theme_id";
	public static final String DELAY = "delay";

	private SharedPreferences preferences;
	private int delay = 100;
	private long themeId = 0;

	public void init(Context context) {
		PreferenceManager.setDefaultValues(
				context,
				R.xml.preferences,
				false);

		preferences = PreferenceManager.getDefaultSharedPreferences(
				context);

		update();
	}

	public SharedPreferences getPreferences() {
		return preferences;
	}

	public void update() {
		themeId = parseLong(
				preferences.getString(THEME_ID, null),
				themeId);
		delay = parseInt(
				preferences.getString(DELAY, null),
				delay);
	}

	public int getDelay() {
		return delay;
	}

	public long getTheme() {
		return themeId;
	}

	public void setTheme(long id) {
		themeId = id;

		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(
				THEME_ID,
				String.valueOf(themeId));
		editor.apply();
	}

	private static int parseInt(String s, int preset) {
		try {
			if (s != null && s.length() > 0) {
				return Integer.parseInt(s);
			}
		} catch (NumberFormatException e) {
			// use preset
		}

		return preset;
	}

	private static long parseLong(String s, long preset) {
		try {
			if (s != null && s.length() > 0) {
				return Long.parseLong(s);
			}
		} catch (NumberFormatException e) {
			// use preset
		}

		return preset;
	}
}
