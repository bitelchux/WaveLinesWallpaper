package de.markusfisch.android.wavelines.service;

import de.markusfisch.android.wavelines.app.WaveLinesApp;
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

	private class WaveLinesEngine extends CanvasWallpaperEngine {
		private final WaveLinesRenderer renderer = new WaveLinesRenderer();

		public WaveLinesEngine() {
			super();

			WaveLinesApp.preferences.getPreferences().registerOnSharedPreferenceChangeListener(
					new SharedPreferences.OnSharedPreferenceChangeListener() {
				@Override
				public void onSharedPreferenceChanged(
						SharedPreferences preferences,
						String key) {
					update();
				}
			});
			update();
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

			renderer.setSize(width, height);
		}

		@Override
		protected void drawFrame(Canvas canvas, long delta) {
			canvas.save();
			renderer.draw(canvas, delta);
			canvas.restore();
		}

		private void update() {
			delay = WaveLinesApp.preferences.getDelay();
			Theme theme = WaveLinesApp.dataSource.getTheme(
					WaveLinesApp.preferences.getTheme());

			if (theme == null) {
				return;
			}

			renderer.setTheme(theme);
		}
	}
}
