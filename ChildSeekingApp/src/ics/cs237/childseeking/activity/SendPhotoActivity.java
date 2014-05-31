package ics.cs237.childseeking.activity;

import ics.cs237.childseeking.filehelper.FileHelper;
import ics.cs237.childseeking.helper.HttpConstants;
import ics.cs237.childseeking.helper.JsonHandler;
import ics.cs237.childseekingapp.activity.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

/**
 * take photo and upload it with the addition information
 * 
 * @author CHazyhabiT
 * 
 */
public class SendPhotoActivity extends Activity {
	
	public static final String TAG = SendPhotoActivity.class.getSimpleName();
	public static final String TYPE_IMAGE = "image";
	
	// request
	private static final int TAKE_PHOTO_REQUEST = 1;
	
			
	// UI component
	private ImageView mImageView;
	private EditText mEditText;
	private Button mRetakeButton;
	private Button mSubmitButton;
	
	private LocationManager mLocationManager;
	
	// photo
	private Uri mMediaUri = null;
	public String photoName;
	
	// additional info
	private String mComment;
	private String mTimeStamp;
	private Location mLocation;
	
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.send_photo_activity);

		// UI component
		mImageView = (ImageView) findViewById(R.id.imageView1);
		mEditText = (EditText) findViewById(R.id.editText1);
		mRetakeButton = (Button) findViewById(R.id.button_retake);
		mSubmitButton = (Button) findViewById(R.id.button_submit);
		
		// Acquire reference to the LocationManager
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (mLocationManager==null) {
			// pop up a dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Oops!");
			builder.setMessage("Location service is unavailable!!!Please try it later!");
			AlertDialog dialog = builder.create();
			dialog.show();
			
			// go back to main activity
			Intent intent = new Intent(SendPhotoActivity.this, StartActivity.class);
			startActivity(intent);
		}
		
		// when activity first created, takePhotos();
		takePhoto();
				
		mRetakeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				takePhoto();
			}
		});
		
		mSubmitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSubmitButton.setEnabled(false);
				submit();
			}
		});

	}
	
	private void takePhoto() {		
		// reminder
		setProgressBarIndeterminateVisibility(false);
		
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mMediaUri = getOutputMediaFileUri();
		if(mMediaUri==null) {
			// display an error
			Toast.makeText(SendPhotoActivity.this, "uri for photo is null", Toast.LENGTH_LONG).show();
		} else {
			// the image will automatically stored
			takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
			startActivityForResult(takePhotoIntent, TAKE_PHOTO_REQUEST);
			new GetLoactionTask().execute();
		}
	}
	
	/**
	 * submit photo and text to server
	 */
	protected void submit() {
		// remind
		setProgressBarIndeterminateVisibility(true);
		Toast.makeText(SendPhotoActivity.this, "the photo is uploading...", Toast.LENGTH_LONG).show();
		
		if(mLocation==null) {
			new GetLoactionTask().execute();
			// alert
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Oops!");
			builder.setMessage("Location service is unavailable!!!Please re-submit later!");
			AlertDialog dialog = builder.create();
			dialog.show();
			setProgressBarIndeterminateVisibility(false);
			return;
		}
		
		// compress photo
		byte[] fileBytes = FileHelper.getByteArrayFromFile(SendPhotoActivity.this, mMediaUri);
		if(fileBytes==null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Oops!");
			builder.setMessage("Photo error!!!Please re-take a new one!");
			AlertDialog dialog = builder.create();
			dialog.show();
			setProgressBarIndeterminateVisibility(false);
			return;
		} else {
			fileBytes = FileHelper.reduceImageForUpload(fileBytes);			
		}

		// retrieve additional info
		mComment = mEditText.getText().toString();
		// mTimeStamp
		double latidude = mLocation.getLatitude();
		double longitude = mLocation.getLongitude();
		String additionalInfo = JsonHandler.additionalInfo(mTimeStamp, mComment, latidude, longitude);
		
		
		// encapsulate the message
		RequestParams params = new RequestParams();
		params.put(HttpConstants.SEND_IMAGE_KEY, new ByteArrayInputStream(fileBytes));
		params.put(HttpConstants.SEND_ADDINFO_KEY, additionalInfo);
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(HttpConstants.UP_LOAD_URL, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onSuccess(int arg0, String feedback) {
				// super.onSuccess(arg0, feedback)
				setProgressBarIndeterminateVisibility(false);
				Toast.makeText(SendPhotoActivity.this, "Send Successfully!", Toast.LENGTH_LONG).show();
				
				// onSuccess
				Intent intent = new Intent(SendPhotoActivity.this, ChooseMultiPhotoActivity.class);
				intent.setType("text/plain");  
				intent.putExtra(android.content.Intent.EXTRA_TEXT, feedback);
			}	
		});
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSubmitButton.setEnabled(true);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG,"request: " + requestCode +" result: " + resultCode);
		if(resultCode==RESULT_OK) {
			if(requestCode==TAKE_PHOTO_REQUEST) {
				// display on ImageView
				Picasso.with(SendPhotoActivity.this).load(mMediaUri.toString()).into(mImageView);
			}
		} else if(resultCode==RESULT_CANCELED){
			Toast.makeText(SendPhotoActivity.this, "RESULT_CANCELED", Toast.LENGTH_LONG).show();
		}	
	}
	
	/**
	 * check whether SD Card is mounted
	 */
	private boolean isExternalStorageAvailable() {
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else return false;
	}

	protected Uri getOutputMediaFileUri() {
		// to be safe, you should check SD Card is mounted,
		// using Environment.getExternalStorageState() before doing this
		if (isExternalStorageAvailable()) {
			// 1. get the external storage directory
			String storeDir = getString(R.string.store_directory);
			File mediaStorageDir = new File(
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					storeDir);
			Log.i(TAG, "Store directory" + mediaStorageDir.getPath());
			// 2. create the sub dire
			if (!mediaStorageDir.exists()) {
				// File.mkdirs() will make directories along this path
				if (!mediaStorageDir.mkdirs()) {
					Log.e(TAG, "Fail to make directory.");
					return null;
				}
				;
			}
			// 3. create the file name
			// 4. create the file
			File mediaFile;
			Date now = new Date();
			mTimeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(now);
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
					Locale.US).format(now);
			String path = mediaStorageDir.getPath() + File.separator;
			mediaFile = new File(path + "IMG_" + timeStamp + ".jpg");
			Log.i(TAG, "File path: " + Uri.fromFile(mediaFile));
			// 5. create the file Uri
			return Uri.fromFile(mediaFile);
		} else {
			return null;
		}
	}
	
	/**
	 * get the current location in AsyncTask
	 *
	 */
	class GetLoactionTask extends AsyncTask<Object, Object, Location> {

		@Override
		protected Location doInBackground(Object... args) {
			return getLocation();
		}
		
		@Override
		protected void onPostExecute(Location result) {
			mLocation = result;			
		}
		
		private Location getLocation() {
			Location bestResult = null;
			float bestAccuracy = Float.MAX_VALUE;
			if(mLocationManager==null) return null;
			List<String> matchingProviderStrings = mLocationManager.getAllProviders();
			for(String provider : matchingProviderStrings) {
				Location location = mLocationManager.getLastKnownLocation(provider);
				if(location!=null) {
					float accuracy = location.getAccuracy();
					if(accuracy < bestAccuracy) {
						bestResult = location;
						bestAccuracy = accuracy;
					}	
				}
			}		
			Log.i(TAG, "Latitude: " + bestResult.getLatitude());
			Log.i(TAG, "Longitude: " + bestResult.getLongitude());
			return bestResult;
		}
	}

	

}
