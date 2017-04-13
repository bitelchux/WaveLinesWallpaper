package de.markusfisch.android.wavelines.database;

import de.markusfisch.android.wavelines.graphics.WaveLinesRenderer;
import de.markusfisch.android.wavelines.R;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class DataSource {
	public static final String THEMES = "themes";
	public static final String THEMES_ID = "_id";
	public static final String THEMES_COUPLED = "coupled";
	public static final String THEMES_UNIFORM = "uniform";
	public static final String THEMES_LINES = "lines";
	public static final String THEMES_WAVES = "waves";
	public static final String THEMES_AMPLITUDE = "amplitude";
	public static final String THEMES_COLORS = "colors";
	public static final String THEMES_THUMBNAIL = "thumbnail";

	private SQLiteDatabase db;

	public void openAsync(final Context context) {
		final OpenHelper helper = new OpenHelper(context);

		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... nothings) {
				try {
					return (db = helper.getWritableDatabase()) != null;
				} catch (SQLException e) {
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean success) {
				if (success) {
					return;
				}

				Toast.makeText(
						context,
						R.string.error_database,
						Toast.LENGTH_LONG).show();
			}
		}.execute();
	}

	public boolean isOpen() {
		return db != null;
	}

	public Cursor queryThemes() {
		return db.rawQuery(
				"SELECT " +
						THEMES_ID + "," +
						THEMES_THUMBNAIL +
						" FROM " + THEMES +
						" ORDER BY " + THEMES_ID,
				null);
	}

	public Theme getTheme(long id) {
		Cursor cursor = db.rawQuery(
				"SELECT " +
						THEMES_ID + "," +
						THEMES_COUPLED + "," +
						THEMES_UNIFORM + "," +
						THEMES_LINES + "," +
						THEMES_WAVES + "," +
						THEMES_AMPLITUDE + "," +
						THEMES_COLORS +
						" FROM " + THEMES +
						" WHERE " + THEMES_ID + "=" + id,
				null);

		if (cursor == null) {
			return null;
		}

		Theme theme = null;

		if (cursor.moveToFirst()) {
			theme = themeFromCursor(cursor);
		}

		cursor.close();

		return theme;
	}

	public long insertTheme(Theme theme) {
		return insertTheme(db, theme);
	}

	public void updateTheme(long id, Theme theme) {
		db.update(
				THEMES,
				getThemeContentValues(theme),
				THEMES_ID + "=" + id,
				null);
	}

	public void deleteTheme(long id) {
		db.delete(
				THEMES,
				THEMES_ID + "=" + id,
				null);
	}

	public static int[] colorsFromCursor(Cursor cursor) {
		byte bytes[] = cursor.getBlob(
				cursor.getColumnIndex(THEMES_COLORS));

		if (bytes == null) {
			return null;
		}

		IntBuffer ib = ByteBuffer
				.wrap(bytes)
				.order(ByteOrder.nativeOrder())
				.asIntBuffer();

		int colors[] = new int[ib.remaining()];
		ib.get(colors);

		return colors;
	}

	private Theme themeFromCursor(Cursor cursor) {
		return new Theme(
				cursor.getInt(cursor.getColumnIndex(THEMES_COUPLED)) > 0,
				cursor.getInt(cursor.getColumnIndex(THEMES_UNIFORM)) > 0,
				cursor.getInt(cursor.getColumnIndex(THEMES_LINES)),
				cursor.getInt(cursor.getColumnIndex(THEMES_WAVES)),
				cursor.getFloat(cursor.getColumnIndex(THEMES_AMPLITUDE)),
				colorsFromCursor(cursor));
	}

	private static long insertTheme(SQLiteDatabase db, Theme theme) {
		return db.insert(
				THEMES,
				null,
				getThemeContentValues(theme));
	}

	private static ContentValues getThemeContentValues(Theme theme) {
		ByteBuffer bb = ByteBuffer.allocate(theme.colors.length << 2);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();
		ib.put(theme.colors);

		ContentValues cv = new ContentValues();
		cv.put(THEMES_COUPLED, theme.coupled);
		cv.put(THEMES_UNIFORM, theme.uniform);
		cv.put(THEMES_LINES, theme.lines);
		cv.put(THEMES_WAVES, theme.waves);
		cv.put(THEMES_AMPLITUDE, theme.amplitude);
		cv.put(THEMES_COLORS, bb.array());
		cv.put(THEMES_THUMBNAIL, bitmapToPng(createThumbnail(theme)));

		return cv;
	}

	private static Bitmap createThumbnail(Theme theme) {
		int size = 128;
		Bitmap bitmap = Bitmap.createBitmap(
				size,
				size,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		WaveLinesRenderer renderer = new WaveLinesRenderer();
		renderer.setTheme(theme);
		renderer.setSize(size, size);
		renderer.draw(canvas, 16l);
		return bitmap;
	}

	private static byte[] bitmapToPng(Bitmap bitmap) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		return out.toByteArray();
	}

	private static void insertDefaultThemes(SQLiteDatabase db) {
		insertTheme(db, new Theme(
				true,
				false,
				24,
				3,
				.02f,
				new int[]{
						0xff0060a0,
						0xff00b0f0,
						0xff0080c0,
						0xff00a0e0,
						0xff0070b0,
						0xff0090d0}));
		insertTheme(db, new Theme(
				false,
				false,
				4,
				2,
				.04f,
				new int[]{
						0xff00b06c,
						0xff007ac6,
						0xffe86f13,
						0xffcf6310}));
	}

	private static class OpenHelper extends SQLiteOpenHelper {
		public OpenHelper(Context context) {
			super(context, "themes.db", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("DROP TABLE IF EXISTS " + THEMES);
			db.execSQL("CREATE TABLE " + THEMES + " (" +
					THEMES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					THEMES_COUPLED + " INTEGER," +
					THEMES_UNIFORM + " INTEGER," +
					THEMES_LINES + " INTEGER," +
					THEMES_WAVES + " INTEGER," +
					THEMES_AMPLITUDE + " DOUBLE," +
					THEMES_COLORS + " BLOB," +
					THEMES_THUMBNAIL + " BLOB);");

			insertDefaultThemes(db);
		}

		@Override
		public void onDowngrade(
				SQLiteDatabase db,
				int oldVersion,
				int newVersion) {
		}

		@Override
		public void onUpgrade(
				SQLiteDatabase db,
				int oldVersion,
				int newVersion) {
		}
	}
}
