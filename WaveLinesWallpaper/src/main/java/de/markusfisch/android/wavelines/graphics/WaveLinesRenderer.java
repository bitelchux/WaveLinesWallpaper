package de.markusfisch.android.wavelines.graphics;

import de.markusfisch.android.wavelines.content.Theme;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class WaveLinesRenderer
{
	private final Paint paint = new Paint( Paint.ANTI_ALIAS_FLAG );
	private final Path path = new Path();
	private float thicknessMax;
	private float thicknessMin;
	private float amplitudeMax;
	private float amplitudeMin;
	private Theme theme = new Theme();
	private WaveLine waveLines[];
	private float width = 0;
	private float height = 0;

	public void reset( Theme theme )
	{
		this.theme = theme;
		waveLines = null;
	}

	public void setup( int width, int height )
	{
		this.width = width;
		this.height = height;
	}

	public void draw( Canvas canvas, long dt )
	{
		if( waveLines == null )
		{
			create();
			return;
		}

		final double elapsed = dt/1000.0;
		float r = 0;

		for( int n = theme.lines;
			n-- > 0; )
		{
			waveLines[n].flow( elapsed );

			if( r > height )
				continue;

			// build path
			{
				final float l = waveLines[n].length;
				final float h = l/2;
				float lx = waveLines[n].shift;
				float y = r;
				float ly = y;
				float x = lx+l;

				path.reset();
				path.moveTo(
					lx,
					ly );

				if( y == 0 )
				{
					x = width;
					path.lineTo(
						x,
						y );
				}
				else
				{
					final float a = waveLines[n].amplitude;

					for( ;; lx = x, x += l )
					{
						final float m = lx+h;

						path.cubicTo(
							m,
							y-a,
							m,
							y+a,
							x,
							y );

						if( x > width )
							break;
					}
				}

				r += waveLines[n].thickness;
				ly = r+amplitudeMax*2;
				path.lineTo( x, ly );
				path.lineTo( 0, ly );
			}

			paint.setColor( waveLines[n].color );
			canvas.drawPath( path, paint );
		}
	}

	private void create()
	{
		if( theme.lines < 1 ||
			width < 1 ||
			height < 1 ||
			theme.colors == null ||
			theme.colors.length < 2 )
			return;

		// calculates sizes relative to screen size
		final float maxSize = Math.max( width, height );

		thicknessMax = (float)Math.ceil(
			(float)(maxSize/theme.lines)*2f );
		thicknessMin = .01f*maxSize;

		if( thicknessMin < 2 )
			thicknessMin = 2;

		amplitudeMax = theme.relativeAmplitude*maxSize;
		amplitudeMin = -amplitudeMax;

		waveLines = new WaveLine[theme.lines];

		int hl = 0;
		float growths[] = null;
		int indices[] = null;

		if( !theme.uniform )
		{
			hl = theme.lines/2;
			growths = new float[hl];
			indices = new int[hl];

			// calculate growth of master rows
			{
				final float min = maxSize*.0001f;
				final float max = maxSize*.0020f;

				for( int n = hl;
					n-- > 0; )
				{
					growths[n] =
						(Math.random() > .5d ? -1 : 1)*
						(min+(float)Math.random()*max);
					indices[n] = n;
				}
			}

			// mix indices to have random partners
			for( int n = hl;
				n-- > 0; )
			{
				final int p = (int)Math.round(
					Math.random()*(hl-1) );

				if( p == n )
					continue;

				int i = indices[p];
				indices[p] = indices[n];
				indices[n] = i;
			}
		}

		// create wave lines
		{
			int c = (int)(Math.random()*(theme.colors.length-1));
			final float av = (float)maxSize/theme.lines;

			final int l = (int)Math.ceil( (float)maxSize/theme.waves );
			final float v = l*.1f;
			final float hv = v/2f;
			WaveLine last = null;

			for( int n = theme.lines;
				n-- > 0;
				c = (++c)%theme.colors.length )
				last = waveLines[n] = new WaveLine(
					theme.coupled ? last : null,
					l,
					v,
					hv,
					av,
					!theme.uniform && n < hl ? growths[n] : 0,
					theme.colors[c],
					!theme.uniform && n >= hl ? indices[n-hl] : -1 );
		}
	}

	private class WaveLine
	{
		public float length;
		public float thickness;
		public float growth;
		public float amplitude;
		public float power;
		public float shift;
		public float speed;
		public int color;
		public int yang;

		public WaveLine(
			final WaveLine ref,
			final int l,
			final float v,
			final float hv,
			final float t,
			final float g,
			final int c,
			final int y )
		{
			if( ref == null )
			{
				length = l+((float)Math.random()*v-hv);
				thickness = t;
				amplitude = amplitudeMin+(float)Math.ceil(
					Math.random()*(amplitudeMax-amplitudeMin) );
				power = theme.coupled ?
					.1f+amplitudeMax*.37f :
					.1f+(float)Math.random()*(amplitudeMax*.75f);
				shift = -(float)Math.random()*(length*2);
				speed = (width*.01f)+(float)Math.random()*(width*.03125f);
			}
			else
			{
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

		public void flow( final double dt )
		{
			// raise the power if the wave gets shallow to avoid
			// having straight lines for too long
			float p = amplitudeMax-Math.abs( amplitude );

			if( Math.abs( power ) > p )
				p = power;
			else if( power < 0 )
				p = -p;

			amplitude += p*dt;

			if( amplitude > amplitudeMax ||
				amplitude < amplitudeMin )
			{
				if( amplitude > amplitudeMax )
					amplitude =
						amplitudeMax-
						(amplitude-amplitudeMax);
				else
					amplitude =
						amplitudeMin+
						(amplitudeMin-amplitude);

				power = -power;
			}

			shift += speed*dt;

			if( shift > 0 )
				shift -= length*2;

			if( yang > -1 )
			{
				thickness =
					(thicknessMax+thicknessMin)-
					waveLines[yang].thickness;
			}
			else if( growth != 0 )
			{
				thickness += growth*dt;

				if( thickness > thicknessMax ||
					thickness < thicknessMin )
				{
					if( thickness > thicknessMax )
					{
						thickness = thicknessMax-
							(thickness-thicknessMax);
					}
					else
					{
						thickness = thicknessMin+
							(thicknessMin-thickness);
					}

					growth = -growth;
				}
			}
		}
	}
}
