package de.markusfisch.android.wavelines.database;

public class Theme
{
	public final boolean coupled;
	public final boolean uniform;
	public final int lines;
	public final int waves;
	public final float relativeAmplitude;
	public final int colors[];

	public Theme(
		boolean coupled,
		boolean uniform,
		int lines,
		int waves,
		float relativeAmplitude,
		int colors[] )
	{
		this.coupled = coupled;
		this.uniform = uniform;
		this.lines = lines;
		this.waves = waves;
		this.relativeAmplitude = relativeAmplitude;
		this.colors = colors;
	}

	public Theme()
	{
		coupled = true;
		uniform = false;
		lines = 24;
		waves = 3;
		relativeAmplitude = .02f;
		colors = new int[] {
			0xff0060a0,
			0xff00b0f0,
			0xff0080c0,
			0xff00a0e0,
			0xff0070b0,
			0xff0090d0 };
	}
}