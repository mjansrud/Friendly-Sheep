package com.friendlysheep;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ActivityGameVersiontwo extends Activity {
	
	private TextView tv_position;
	private ImageView iv_sheep, iv_stone;
	private RelativeLayout rl_screen;
	private AlertDialog dialog;
	private TranslateAnimation ta_leftToRigth;
	private LayoutParams params;
	private Random r_anim;
	private Boolean alive = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_game_versiontwo);
		setLayout();
		
		dialog = new AlertDialog.Builder(this).setTitle("Get ready").setMessage("3").setIcon(android.R.drawable.ic_dialog_info).show();
		
		new CountDownTimer(3000, 1000) {

			 public void onTick(long millisUntilFinished) {
				 dialog.setMessage("" + (millisUntilFinished / 1000));
			  //here you can have your logic to set text to edittext 
			 }
 
			 public void onFinish() {
				 dialog.setMessage("0"); 
				 dialog.hide();
				 setAnimation();
			 }
		}
	    .start();
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		if(x < 400){
			tv_position.setText("LEFT");
		}
		else if(x > 700){
			tv_position.setText("RIGHT");			
		}
		else if(y < 1400){
			tv_position.setText("TOP");			
		}
		else if(y > 1500){
			tv_position.setText("BOTTOM");			
		}
		Log.i(Integer.toString(x),Integer.toString(y));
		return false;
	}
	
	public void setLayout(){
		rl_screen = (RelativeLayout) findViewById(R.id.rl_screen);
		
		tv_position = (TextView) findViewById(R.id.tv_position);
		
		iv_sheep = (ImageView) findViewById(R.id.iv_sheep);
		params = (LayoutParams) iv_sheep.getLayoutParams();
		params.width = 100;
		params.height = 100;
		iv_sheep.setLayoutParams(params);
		iv_sheep.setX(450);
		iv_sheep.setY(1100);
	}
	
	public void setAnimation(){
		Integer[] coordinates = RandomizeAnimation();
		ta_leftToRigth = new TranslateAnimation(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
	    ta_leftToRigth.setDuration(1000);
	    ta_leftToRigth.setFillAfter(false);
	    
	    iv_stone = new ImageView(this);
	    iv_stone.setBackgroundResource(R.drawable.water);
//	    LayoutParams params = (LayoutParams) iv_stone.getLayoutParams();
//		params.width = 100;
//		params.height = 100;
		iv_stone.setLayoutParams(params);
	    
	    rl_screen.addView(iv_stone);
	    
	    iv_stone.startAnimation(ta_leftToRigth);
	}
	
	public Integer[] RandomizeAnimation(){
		r_anim = new Random();
		int randomHolder = r_anim.nextInt(3);
		Integer[] list;
		if(randomHolder == 0){
			list = new Integer[] {0,400,1100,1100};
		}
		else if(randomHolder == 1){
			list = new Integer[] {800,500,1100,1100};
		}
		else if(randomHolder == 2){
			list = new Integer[] {450,450,0,1100};
		}
		else{
			list = new Integer[] {450,450,1400,1100};
		}
		return list;
	}
	//Method to run animation every x second
	public void runAnimationOnRepeat(){
		
	}
}

