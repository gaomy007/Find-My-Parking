package com.mumusha.findmyparking;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class NewRecord extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_record);
	}
	
	
    public void onClickReset(View v) {
        //resultField.setText("");
        /**
         * Construct an Intent to launch NewRecord. Note that
         * NewRecord is referenced explicitly via its class object.
         */
    	EditText NoteText = (EditText) findViewById(R.id.editText1);  
    	NoteText.setText(null);
    	
    	TimePicker tmp = (TimePicker) this.findViewById(R.id.timePicker1);
        tmp.setCurrentHour(0);
        tmp.setCurrentMinute(0);
    }
    
    
    public void onClickOk(View v) {
        //resultField.setText("");
        /**
         * Construct an Intent to launch NewRecord. Note that
         * NewRecord is referenced explicitly via its class object.
         */
        Intent intent = new Intent(this, MainActivity.class);
        intent.setClass(NewRecord.this,MainActivity.class);
        startActivity(intent);
        
    }

	public void onBackPressed() {

		super.onBackPressed();
	}

}
