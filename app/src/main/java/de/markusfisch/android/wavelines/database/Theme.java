package de.markusfisch.android.wavelines.database;

public class Theme {
	public final boolean coupled;
	public final boolean uniform;
	public final int lines;
	public final int waves;
	public final float amplitude;
	public final int colors[];

	public Theme(
			boolean coupled,
			boolean uniform,
			int lines,
			int waves,
			float amplitude,
			int colors[]) {
		this.coupled = coupled;
		this.uniform = uniform;
		this.lines = lines;
		this.waves = waves;
		this.amplitude = amplitude;
		this.colors = colors.clone();
	}
}
