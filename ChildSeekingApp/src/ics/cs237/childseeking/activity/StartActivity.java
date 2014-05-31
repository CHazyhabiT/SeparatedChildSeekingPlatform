package ics.cs237.childseeking.activity;

import ics.cs237.childseekingapp.activity.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


/**
 * The entry of this App
 * 
 * @author CHazyhabiT
 *
 */

public class StartActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);

		final Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent sendPhotoIntent = new Intent(StartActivity.this, SendPhotoActivity.class);
				startActivity(sendPhotoIntent);

			}
		});
	}

}