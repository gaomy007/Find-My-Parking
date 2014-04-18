package com.mumusha.findmyparking;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import android.graphics.BitmapFactory;
//import android.graphics.BitmapFactory;
import android.location.Location;
//import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
//import android.widget.TimePicker;
import android.widget.Toast;

public class FindRecord extends Activity {
	private SupportMapFragment mapFragment;
	private GoogleMap googleMap;
	private ImageButton mImageButton;
	private ScrollView mainScrollView;
	private ImageView transparentImageView;

	private TextView userNote;
	private ImageView userImage;
	private LocationClient mLocationClient;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static Uri fileUri = null;
	private int deletablePic = 0;
	private EditText edtInput;
	private final long interval = 1 * 1000;
	private CountDownTimer countDownTimer;
	private TextView chron;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_record);
		chron = (TextView) findViewById(R.id.chronometer1);
		userNote = (TextView) findViewById(R.id.textViewUserInput);
		userImage = (ImageView) findViewById(R.id.imageView1);

		SharedPreferences sharedPref = getSharedPreferences("userRecord",
				MODE_PRIVATE);

		String restoredNote = sharedPref.getString("note", "default");

		if (restoredNote != null) {
			userNote.setText(restoredNote);
			Log.i("note", restoredNote);
		} else
			Log.i("note", "null");

		String picPath = sharedPref.getString("picPath", "default");
		if (picPath.equals("default")) {

			Log.i("userPic", "default");
		} else {

			Log.i("userPic", picPath);

			Uri fileUri = Uri.parse(picPath);

			Bitmap bitmap = null;

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
			userImage.setImageBitmap(bitmap);

		}
		Time now = new Time();
		now.setToNow();
		long restSec;
		int hour = now.hour;
		int mintue = now.minute;
		int parkingHour = sharedPref.getInt("parkingHour", 0);
		int parkingMin = sharedPref.getInt("parkingMin", 0);
		restSec = ((parkingHour - hour) * 60 + (parkingMin - mintue)) *60* 1000;
		Log.i("parkingHour", Integer.toString(parkingHour));
		Log.i("parkingMin", Integer.toString(parkingMin));
		Log.i("restSec", Long.toString(restSec));
		countDownTimer = new MyCountDownTimer(restSec, interval);

		chron.setText(chron.getText() + String.valueOf(restSec / 1000));
		countDownTimer.start();

	}

	public class MyCountDownTimer extends CountDownTimer {

		public MyCountDownTimer(long startTime, long interval) {

			super(startTime, interval);

		}

		@Override
		public void onFinish() {

			chron.setText("Time's up!");

		}

		@Override
		public void onTick(long millisUntilFinished) {

			chron.setText("" + millisUntilFinished / 1000);

		}

	}

	public void onClickFinish(View v) {
		// resultField.setText("");
		/**
		 * Construct an Intent to launch NewRecord. Note that NewRecord is
		 * referenced explicitly via its class object.
		 */
		SharedPreferences sharedPref = getSharedPreferences("userRecord",MODE_PRIVATE);
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putBoolean("hasRecord", false);
		editor.commit();
		
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.setClass(FindRecord.this, MainActivity.class);
		startActivity(intent);

	}
}
