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
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ActivityGame extends Activity {
	
	private TextView tv_position;
	private ImageView iv_sheep, iv_stone, iv_shield;
	private RelativeLayout rl_screen;
	private AlertDialog dialog;
	private TranslateAnimation ta_leftToRigth;
	private LayoutParams lp_sheep, lp_shield;
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
				 gameRunning();
			 }
		}
	    .start();
		
	}
	
	public void gameRunning(){
		final Handler h = new Handler();
		final int delay = 1000; //milliseconds

		h.postDelayed(new Runnable(){
		    public void run(){
		        setAnimation();
		        h.postDelayed(this, delay);
		    }
		}, delay);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		if(x < 400){
			tv_position.setText("LEFT");
			setShield("LEFT");
		}
		else if(x > 700){
			tv_position.setText("RIGHT");
			setShield("RIGHT");
		}
		else if(y < 1400){
			tv_position.setText("TOP");
			setShield("TOP");
		}
		else if(y > 1500){
			tv_position.setText("BOTTOM");
			setShield("BOTTOM");
		}
		Log.i(Integer.toString(x),Integer.toString(y));
		return false;
	}
	
	public void setLayout(){
		rl_screen = (RelativeLayout) findViewById(R.id.rl_screen);
		
		tv_position = (TextView) findViewById(R.id.tv_position);
		
		iv_sheep = (ImageView) findViewById(R.id.iv_sheep);
		lp_sheep = (LayoutParams) iv_sheep.getLayoutParams();
		lp_sheep.width = 100;
		lp_sheep.height = 100;
		iv_sheep.setLayoutParams(lp_sheep);
		iv_sheep.setX(450);
		iv_sheep.setY(1100);
	}
	
	public void setAnimation(){
		Integer[] coordinates = RandomizeAnimation();
		ta_leftToRigth = new TranslateAnimation(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
	    ta_leftToRigth.setDuration(1000);
	    
	    iv_stone = new ImageView(this);
	    iv_stone.setBackgroundResource(R.drawable.water);
//	    LayoutParams params = (LayoutParams) iv_stone.getLayoutParams();
//		params.width = 100;
//		params.height = 100;
		iv_stone.setLayoutParams(lp_sheep);
	    
	    rl_screen.addView(iv_stone);
	    
	    iv_stone.startAnimation(ta_leftToRigth);
	}
	
	public Integer[] RandomizeAnimation(){
		r_anim = new Random();
		int randomHolder = r_anim.nextInt(4);
		Integer[] list;
		if(randomHolder == 0){							//LEFT
			list = new Integer[] {0,400,1100,1100};
		}
		else if(randomHolder == 1){						//RIGHT
			list = new Integer[] {800,500,1100,1100};
		}
		else if(randomHolder == 2){						//TOP
			list = new Integer[] {450,450,0,1100};
		}
		else{											//BOTTOM
			list = new Integer[] {450,450,1400,1100};
		}
		return list;
	}
	
	public void setShield(String position){
		iv_shield = new ImageView(this);
		iv_shield.setBackgroundResource(R.drawable.background_ground);
		lp_shield = new LayoutParams(10,70);
//		iv_shield.setLayoutParams(lp_shield);
//		
		if(position == "LEFT"){
			iv_shield.setX(400);
			iv_shield.setY(1100);			
		}
		else if (position == "RIGHT"){
			iv_shield.setX(600);
			iv_shield.setY(1100);
		}
		else if (position == "TOP"){
			iv_shield.setRotation(90);
			iv_shield.setX(500);
			iv_shield.setY(1000);
		}
		else if (position == "BOTTOM"){
			iv_shield.setRotation(90);
			iv_shield.setX(500);
			iv_shield.setY(1200);
		}
		rl_screen.addView(iv_shield, lp_shield);
	}
}

