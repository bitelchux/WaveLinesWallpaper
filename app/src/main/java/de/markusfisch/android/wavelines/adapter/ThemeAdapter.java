package de.markusfisch.android.wavelines.adapter;

import de.markusfisch.android.wavelines.database.DataSource;
import de.markusfisch.android.wavelines.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class ThemeAdapter extends CursorAdapter
{
	public int idColumn;
	public int colorsColumn;

	public ThemeAdapter( Context context, Cursor cursor )
	{
		super( context, cursor, false );

		findColumnIndices();
	}

	@Override
	public View newView(
		Context context,
		Cursor cursor,
		ViewGroup parent )
	{
		LayoutInflater inflater = LayoutInflater.from(
			parent.getContext() );

		return inflater.inflate(
			R.layout.row_theme,
			parent,
			false );
	}

	@Override
	public void bindView(
		View view,
		Context context,
		Cursor cursor )
	{
		setData( getViewHolder( view ), cursor );
	}

	private ViewHolder getViewHolder( View view )
	{
		ViewHolder holder;

		if( (holder = (ViewHolder)view.getTag()) == null )
		{
			holder = new ViewHolder();
			holder.colors = view.findViewById( R.id.colors );
		}

		return holder;
	}

	private void setData( ViewHolder holder, Cursor cursor )
	{
		if( holder == null )
			return;

		int colors[] = DataSource.colorsFromCursor( cursor );

		if( colors == null ||
			colors.length < 1 )
			return;

		holder.colors.setBackgroundColor( colors[0] );
	}

	private void findColumnIndices()
	{
		Cursor cursor = getCursor();

		if( cursor == null )
			return;

		idColumn = cursor.getColumnIndex(
			DataSource.THEMES_ID );
		colorsColumn = cursor.getColumnIndex(
			DataSource.THEMES_COLORS );
	}

	private static final class ViewHolder
	{
		public View colors;
	}
}
