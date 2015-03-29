package de.markusfisch.android.wavelines.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Theme implements Serializable
{
	public boolean coupled = true;
	public boolean uniform = false;
	public int colors[] = null;
	public int lines = 24;
	public int waves = 3;
	public float relativeAmplitude = .02f;

	public static boolean save( File file, Theme theme )
	{
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream( file ) );

			oos.writeObject( theme );
			oos.close();

			return true;
		}
		catch( Exception e )
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

			return (Theme)ois.readObject();
		}
		catch( Exception e )
		{
			return null;
		}
	}
}
