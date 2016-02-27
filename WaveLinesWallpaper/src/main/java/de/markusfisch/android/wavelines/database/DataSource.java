package de.markusfisch.android.wavelines.database;

import de.markusfisch.android.wavelines.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class DataSource
{
	public static final String THEMES = "themes";
	public static final String THEMES_ID = "_id";
	public static final String THEMES_COUPLED = "coupled";
	public static final String THEMES_UNIFORM = "uniform";
	public static final String THEMES_LINES = "lines";
	public static final String THEMES_WAVES = "waves";
	public static final String THEMES_AMPLITUDE = "amplitude";
	public static final String THEMES_COLORS = "colors";

	private SQLiteDatabase db;

	public DataSource( Context context )
	{
		openAsync(
			new OpenHelper( context ),
			context );
	}

	public boolean isOpen()
	{
		return db != null;
	}

	public Cursor queryThemes()
	{
		return db.rawQuery(
			"SELECT "+
				THEMES_ID+","+
				THEMES_COLORS+
				" FROM "+THEMES+
				" ORDER BY "+THEMES_ID,
			null );
	}

	public Theme getTheme( long id )
	{
		Cursor cursor = db.rawQuery(
			"SELECT "+
				THEMES_ID+","+
				THEMES_COUPLED+","+
				THEMES_UNIFORM+","+
				THEMES_LINES+","+
				THEMES_WAVES+","+
				THEMES_AMPLITUDE+","+
				THEMES_COLORS+
				" FROM "+THEMES+
				" WHERE "+THEMES_ID+"="+id,
			null );

		if( cursor == null )
			return null;

		Theme theme = null;

		if( cursor.moveToFirst() )
			theme = themeFromCursor( cursor );

		cursor.close();

		return theme;
	}

	public long insertTheme(
		boolean coupled,
		boolean uniform,
		int lines,
		int waves,
		double amplitude,
		int colors[] )
	{
		return insertTheme(
			db,
			coupled,
			uniform,
			lines,
			waves,
			amplitude,
			colors );
	}

	public void updateTheme(
		long id,
		boolean coupled,
		boolean uniform,
		int lines,
		int waves,
		double amplitude,
		int colors[] )
	{
		db.update(
			THEMES,
			getThemeContentValues(
				coupled,
				uniform,
				lines,
				waves,
				amplitude,
				colors ),
			THEMES_ID+"="+id,
			null );
	}

	public void deleteTheme( long id )
	{
		db.delete(
			THEMES,
			THEMES_ID+"="+id,
			null );
	}

	public static int[] colorsFromCursor( Cursor cursor )
	{
		byte bytes[] = cursor.getBlob(
			cursor.getColumnIndex( THEMES_COLORS ) );

		if( bytes == null )
			return null;

		IntBuffer ib = ByteBuffer
			.wrap( bytes )
			.order( ByteOrder.nativeOrder() )
			.asIntBuffer();

		int colors[] = new int[ib.remaining()];
		ib.get( colors );

		return colors;
	}

	private Theme themeFromCursor( Cursor cursor )
	{
		return new Theme(
			cursor.getInt(
				cursor.getColumnIndex( THEMES_COUPLED ) ) > 0,
			cursor.getInt(
				cursor.getColumnIndex( THEMES_UNIFORM ) ) > 0,
			cursor.getInt(
				cursor.getColumnIndex( THEMES_LINES ) ),
			cursor.getInt(
				cursor.getColumnIndex( THEMES_WAVES ) ),
			cursor.getFloat(
				cursor.getColumnIndex( THEMES_AMPLITUDE ) ),
			colorsFromCursor( cursor ) );
	}

	private static long insertTheme(
		SQLiteDatabase db,
		boolean coupled,
		boolean uniform,
		int lines,
		int waves,
		double amplitude,
		int colors[] )
	{
		return db.insert(
			THEMES,
			null,
			getThemeContentValues(
				coupled,
				uniform,
				lines,
				waves,
				amplitude,
				colors ) );
	}

	private static ContentValues getThemeContentValues(
		boolean coupled,
		boolean uniform,
		int lines,
		int waves,
		double amplitude,
		int colors[] )
	{
		ByteBuffer bb = ByteBuffer.allocate( colors.length << 2 );
		bb.order( ByteOrder.nativeOrder() );
		IntBuffer ib = bb.asIntBuffer();
		ib.put( colors );

		ContentValues cv = new ContentValues();
		cv.put( THEMES_COUPLED, coupled );
		cv.put( THEMES_UNIFORM, uniform );
		cv.put( THEMES_LINES, lines );
		cv.put( THEMES_WAVES, waves );
		cv.put( THEMES_AMPLITUDE, amplitude );
		cv.put( THEMES_COLORS, bb.array() );

		return cv;
	}

	private void insertDefaultThemes( SQLiteDatabase db )
	{
		insertTheme(
			db,
			true,
			false,
			24,
			3,
			.02,
			new int[] {
				0xff0060a0,
				0xff00b0f0,
				0xff0080c0,
				0xff00a0e0,
				0xff0070b0,
				0xff0090d0 } );

		insertTheme(
			db,
			false,
			false,
			4,
			2,
			.04,
			new int[] {
				0xff00b06c,
				0xff007ac6,
				0xffe86f13,
				0xffcf6310 } );
	}

	private void openAsync(
		final OpenHelper helper,
		final Context context )
	{
		new AsyncTask<Void, Void, Boolean>()
		{
			@Override
			protected Boolean doInBackground( Void... nothings )
			{
				try
				{
					return (db = helper.getWritableDatabase()) != null;
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
					context,
					R.string.error_database,
					Toast.LENGTH_LONG ).show();
			}
		}.execute();
	}

	private class OpenHelper extends SQLiteOpenHelper
	{
		public OpenHelper( Context c )
		{
			super( c, "themes.db", null, 1 );
		}

		@Override
		public void onCreate( SQLiteDatabase db )
		{
			db.execSQL( "DROP TABLE IF EXISTS "+THEMES );
			db.execSQL(
				"CREATE TABLE "+THEMES+" ("+
					THEMES_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
					THEMES_COUPLED+" INTEGER,"+
					THEMES_UNIFORM+" INTEGER,"+
					THEMES_LINES+" INTEGER,"+
					THEMES_WAVES+" INTEGER,"+
					THEMES_AMPLITUDE+" DOUBLE,"+
					THEMES_COLORS+" BLOB );" );

			insertDefaultThemes( db );
		}

		@Override
		public void onDowngrade(
			SQLiteDatabase db,
			int oldVersion,
			int newVersion )
		{
			// without that method, a downgrade will
			// cause an exception
		}

		@Override
		public void onUpgrade(
			SQLiteDatabase db,
			int oldVersion,
			int newVersion )
		{
			// there'll be upgrades
		}
	}
}
