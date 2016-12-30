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

	public Theme() {
		coupled = true;
		uniform = false;
		lines = 24;
		waves = 3;
		amplitude = .02f;
		colors = new int[]{
				0xff0060a0,
				0xff00b0f0,
				0xff0080c0,
				0xff00a0e0,
				0xff0070b0,
				0xff0090d0};
	}
}
