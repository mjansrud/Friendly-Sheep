package com.friendlysheep;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.EmptyBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.anddev.andengine.opengl.texture.atlas.bitmap.source.decorator.BaseBitmapTextureAtlasSourceDecorator;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.qwerjk.andengine.entity.sprite.PixelPerfectAnimatedSprite;
import com.qwerjk.andengine.entity.sprite.PixelPerfectSprite;
import com.qwerjk.andengine.opengl.texture.region.PixelPerfectTextureRegion;
import com.qwerjk.andengine.opengl.texture.region.PixelPerfectTextureRegionFactory;
import com.qwerjk.andengine.opengl.texture.region.PixelPerfectTiledTextureRegion;
import android.graphics.Color;
import android.graphics.Typeface;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout.LayoutParams;

public class ActivityGame extends BaseGameActivity implements IOnSceneTouchListener{
	
    // ===========================================================
    // Constants
    // ===========================================================

    private static int CAMERA_WIDTH;
    private static int CAMERA_HEIGHT;
    private int score = 0;
    private TimerTask scoreTimer;

    // ===========================================================
    // Fields
    // ===========================================================

    private Random mRandom;
	private Paint mPaint;
	private Paint mPaintTransparent;
	private int mDisplayWidth;
	private int mDisplayHeight;
	private Boolean alive = true, once = true;
	private Dialog gameOver;
	private final Context context = this;
	private Runnable runnable;

    private Bitmap  mBitmap;
    private Font mFont;
    private Scene mScene;
    private Engine mEngine;
    private Camera mCamera;
    private BitmapTextureAtlas mBulletTexture;
    private BitmapTextureAtlas mSheepTexture;
    private BitmapTextureAtlas mShieldTexture;
    private PixelPerfectTextureRegion mShieldRegion;
    private PixelPerfectTextureRegion mSheepRegion;
    private PixelPerfectTiledTextureRegion mBulletRegion;
    
    private BitmapTextureAtlas mFontTexture;
	private ArrayList<Shape> mBulletSprites;
	private ArrayList<Shape> mTargetSprites;

    private PixelPerfectSprite mShield;
    private PixelPerfectSprite mSheep;
	private PixelPerfectTextureRegion mDrawingTextureRegion;
    private IBitmapTextureAtlasSource mDrawingTextureSource;
	private IBitmapTextureAtlasSource decoratedTextureAtlasSource;

	
	@Override
	public Engine onLoadEngine() {
		
	    //log game started
		Log("Engine loaded");
		mRandom = new Random();
		getDisplayInfo();
		
	    this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    final Engine engine = new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	
	    try {
	        if(MultiTouch.isSupported(this)) {
	            engine.setTouchController(new MultiTouchController());
	        } else {
	            Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
	        }
	    } catch (final MultiTouchException e) {
	        Toast.makeText(this, "Sorry your Android Version does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
	    }
	    
	    gameRunning();	    	

	
		this.mEngine = engine;
	    return engine;
	}
	
	@Override
	public void onLoadResources() {
		PixelPerfectTextureRegionFactory.setAssetBasePath("gfx/");
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
	
	    mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	
	    mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.BLACK);
	    
	    mEngine.getTextureManager().loadTexture(this.mFontTexture);
	    mEngine.getFontManager().loadFont(this.mFont);
	
	    //create textures with minimum sizes
	    mShieldTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.DEFAULT);
	    mBulletTexture = new BitmapTextureAtlas(2048, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    mSheepTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	   
	    //create regions and fetch bitmaps - add to texture
	    mBulletRegion = PixelPerfectTextureRegionFactory.createTiledFromAsset(mBulletTexture, this, "spinning-triangle.png", 0, 0, 20, 1);
	    mSheepRegion = PixelPerfectTextureRegionFactory.createFromAsset(mSheepTexture, this, "sheep.png", 0, 0);
	    mShieldRegion = PixelPerfectTextureRegionFactory.createFromAsset(mShieldTexture, this, "shield.png", 0, 0);
	    
	    //drawing source
    	mDrawingTextureSource = new EmptyBitmapTextureAtlasSource(mDisplayWidth, mDisplayHeight);
    	mDrawingTextureSource.onLoadBitmap(Bitmap.Config.ALPHA_8);
    	
	    mEngine.getTextureManager().loadTextures(mBulletTexture, mSheepTexture, mShieldTexture);
	}
	
	@Override
	public Scene onLoadScene() {
	    this.mEngine.registerUpdateHandler(new FPSLogger());
		    
	    mScene = new Scene(1);
	    mScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
		mScene.setOnSceneTouchListener(this);
	
	    //mViewDrawPath = new ViewDrawPath(this);
	    //addContentView(mViewDrawPath, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    
	    mSheep  = addSprite(mScene, 250, 1000, mSheepRegion);  

	    mBulletSprites = new ArrayList<Shape>();
	    mTargetSprites = new ArrayList<Shape>();
	    
	    mTargetSprites.add(mSheep);
	    
	    
	    final ChangeableText scoreText = new ChangeableText(CAMERA_WIDTH/2, 50, this.mFont, "Score: " + score + "  ");
	    final ChangeableText collisionText = new ChangeableText(0, 0, this.mFont, "no collisions");
	    
	    mScene.attachChild(scoreText);
	    mScene.attachChild(collisionText);
	    
	    /* The actual collision-checking. */
	    mScene.registerUpdateHandler(new IUpdateHandler() {
	        @Override
	        public void reset() { } 
	
	        @Override
	        public void onUpdate(final float pSecondsElapsed) {
	            for(Shape bullet : mBulletSprites){
	    	            for(Shape target : mTargetSprites){
		                    if(bullet != null && target != null && bullet.collidesWith(target)){
		                    	
		                        collisionText.setText("bam!");
		                        
		                    	if(target != mSheep){
		                    		score++;
		                    		scoreText.setText("Score: " + score + "");		                    		
		                    	}
		                    	else{
		                    		scoreText.setText("GAME OVER!");
		                    		alive = false;
		                    	}
		                    	
		                    	Log("Removed item");
		                        mBulletSprites.remove(bullet);
		                        bullet.detachSelf();
		                        return;
		                    }
	                }
	            }
	            collisionText.setText(""); 
	        }
	    });
	    
	    return mScene;
	}
	
    @Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

			switch (pSceneTouchEvent.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	    		Log("Down");
	    	    mSheep  = addSprite(mScene, 250, 1000, mShieldRegion);   
	        break;
	        case MotionEvent.ACTION_MOVE: 
	    		Log("Move");
	        break;
	        case MotionEvent.ACTION_UP:
	    		Log("Up");

                mScene.attachChild(mSheep);	  
                mTargetSprites.add(mSheep);  
	            
            	final Handler handler = new Handler();
    		    handler.postDelayed(new Runnable() { 
    		    	@Override
    		    	public void run() {
    	                mTargetSprites.remove(mSheep);
    		    		mSheep.detachSelf();  
    		    		Log.i("DELAY", "-------------------------");
    		    	}
    		    }, 1000);
	        break;
	    }   
	    return true;
	}
	
	public void getDisplayInfo(){
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		CAMERA_WIDTH = size.x;
		CAMERA_HEIGHT = size.y;
		
		mDisplayWidth = size.x;
		mDisplayHeight = size.y;

	}

	
	public void gameRunning(){
		
		newAnimation();
		
	}

	public void newAnimation(){
		
		
		final Handler h = new Handler();
		final int delay = mRandom.nextInt(5000 - 2000) + 2000; //milliseconds 

		h.postDelayed(runnable = new Runnable(){
		    public void run(){
		    	if(alive){
		    		newSprite();
		    		h.postDelayed(this, delay);		    		
		    	}
		    	else if (!alive && once){
		    		once = false;
		    		stop();
		    		//Removes all "in action" bullets
		    		for(Shape bullet : mBulletSprites){
		    			bullet.detachSelf();
		    		}
		    	}
		    }
		    
		    public void stop(){
		    	
		    	onStop(h, runnable);
		    	//Creates the popup window
		    	gameOver = new Dialog(context);
	          	gameOver.setContentView(R.layout.game_over_view);
	          	gameOver.setTitle("Game Over");
		    	TextView tv_gameOver = (TextView) gameOver.findViewById(R.id.tv_gameOver);
		    	tv_gameOver.setText("What a shame! The sheep is shaved.\nYour score: " + score);
		    	
		    	gameOver.show();
		    	Button b_restart = (Button) gameOver.findViewById(R.id.b_restart);
		    	b_restart.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
    					startActivity(new Intent(ActivityGame.this, ActivityGame.class));

						
					}
				});
		    }
		}, delay);		
		
	}
	protected void onStop(Handler handler, Runnable runnable) {
		//To stop the handler (the timer)
		super.onStop();
		handler.removeCallbacks(runnable);
	}
	
	public void newSprite(){

		 mBulletSprites.add(addAnimatedSprite(mScene, -10, -10, 10, mBulletRegion));
		 
	}
	
	public Integer[] RandomizeAnimation(){
		
		int randomY = 0;
		int randomX = 0;
		
		int randomPosition = mRandom.nextInt(4);
		
		switch (randomPosition){
			case 0: 
				randomY = mRandom.nextInt(mDisplayHeight - 0) + 0;
				randomX = -20;
				break;
			case 1:
				randomY = -20;
				randomX = mRandom.nextInt(mDisplayWidth - 0) + 0;
				break;
			case 2:
				randomY = mRandom.nextInt(mDisplayHeight - 0) + 0;
				randomX = mDisplayWidth + 20;
				break;
			case 3:
				randomY = mDisplayHeight + 20;
				randomX = mRandom.nextInt(mDisplayWidth - 0) + 0;
				break;
		}
		
		Integer[] list = new Integer[] {randomX,450,randomY,1100};
		
		return list;
	}
	
	private void Log(String log){
		Log.i("ActivityGame", log);
	}
	
	@Override
	public void onBackPressed(){
		Log.i("onback","pressed");
		startActivity(new Intent(this, ActivityMenu.class));
	}

	
	private PixelPerfectAnimatedSprite addAnimatedSprite(final Scene scene, final int x, final int y, final int speed, final PixelPerfectTiledTextureRegion region){
	   
		PixelPerfectAnimatedSprite sprite = new PixelPerfectAnimatedSprite(x,y,region){ };
	    sprite.animate(speed, true);
	    scene.attachChild(sprite);
	    scene.registerTouchArea(sprite);

		Integer[] coordinates = RandomizeAnimation();
		float[] objCenterPos = new float[2];
		objCenterPos = mSheep.getSceneCenterCoordinates();
		org.anddev.andengine.entity.modifier.PathModifier.Path path = new org.anddev.andengine.entity.modifier.PathModifier.Path(2).to(coordinates[0], coordinates[2]).to(objCenterPos[0], objCenterPos[1]);
		sprite.registerEntityModifier(new LoopEntityModifier(new PathModifier(3, path)));
		
	    return sprite;
	} 
	
	private PixelPerfectSprite addSprite(final Scene scene, final int x, final int y, final PixelPerfectTextureRegion region){
	    PixelPerfectSprite sprite = new PixelPerfectSprite(x,y,region);
	    scene.attachChild(sprite);
	    scene.registerTouchArea(sprite);
	    return sprite;
	}
	
	
	@Override
	public void onLoadComplete() {

	    //run game
		gameRunning();
		
	}

}
