package de.markusfisch.android.wavelines.activity;

import de.markusfisch.android.wavelines.fragment.ThemesFragment;
import de.markusfisch.android.wavelines.R;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_main);

		initToolbar();

		if (state == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame, new ThemesFragment())
					.commit();
		}
	}

	private void initToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
	}
}
