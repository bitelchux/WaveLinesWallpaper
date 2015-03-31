package de.markusfisch.android.wallpaper.service;

import android.os.SystemClock;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.graphics.Canvas;

public abstract class WallpaperService
	extends android.service.wallpaper.WallpaperService
{
	protected abstract class WallpaperEngine extends Engine
	{
		protected int delay = 40;

		final private Handler handler = new Handler();
		final private Runnable runnable = new Runnable()
		{
			public void run()
			{
				nextFrame();
			}
		};

		private boolean visible = false;
		private long time = 0;

		@Override
		public void onDestroy()
		{
			super.onDestroy();

			stopRunnable();
		}

		@Override
		public void onVisibilityChanged( boolean v )
		{
			visible = v;

			if( visible )
			{
				time = SystemClock.elapsedRealtime();
				nextFrame();
			}
			else
				stopRunnable();
		}

		@Override
		public void onSurfaceChanged(
			SurfaceHolder holder,
			int format,
			int width,
			int height )
		{
			super.onSurfaceChanged( holder, format, width, height );

			nextFrame();
		}

		@Override
		public void onSurfaceDestroyed( SurfaceHolder holder )
		{
			visible = false;
			stopRunnable();

			super.onSurfaceDestroyed( holder );
		}

		@Override
		public void onOffsetsChanged(
			float xOffset,
			float yOffset,
			float xOffsetStep,
			float yOffsetStep,
			int xPixelOffset,
			int yPixelOffset )
		{
		}

		protected abstract void drawFrame( Canvas canvas, long e );

		protected void nextFrame()
		{
			stopRunnable();

			if( !visible )
				return;

			handler.postDelayed( runnable, delay );

			final SurfaceHolder h = getSurfaceHolder();
			Canvas canvas = null;

			try
			{
				if( (canvas = h.lockCanvas()) != null )
				{
					final long now = SystemClock.elapsedRealtime();
					drawFrame( canvas, now-time );
					time = now;
				}
			}
			finally
			{
				if( canvas != null )
					h.unlockCanvasAndPost( canvas );
			}
		}

		private void stopRunnable()
		{
			handler.removeCallbacks( runnable );
		}
	}
}
