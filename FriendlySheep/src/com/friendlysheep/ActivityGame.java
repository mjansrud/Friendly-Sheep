package com.friendlysheep;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.IBackground;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.BaseGameActivity;
import com.makersf.andengine.extension.collisions.entity.sprite.PixelPerfectAnimatedSprite;
import com.makersf.andengine.extension.collisions.opengl.texture.region.PixelPerfectTextureRegionFactory;
import com.makersf.andengine.extension.collisions.opengl.texture.region.PixelPerfectTiledTextureRegion;


import android.graphics.Color;
import android.graphics.Typeface;

import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class ActivityGame extends BaseGameActivity implements IOnSceneTouchListener{
	
    // ===========================================================
    // Constants
    // ===========================================================

    private static int CAMERA_WIDTH;
    private static int CAMERA_HEIGHT;
	private static final int ALPHA_THERSHOLD = 0;
    private int score = 0;
    

    // ===========================================================
    // Fields
    // ===========================================================

    private Random mRandom;
	private int mDisplayWidth;
	private int mDisplayHeight;
	private Boolean alive = true, once = true;
	private Dialog gameOver;
	private Context context = this;
	private Runnable runnable;

    private Font mFont;
    private Scene mScene;
    private Camera mCamera;
    private BitmapTextureAtlas mBulletTexture;
    private BitmapTextureAtlas mSheepTexture;
    private BitmapTextureAtlas mShieldTexture;
    private PixelPerfectTiledTextureRegion mShieldRegion;
    private PixelPerfectTiledTextureRegion mBulletRegion;
    private PixelPerfectTiledTextureRegion mSheepRegion;
    private VertexBufferObjectManager VBOmanager;
    private BitmapTextureAtlas mFontTexture;
	private ArrayList<PixelPerfectAnimatedSprite> mTrashSprites;
	private ArrayList<PixelPerfectAnimatedSprite> mBulletSprites;
	private ArrayList<PixelPerfectAnimatedSprite> mShieldSprites;

    private PixelPerfectAnimatedSprite mSheep;
  


	@Override 
	public EngineOptions onCreateEngineOptions() {
		// TODO Auto-generated method stub
		
		 //log game started
		Log("Engine loaded");
		mRandom = new Random();
		getDisplayInfo();
		
	    this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
	    gameRunning();	    	

		return new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);

	}


	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		// TODO Auto-generated method stub
		
		mEngine.registerUpdateHandler(new FPSLogger());
		VBOmanager = this.getVertexBufferObjectManager();
	    
	    mScene = new Scene();
		mScene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		mScene.setOnSceneTouchListener(this);
	
	    //mViewDrawPath = new ViewDrawPath(this);
	    //addContentView(mViewDrawPath, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    
		mSheep = addSheepSprite(mScene, 500, 650, mSheepRegion);

	    mTrashSprites = new ArrayList<PixelPerfectAnimatedSprite>();
	    mBulletSprites = new ArrayList<PixelPerfectAnimatedSprite>();
	    mShieldSprites = new ArrayList<PixelPerfectAnimatedSprite>();
	    
	    mShieldSprites.add(mSheep);
	    
		pOnCreateSceneCallback.onCreateSceneFinished(mScene);
	}
	
	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback) {
		// TODO Auto-generated method stub
		
		PixelPerfectTextureRegionFactory.setAssetBasePath("gfx/");
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
	
	    mFontTexture = new BitmapTextureAtlas(getTextureManager(),256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	
	    mFont = new Font(getFontManager(), this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.BLACK);
	    
	
	    //create textures with minimum sizes
        mShieldTexture = new BitmapTextureAtlas(getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
	    mBulletTexture = new BitmapTextureAtlas(getTextureManager(), 2048, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    mSheepTexture = new BitmapTextureAtlas(getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	   
	    //create regions and fetch bitmaps - add to texture
	    mSheepRegion  = PixelPerfectTextureRegionFactory.createTiledFromAsset(mSheepTexture,  this.getAssets(), "sheep.png", 0, 0, 1, 1, ALPHA_THERSHOLD);
	    mShieldRegion = PixelPerfectTextureRegionFactory.createTiledFromAsset(mShieldTexture, this.getAssets(), "shield.png", 0, 0, 1, 1, ALPHA_THERSHOLD);
	    mBulletRegion = PixelPerfectTextureRegionFactory.createTiledFromAsset(mBulletTexture, this.getAssets(), "spinning-triangle.png", 0, 0, 20, 1, ALPHA_THERSHOLD);
	    
	    mShieldTexture.load();
	    mBulletTexture.load();
	    mSheepTexture.load();
	    
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback) {
			// TODO Auto-generated method stub
		
			/* The actual collision-checking. */
			mScene.registerUpdateHandler(new IUpdateHandler() {
	        @Override
	        public void reset() { } 
	
	        @Override
	        public void onUpdate(final float pSecondsElapsed) {
	        	 

	     
	        	   for(PixelPerfectAnimatedSprite trash : mTrashSprites){
	        		   
	        		   mTrashSprites.remove(trash);
	        		   trash.detachSelf();
	        		   
	        	   }
	        		
	    	       for(PixelPerfectAnimatedSprite shield : mShieldSprites){
	    		          for(PixelPerfectAnimatedSprite bullet : mBulletSprites){
	    	
		                    if(bullet.collidesWith(shield)){
		        	        	
		                        Log("Collision!");
		                        mBulletSprites.remove(bullet);
		                        bullet.detachSelf();
		                        
		                    	if(shield != mSheep){
		                    		score++;
		                    		Log("Score: " + score + "");		                    		
		                    	}
		                    	else{
		                    		Log("GAME OVER!");
		                    		alive = false;
		                    		
	        			    		deadMenu();
	        			    		//Removes all "in action" bullets
	        			    		for(PixelPerfectAnimatedSprite trash : mBulletSprites){
	        			    			trash.detachSelf();
	        			    		}
		        			    	
		                    	}
		                    	
		                        break;
		                    }
	                }
	    	    }
	        }
	    });

		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	
	 @Override
	 public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {

				switch (pSceneTouchEvent.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		        	if(mShieldSprites.size() <= 2) mShieldSprites.add(addShieldSprite(mScene, pSceneTouchEvent.getX(), pSceneTouchEvent.getY(), mShieldRegion)); 
		    		Log("Down");
		        break;
		        case MotionEvent.ACTION_MOVE: 
		    		Log("Move");
		        break;
		        case MotionEvent.ACTION_UP:
		    		Log("Up");
	  
		            
		    		((Activity) context).runOnUiThread(new Runnable() {
		    			  public void run() {
		  	            	final Handler handler = new Handler();
			    		    handler.postDelayed(new Runnable() { 
			    		    	@Override
			    		    	public void run() {

			    		    		mTrashSprites.add(mShieldSprites.remove(1));
			    		    		Log.i("DELAY", "-------------------------");
			    		    	}
			    		    }, 400);
		    			  }
		    		});

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
			 
			Log("width: " +CAMERA_WIDTH);
			Log("height: "+CAMERA_HEIGHT);
			
			mDisplayWidth = size.x;
			mDisplayHeight = size.y;

		}

		
		public void gameRunning(){

			bulletTimer();
			
		}
		
		
		public void deadMenu(){
			
			((Activity) context).runOnUiThread(new Runnable() {
  			  public void run() {
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
  			});
		}

		public void bulletTimer(){
			
			final Handler h = new Handler();
			final int delay = mRandom.nextInt(1500 - 200) + 200; //milliseconds 

			h.postDelayed(runnable = new Runnable(){
			    public void run(){
			    	if(alive){
			    		newSprite();
			    		h.postDelayed(this, delay);		    		
			    	}
			    }
			}, delay);		
			
		}
		
		protected void onStop(Handler handler, Runnable runnable) {
			//To stop the handler (the timer)
			super.onStop();
			handler.removeCallbacks(runnable);
		}
		
		public void newSprite(){

			mBulletSprites.add(addBulletSprite(mScene, -10, -10, 10, mBulletRegion));
			 
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

		
		private PixelPerfectAnimatedSprite addBulletSprite(final Scene scene, final int x, final int y, final int speed, final PixelPerfectTiledTextureRegion region){
		   
			PixelPerfectAnimatedSprite sprite = new PixelPerfectAnimatedSprite(x,y,region, VBOmanager);
		    sprite.animate(speed, true);

			Integer[] coordinates = RandomizeAnimation();
			float[] objCenterPos = new float[2];
			objCenterPos = mSheep.getSceneCenterCoordinates();
			org.andengine.entity.modifier.PathModifier.Path path = new org.andengine.entity.modifier.PathModifier.Path(2).to(coordinates[0], coordinates[2]).to(objCenterPos[0], objCenterPos[1]);
			sprite.registerEntityModifier((org.andengine.entity.modifier.IEntityModifier) new LoopEntityModifier(new PathModifier((float) 1, path)));
			
		    scene.attachChild((IEntity) sprite);

		    return sprite;
		} 
		
		private PixelPerfectAnimatedSprite addSheepSprite(final Scene scene, final int x, final int y, final PixelPerfectTiledTextureRegion region){

			PixelPerfectAnimatedSprite sprite = new PixelPerfectAnimatedSprite(x,y,region, VBOmanager);
		    scene.attachChild((IEntity) sprite);
		    return sprite;
		    
		}
		
		
		private PixelPerfectAnimatedSprite addShieldSprite(final Scene scene, final float x, final float y, final PixelPerfectTiledTextureRegion region){
		   
			PixelPerfectAnimatedSprite sprite = new PixelPerfectAnimatedSprite(x,y,region, VBOmanager) ;

		    double deltaX = mSheep.getSceneCenterCoordinates()[0] - x;
		    double deltaY = y  - mSheep.getSceneCenterCoordinates()[1];
		    
		    Log(deltaX + "");
		    Log(deltaY + "");
		    float angle = (float) (Math.atan(deltaX / Math.abs(deltaY)) * 180 / Math.PI);
		   
		    if(deltaY < 0) angle = angle + 180;
		    if(deltaY > 0) angle = angle * -1;
		
		    sprite.setRotation(angle);
		    scene.attachChild((IEntity) sprite);
		    return sprite;
		}
}