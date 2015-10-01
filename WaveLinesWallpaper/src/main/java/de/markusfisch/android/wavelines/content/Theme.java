package de.markusfisch.android.wavelines.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Theme implements Serializable
{
	public boolean coupled = true;
	public boolean uniform = false;
	public int colors[] = {
		0xff0060a0,
		0xff00b0f0,
		0xff0080c0,
		0xff00a0e0,
		0xff0070b0,
		0xff0090d0 };
	public int lines = 24;
	public int waves = 3;
	public float relativeAmplitude = .02f;

	public boolean save( File file )
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream( file ) );

			oos.writeObject( this );
			oos.close();

			return true;
		}
		catch( IOException e )
		{
			return false;
		}
	}

	public static Theme restore( File file )
	{
		try
		{
			ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream( file ) );

			Theme theme = (Theme)ois.readObject();
			ois.close();

			return theme;
		}
		catch( ClassNotFoundException e )
		{
			return null;
		}
		catch( IOException e )
		{
			return null;
		}
	}
}
