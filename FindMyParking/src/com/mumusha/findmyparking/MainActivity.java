package com.mumusha.findmyparking;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
//import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static final String ACTION_NEW = "org.xmlvm.tutorial.intent.action.ACTION_NEW";
    //private TextView           resultField;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferences sharedPref = getSharedPreferences("userRecord",
				MODE_PRIVATE);
		Boolean hasRecord = sharedPref.getBoolean("hasRecord", false);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 /**
     * This method will be invoked whenever the button is clicked in the
     * BlueActivity. This will trigger the invocation of YellowActivity.
     */
    public void onClickNew(View v) {
        //resultField.setText("");
        /**
         * Construct an Intent to launch NewRecord. Note that
         * NewRecord is referenced explicitly via its class object.
         */
        Intent intent = new Intent(this, NewRecord.class);
        intent.setClass(MainActivity.this, NewRecord.class);
        startActivity(intent);
        
    }
    public void onClickFind(View v) {
        //resultField.setText("");
        /**
         * Construct an Intent to launch NewRecord. Note that
         * NewRecord is referenced explicitly via its class object.
         */
    	
		SharedPreferences sharedPref = getSharedPreferences("userRecord",
				MODE_PRIVATE);
		Boolean hasRecord = sharedPref.getBoolean("hasRecord", false);
		
		
    	if(!hasRecord){ 
    		Toast.makeText(this, "Don't have record", Toast.LENGTH_SHORT).show();
    		return;
    		
    	}
    	
        Intent intent = new Intent(this, FindRecord.class);
        intent.setClass(MainActivity.this, FindRecord.class);
        startActivity(intent);
        
    }
}
