package com.friendlysheep;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
 

public class ActivityMenu extends Activity{

	//Defining variables
	private Button b_play;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setLayout();
	}
	
	private void setLayout(){

		b_play = (Button)findViewById(R.id.b_play);
		b_play.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
                 // Perform action on click   

            	 // Send to ActivityGame
         	  	Intent intent = new Intent(ActivityMenu.this, ActivityGame.class);
        	  	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	  	ActivityMenu.this.startActivity(intent);
        	  	ActivityMenu.this.overridePendingTransition(R.anim.animate_in_left, R.anim.animate_out_left );

             }
         });
		 
	}
	
	
}
