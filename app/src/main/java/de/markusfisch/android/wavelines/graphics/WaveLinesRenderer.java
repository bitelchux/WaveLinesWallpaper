package de.markusfisch.android.wavelines.graphics;

import de.markusfisch.android.wavelines.database.Theme;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class WaveLinesRenderer {
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Path path = new Path();

	private float thicknessMax;
	private float thicknessMin;
	private float amplitudeMax;
	private float amplitudeMin;
	private Theme theme;
	private WaveLine waveLines[];
	private float width = 0;
	private float height = 0;

	public void init(Theme theme) {
		this.theme = theme;
		waveLines = null;
	}

	public void setup(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void draw(Canvas canvas, long delta) {
		if (waveLines == null) {
			create();
		}

		final double elapsed = delta / 1000.0;
		float r = 0;

		for (int i = theme.lines; i-- > 0; ) {
			waveLines[i].flow(elapsed);

			if (r > height) {
				continue;
			}

			// build path
			{
				final float l = waveLines[i].length;
				final float h = l / 2;
				float lx = waveLines[i].shift;
				float y = r;
				float ly = y;
				float x = lx + l;

				path.reset();
				path.moveTo(lx, ly);

				if (y == 0) {
					x = width;
					path.lineTo(x, y);
				} else {
					final float a = waveLines[i].amplitude;

					for (; ; lx = x, x += l) {
						final float m = lx + h;

						path.cubicTo(
								m,
								y - a,
								m,
								y + a,
								x,
								y);

						if (x > width) {
							break;
						}
					}
				}

				r += waveLines[i].thickness;
				ly = r + amplitudeMax * 2;
				path.lineTo(x, ly);
				path.lineTo(0, ly);
			}

			paint.setColor(waveLines[i].color);
			canvas.drawPath(path, paint);
		}
	}

	private void create() {
		if (theme.lines < 1 ||
				width < 1 ||
				height < 1 ||
				theme.colors == null ||
				theme.colors.length < 2) {
			return;
		}

		// calculates sizes relative to screen size
		final float maxSize = Math.max(width, height);

		thicknessMax = (float) Math.ceil(
				(float) (maxSize / theme.lines) * 2f);
		thicknessMin = Math.max(2, .01f * maxSize);

		amplitudeMax = theme.relativeAmplitude * maxSize;
		amplitudeMin = -amplitudeMax;

		waveLines = new WaveLine[theme.lines];

		int hl = 0;
		float growths[] = null;
		int indices[] = null;

		if (!theme.uniform) {
			hl = theme.lines / 2;
			growths = new float[hl];
			indices = new int[hl];

			// calculate growth of master rows
			{
				final float min = maxSize * .0001f;
				final float max = maxSize * .0020f;

				for (int i = hl; i-- > 0; ) {
					growths[i] = (Math.random() > .5 ? -1 : 1) *
							(min + (float) Math.random() * max);
					indices[i] = i;
				}
			}

			// mix indices to have random partners
			for (int i = hl; i-- > 0; ) {
				int p = (int) Math.round(Math.random() * (hl - 1));

				if (p == i) {
					continue;
				}

				int tmp = indices[p];
				indices[p] = indices[i];
				indices[i] = tmp;
			}
		}

		// create wave lines
		{
			int c = (int) (Math.random() * (theme.colors.length - 1));
			final float av = (float) maxSize / theme.lines;

			final int l = (int) Math.ceil((float) maxSize / theme.waves);
			final float v = l * .1f;
			final float hv = v / 2f;
			WaveLine last = null;

			for (int i = theme.lines;
					i-- > 0;
					c = (++c) % theme.colors.length) {
				last = waveLines[i] = new WaveLine(
						theme.coupled ? last : null,
						l,
						v,
						hv,
						av,
						!theme.uniform && i < hl ? growths[i] : 0,
						theme.colors[c],
						!theme.uniform && i >= hl ? indices[i - hl] : -1);
			}
		}
	}

	private class WaveLine {
		private float length;
		private float thickness;
		private float growth;
		private float amplitude;
		private float power;
		private float shift;
		private float speed;
		private int color;
		private int yang;

		private WaveLine(
				WaveLine ref,
				int l,
				float v,
				float hv,
				float t,
				float g,
				int c,
				int y) {
			if (ref == null) {
				length = l + ((float) Math.random() * v - hv);
				thickness = t;
				amplitude = amplitudeMin + (float) Math.ceil(
						Math.random() * (amplitudeMax - amplitudeMin));
				power = theme.coupled ?
						.1f + amplitudeMax * .37f :
						.1f + (float) Math.random() * amplitudeMax * .75f;
				shift = -(float) Math.random() * length * 2;
				speed = width * .01f +
						(float) Math.random() * (width * .03125f);
			} else {
				length = ref.length;
				thickness = ref.thickness;
				amplitude = ref.amplitude;
				power = ref.power;
				shift = ref.shift;
				speed = ref.speed;
			}

			growth = g;
			color = c;
			yang = y;
		}

		private void flow(double delta) {
			// raise the power if the wave gets shallow to avoid
			// having straight lines for too long
			float p = amplitudeMax - Math.abs(amplitude);

			if (Math.abs(power) > p) {
				p = power;
			} else if (power < 0) {
				p = -p;
			}

			amplitude += p * delta;

			if (amplitude > amplitudeMax ||
					amplitude < amplitudeMin) {
				if (amplitude > amplitudeMax) {
					amplitude = amplitudeMax - (amplitude - amplitudeMax);
				} else {
					amplitude = amplitudeMin + (amplitudeMin - amplitude);
				}

				power = -power;
			}

			shift += speed * delta;

			if (shift > 0) {
				shift -= length * 2;
			}

			if (yang > -1) {
				thickness = (thicknessMax + thicknessMin) -
						waveLines[yang].thickness;
			} else if (growth != 0) {
				thickness += growth * delta;

				if (thickness > thicknessMax ||
						thickness < thicknessMin) {
					if (thickness > thicknessMax) {
						thickness = thicknessMax - (thickness - thicknessMax);
					} else {
						thickness = thicknessMin + (thicknessMin - thickness);
					}

					growth = -growth;
				}
			}
		}
	}
}
