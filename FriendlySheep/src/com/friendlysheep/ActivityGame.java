package com.friendlysheep;

import java.util.ArrayList;
import java.util.Random;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ActivityGame extends Activity  {
	
	private TextView tv_position;
	private ImageView iv_sheep, iv_stone;
	private RelativeLayout rl_screen;
	private TranslateAnimation ta_leftToRigth;
	private LayoutParams lp_sheep;
	private Random r_anim;
	private Paint mPaint;
	private int displayWidth;
	private int displayHeight;
	private Activity activity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		setLayout();
		
		Log("View created");
		
		/*
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
		*/
		
		 getDisplayInfo();
		 gameRunning();
	}
	
	public void getDisplayInfo(){
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		displayWidth = size.x;
		displayHeight = size.y;

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
	

	public void setLayout(){

		Log("Setting layout");
		
		activity = this;
		rl_screen = (RelativeLayout) findViewById(R.id.rl_screen);
		iv_sheep = (ImageView) findViewById(R.id.iv_sheep);
		lp_sheep = (LayoutParams) iv_sheep.getLayoutParams();
		lp_sheep.width = 100;
		lp_sheep.height = 100;
		iv_sheep.setLayoutParams(lp_sheep);
		iv_sheep.setX(450);
		iv_sheep.setY(1100);
		
	    final ViewDrawPath viewDrawPath = new ViewDrawPath(this);
	    rl_screen.addView(viewDrawPath);
	    
	    mPaint = new Paint();
	    mPaint.setAntiAlias(true);
	    mPaint.setDither(true);
	    mPaint.setColor(Color.WHITE);
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setStrokeJoin(Paint.Join.ROUND);
	    mPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPaint.setStrokeWidth(12);  
	    
	    
	}
	
	public void setAnimation(){

		Integer[] coordinates = RandomizeAnimation();
		ta_leftToRigth = new TranslateAnimation(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
	    ta_leftToRigth.setDuration(1000);
	    
	    iv_stone = new ImageView(activity);
	    iv_stone.setBackgroundResource(R.drawable.water);

		iv_stone.setLayoutParams(lp_sheep);
	    rl_screen.addView(iv_stone);
	    iv_stone.startAnimation(ta_leftToRigth);
	    
	}
	
	public Integer[] RandomizeAnimation(){
		
		int randomY = 0;
		int randomX = 0;
		
		r_anim = new Random();
		int randomPosition = r_anim.nextInt(4);
		
		switch (randomPosition){
			case 0:
				randomY = r_anim.nextInt(displayHeight - 0) + 0;
				randomX = -20;
				break;
			case 1:
				randomY = -20;
				randomX = r_anim.nextInt(displayWidth - 0) + 0;
				break;
			case 2:
				randomY = r_anim.nextInt(displayHeight - 0) + 0;
				randomX = displayWidth + 20;
				break;
			case 3:
				randomY = displayHeight + 20;
				randomX = r_anim.nextInt(displayWidth - 0) + 0;
				break;
		}
		
		Integer[] list = new Integer[] {randomX,450,randomY,1100};
		
		return list;
	}
	
	private void Log(String log){
		Log.i("ActivityGame", log);
	}


public class ViewDrawPath extends View {

        public int width;
        public  int height;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public ViewDrawPath(Context c) {
	        super(c);
	        context=c;
	        mPath = new Path();
	        mBitmapPaint = new Paint(Paint.DITHER_FLAG);  
	        circlePaint = new Paint();
	        circlePath = new Path();
	        circlePaint.setAntiAlias(true);
	        circlePaint.setColor(Color.BLUE);
	        circlePaint.setStyle(Paint.Style.STROKE);
	        circlePaint.setStrokeJoin(Paint.Join.MITER);
	        circlePaint.setStrokeWidth(4f);
	        
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
	        mCanvas = new Canvas(mBitmap);
        }
        @Override
        protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

		    canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
		    canvas.drawPath( mPath,  mPaint);
		    canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
	        mPath.reset();
	        mPath.moveTo(x, y);
	        mX = x;
	        mY = y;
        }
        private void touch_move(float x, float y) {
	        float dx = Math.abs(x - mX);
	        float dy = Math.abs(y - mY);
	        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
	             mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
	            mX = x;
	            mY = y;
	
	            circlePath.reset();
	            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
	        }
        }
        private void touch_up() {
	        mPath.lineTo(mX, mY);
	        circlePath.reset();
	        // commit the path to our offscreen
	        mCanvas.drawPath(mPath,  mPaint);
	        // kill this so we don't double draw
	        
			final Handler h = new Handler();
			final int delay = 1000; //milliseconds

			h.postDelayed(new Runnable(){
			    public void run(){
			        // setAnimation();
			        h.postDelayed(this, delay);
			    }
			}, delay);
	        
	        mPath.reset();
	        
	        final Handler handler = new Handler();
		    handler.postDelayed(new Runnable() {
		    	@Override
		    	public void run() {
		    		Log.i("DELAY", "-------------------------");
		    	}
		    }, 1500);
        }

        @SuppressLint("ClickableViewAccessibility") @Override
        public boolean onTouchEvent(MotionEvent event) {
	        float x = event.getX();
	        float y = event.getY();
	
	        switch (event.getAction()) {
	            case MotionEvent.ACTION_DOWN:
	                touch_start(x, y);
	                invalidate();
	                break;
	            case MotionEvent.ACTION_MOVE:
	                touch_move(x, y);
	                invalidate();
	                break;
	            case MotionEvent.ACTION_UP:
	                touch_up();
	                invalidate();
	                break;
	        }
	        return true;
        }
        
        
    }

	
}
