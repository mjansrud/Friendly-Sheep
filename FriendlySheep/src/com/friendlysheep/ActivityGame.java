package com.friendlysheep;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
 

public class ActivityGame extends Activity{

	//Defining variables
	private boolean alive;
	private Button b_left; 
	private Button b_right;
	private Button b_top;
	private Button b_bottom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		setLayout();
	}
	
	private void setLayout(){
		
		b_left 	= (Button)findViewById(R.id.b_left);
		b_right = (Button)findViewById(R.id.b_right);
		b_top 	= (Button)findViewById(R.id.b_top);
		b_bottom= (Button)findViewById(R.id.b_bottom);
		
	}
	
	
	private void playGame(){
		
		while(alive){
			
			
			
			
		}
		
	}

	
}
