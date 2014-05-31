package ics.cs237.childseeking.activity;

import ics.cs237.childseeking.helper.JsonHandler;
import ics.cs237.childseekingapp.activity.R;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
/**
 * @author CHazyhabiT
 *
 */
public class DisplayLocationsActivity extends Activity {

	private ArrayList<LatLng> locaions = new ArrayList<LatLng>();
	private GoogleMap map;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.display_locations_activity);
		
		// remind
		setProgressBarIndeterminateVisibility(true);
		Toast.makeText(DisplayLocationsActivity.this, "initializing the map...", Toast.LENGTH_LONG).show();
		
		// interpret the locations
		Intent intent = getIntent();
		String locationsString = intent.getStringExtra(android.content.Intent.EXTRA_TEXT);
		locaions = JsonHandler.interpretLocations(locationsString);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		for(int i=0;i<locaions.size();i++) {
			map.addMarker(new MarkerOptions().position(locaions.get(i)));		
		}
		
//		Marker kiel = map.addMarker(new MarkerOptions()
//				.position(KIEL)
//				.title("Kiel")
//				.snippet("Kiel is cool")
//				.icon(BitmapDescriptorFactory
//						.fromResource(R.drawable.ic_launcher)));

		// Move the camera instantly to hamburg with a zoom of 10.
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(locaions.get(1), 10));
		// Zoom in, animating the camera.
		// Zoom to level 14 (0~18 the lower level, the less details)
		// animateCamera(CameraUpdate update, int durationMs, GoogleMap.CancelableCallback callback)
		// Moves the map according to the update with an animation over a specified duration,
		// and calls an optional callback on completion.
		map.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
		setProgressBarIndeterminateVisibility(false);
	}
	

	
	
	

}

