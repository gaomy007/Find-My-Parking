package com.mumusha.findmyparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class FindRecord extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_record);
	}
	
	
	
    public void onClickFinish(View v) {
        //resultField.setText("");
        /**
         * Construct an Intent to launch NewRecord. Note that
         * NewRecord is referenced explicitly via its class object.
         */
        Intent intent = new Intent(this, MainActivity.class);
        intent.setClass(FindRecord.this,MainActivity.class);
        startActivity(intent);
        
    }
}
