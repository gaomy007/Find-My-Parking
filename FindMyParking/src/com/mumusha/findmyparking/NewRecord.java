package com.mumusha.findmyparking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
//import android.view.View.OnTouchListener;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
//import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.location.Location;
//import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
//import android.widget.TimePicker;
import android.widget.Toast;

public class NewRecord extends FragmentActivity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private SupportMapFragment mapFragment;
	private GoogleMap googleMap;
	private ImageButton mImageButton;
	private ScrollView mainScrollView;
	private ImageView transparentImageView;
	private LocationClient mLocationClient;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static Uri fileUri = null;
	private int deletablePic = 0;
	private EditText edtInput;

	public static class ErrorDialogFragment extends DialogFragment {

		// Global field to contain the error dialog
		private Dialog mDialog;

		// Default constructor. Sets the dialog field to null
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		// Set the dialog to display
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		// Return a Dialog to the DialogFragment.
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_record);
		// R.id.
		mLocationClient = new LocationClient(this, this, this);
		mapFragment = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map));
		googleMap = mapFragment.getMap();
		googleMap.setMyLocationEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);
		googleMap.getUiSettings().setRotateGesturesEnabled(false);

		// add listener to handle the conflict between scroll and map
		mainScrollView = (ScrollView) findViewById(R.id.scroll);
		transparentImageView = (ImageView) findViewById(R.id.transparent_image);
		mImageButton = (ImageButton) findViewById(R.id.imageButton1);
		transparentImageView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					// Disallow ScrollView to intercept touch events.
					mainScrollView.requestDisallowInterceptTouchEvent(true);
					// Disable touch on transparent view
					return false;

				case MotionEvent.ACTION_UP:
					// Allow ScrollView to intercept touch events.
					mainScrollView.requestDisallowInterceptTouchEvent(false);
					return true;

				case MotionEvent.ACTION_MOVE:
					mainScrollView.requestDisallowInterceptTouchEvent(true);
					return false;

				default:
					return true;
				}
			}
		}

		);

		edtInput = (EditText) findViewById(R.id.edt_settime);

	}

	/*
	 * Called when the Activity becomes visible.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		if (isGooglePlayServicesAvailable()) {
			mLocationClient.connect();
		}

	}

	@Override
	protected void onStop() {
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Decide what to do based on the original request code
		switch (requestCode) {

		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			/*
			 * If the result code is Activity.RESULT_OK, try to connect again
			 */
			switch (resultCode) {
			case Activity.RESULT_OK:
				mLocationClient.connect();
				break;
			}

		case REQUEST_TAKE_PHOTO:
			Bitmap bitmap = null;
			deletablePic = 1;
			try {
				GetImageThumbnail getImageThumbnail = new GetImageThumbnail();
				bitmap = getImageThumbnail.getThumbnail(fileUri, this);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mImageButton.setImageBitmap(bitmap);
			break;

		}
	}

	private void deletePic() {

		if (deletablePic == 1) {
			new File(mCurrentPhotoPath).delete();

			// getContentResolver().delete(Uri.fromFile(fileUri), null, null);
			deletablePic = 0;
			Log.i("pic deleted", "p");
		}
	}

	private boolean isGooglePlayServicesAvailable() {
		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Location Updates", "Google Play services is available.");
			return true;
		} else {
			// Get the error dialog from Google Play services
			Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
					resultCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

			// If Google Play services can provide an error dialog
			if (errorDialog != null) {
				// Create a new DialogFragment for the error dialog
				ErrorDialogFragment errorFragment = new ErrorDialogFragment();
				errorFragment.setDialog(errorDialog);
				// errorFragment.show(getSupportFragmentManager(),"Location Updates");
			}

			return false;
		}
	}

	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		Location location = mLocationClient.getLastLocation();
		/*
		while(location == null){
			
			 location = mLocationClient.getLastLocation();
		}
		*/
		
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,
				17);
		googleMap.animateCamera(cameraUpdate);
	}

	@Override
	public void onDisconnected() {
		// Display the connection status
		Toast.makeText(this, "Disconnected. Please re-connect.",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		if (connectionResult.hasResolution()) {
			try {
				// Start an Activity that tries to resolve the error
				connectionResult.startResolutionForResult(this,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
			} catch (IntentSender.SendIntentException e) {
				// Log the error
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Sorry. Location services not available to you",
					Toast.LENGTH_LONG).show();
		}
	}

	public void onClickReset(View v) {
		// resultField.setText("");
		/**
		 * Construct an Intent to launch NewRecord. Note that NewRecord is
		 * referenced explicitly via its class object.
		 */
		EditText NoteText1 = (EditText) findViewById(R.id.editText1);
		NoteText1.setText(null);
		EditText NoteText2 = (EditText) findViewById(R.id.edt_settime);
		NoteText2.setText(null);
		mImageButton.setImageResource(R.drawable.ic_launcher1);

		deletePic();

	}
	
	
	
	private void saveRecord(){
		Location location = mLocationClient.getLastLocation();
		SharedPreferences sharedPref = getSharedPreferences("userRecord",MODE_PRIVATE);
		
		SharedPreferences.Editor editor = sharedPref.edit();
		Time now = new Time();
		now.setToNow();
		
		int hour=now.hour;
		int mintue=now.minute;
		Log.i("currenth1", Integer.toString(hour));
		Log.i("currentm1", Integer.toString(mintue));
		int wantMin=Integer.parseInt(edtInput.getText().toString());
		hour=(hour+wantMin/60)%23;
		mintue=(mintue+wantMin%60)%59;
		editor.putBoolean("hasRecord", true);
		editor.putInt("parkingHour", hour);
		editor.putInt("parkingMin", mintue);
		Log.i("currenth", Integer.toString(hour));
		Log.i("currentm", Integer.toString(mintue));
		
		editor.putString("note", ((EditText)findViewById(R.id.editText1)).getText().toString());
		editor.putString("picPath", mCurrentPhotoPath);
		editor.putLong("latitude", (long)location.getLatitude());
		editor.putLong("longitude", (long)location.getLongitude());
		
		editor.commit();	
		Log.i("savedLa",Double.toString(location.getLatitude()) );
		Log.i("savedLo",Double.toString(location.getLongitude()) );
		Log.i("notevalue",((EditText)findViewById(R.id.editText1)).getText().toString() );
	}

	public void onClickOk(View v) {
		// resultField.setText("");
		/**
		 * Construct an Intent to launch NewRecord. Note that NewRecord is
		 * referenced explicitly via its class object.
		 */
		// Intent intent = new Intent(this, MainActivity.class);
		// intent.setClass(NewRecord.this, MainActivity.class);
		// startActivity(intent);

		
		
		
		if (edtInput.getText().toString().isEmpty()) {
			Toast.makeText(this, "empty parking time!", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		int n = Integer.parseInt(edtInput.getText().toString());
		if (n > 600 || n < 1) {
			Toast.makeText(this, "invaild parking time!", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		saveRecord();
		Intent intent = new Intent(this, timerService.class);
		intent.putExtra("n", n);
		startService(intent);
		finish();

	}

	public void onBackPressed() {

		super.onBackPressed();
	}

	String mCurrentPhotoPath;

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}

	static final int REQUEST_TAKE_PHOTO = 1;

	public void dispatchTakePictureIntent(View v) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File

			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				deletePic();
				fileUri = Uri.fromFile(photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}

	}

}
