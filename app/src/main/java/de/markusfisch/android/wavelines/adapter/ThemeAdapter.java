package de.markusfisch.android.wavelines.adapter;

import de.markusfisch.android.wavelines.database.DataSource;
import de.markusfisch.android.wavelines.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

public class ThemeAdapter extends CursorAdapter {
	public int thumbnailColumn;

	public ThemeAdapter(Context context, Cursor cursor) {
		super(context, cursor, false);
		findColumnIndices();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return inflater.inflate(R.layout.row_theme, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		setData(getViewHolder(view), cursor);
	}

	private ViewHolder getViewHolder(View view) {
		ViewHolder holder;

		if ((holder = (ViewHolder) view.getTag()) == null) {
			holder = new ViewHolder();
			holder.thumbnail = (ImageView) view.findViewById(
					R.id.thumbnail);
		}

		return holder;
	}

	private void setData(ViewHolder holder, Cursor cursor) {
		if (holder == null) {
			return;
		}

		byte bytes[] = cursor.getBlob(thumbnailColumn);
		holder.thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(
					bytes,
					0,
					bytes.length));
	}

	private void findColumnIndices() {
		Cursor cursor = getCursor();

		if (cursor == null) {
			return;
		}

		thumbnailColumn = cursor.getColumnIndex(DataSource.THEMES_THUMBNAIL);
	}

	private static final class ViewHolder {
		private ImageView thumbnail;
	}
}
