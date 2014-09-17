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
    private BitmapTextureAtlas mDrawingTexture;
    private PixelPerfectTextureRegion mSheepRegion;
    private PixelPerfectTiledTextureRegion mBulletRegion;
    
    private BitmapTextureAtlas mFontTexture;
	private ArrayList<Shape> mBulletSprites;
	private ArrayList<Shape> mTargetSprites;

    private PixelPerfectSprite mSheep;
    private ViewDrawPath mViewDrawPath;
	private PixelPerfectTextureRegion mDrawingTextureRegion;
    private IBitmapTextureAtlasSource mDrawingTextureSource;
	private IBitmapTextureAtlasSource decoratedTextureAtlasSource;

	private float mStartX = 0;
    private float mStartY = 0;
	private float mLastX = 0;
	private float mLastY = 0;
	private float mCurrentX = 0;
	private float mCurrentY = 0;
	private Line mCurrentLine;
	private boolean mDrawing;
	
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
	    mDrawingTexture = new BitmapTextureAtlas(2048, 2048, TextureOptions.DEFAULT);
	    mBulletTexture = new BitmapTextureAtlas(2048, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    mSheepTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	   
	    //create regions and fetch bitmaps - add to texture
	    mBulletRegion = PixelPerfectTextureRegionFactory.createTiledFromAsset(mBulletTexture, this, "spinning-triangle.png", 0, 0, 20, 1);
	    mSheepRegion = PixelPerfectTextureRegionFactory.createFromAsset(mSheepTexture, this, "sheep.png", 0, 0);
	    
	    //drawing source
    	mDrawingTextureSource = new EmptyBitmapTextureAtlasSource(mDisplayWidth, mDisplayHeight);
    	mDrawingTextureSource.onLoadBitmap(Bitmap.Config.ALPHA_8);
    	
	    mEngine.getTextureManager().loadTextures(mBulletTexture, mSheepTexture, mDrawingTexture);
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
		                    if(target != null && bullet.collidesWith(target)){
		                    	if(target!=mSheep){
		                    		score++;
		                    		scoreText.setText("Score: " + score + "");		                    		
		                    	}
		                    	else{
		                    		scoreText.setText("GAME OVER!");
		                    		alive = false;
		                    	}
		                    	Log("Removed item");
		                        collisionText.setText("bam!");
		                        bullet.detachSelf();
		                        mBulletSprites.remove(bullet);
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
	            mStartX = pSceneTouchEvent.getX();
	            mStartY = pSceneTouchEvent.getY();
	            mCurrentLine = new Line(mStartX, mStartY, mStartX,mStartY);
	            mCurrentLine.setLineWidth(15);
	            mCurrentLine.setColor(0, 0, 0, 0.4f);
                mScene.attachChild(mCurrentLine);	 
	        break;
	        case MotionEvent.ACTION_MOVE: 
	    		Log("Move");
    			mDrawing = true;
	    		mCurrentX = pSceneTouchEvent.getX();
	            mCurrentY = pSceneTouchEvent.getY();
    			mCurrentLine.setPosition(mStartX, mStartY, mCurrentX, mCurrentY);
	        break;
	        case MotionEvent.ACTION_UP:
	    		Log("Up");
	    		mCurrentLine.detachSelf();
	            mLastX = pSceneTouchEvent.getX();
	            mLastY = pSceneTouchEvent.getY();
	            
	            //define final line
	            final Line insertLine = new Line(mStartX, mStartY, mLastX, mLastY);
	            insertLine.setLineWidth(50);
	            insertLine.setColor(0.0f, 0.0f, 0.0f, 1.0f);

                mScene.attachChild(insertLine);	  
                mTargetSprites.add(insertLine);  
	            
            	final Handler handler = new Handler();
    		    handler.postDelayed(new Runnable() { 
    		    	@Override
    		    	public void run() {
    		    		insertLine.detachSelf();
    	                mTargetSprites.remove(insertLine);  
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
	
	public class ViewDrawPath extends View {

        public int width;
        public  int height;
        private ArrayList<Path> mPaths = new ArrayList<Path>();
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
	        
		    mPaint = new Paint();
		    mPaint.setAntiAlias(true);
		    mPaint.setDither(true);
		    mPaint.setColor(Color.BLACK);
		    mPaint.setStyle(Paint.Style.STROKE);
		    mPaint.setStrokeJoin(Paint.Join.ROUND);
		    mPaint.setStrokeCap(Paint.Cap.ROUND);
		    mPaint.setStrokeWidth(12);  

		    mPaintTransparent = new Paint();
		    mPaintTransparent.setAntiAlias(true);
		    mPaintTransparent.setDither(true);
		    mPaintTransparent.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		    mPaintTransparent.setStrokeWidth(14);  
		    mPaintTransparent.setStrokeJoin(Paint.Join.ROUND);
		    mPaintTransparent.setStrokeCap(Paint.Cap.ROUND);
		    mPaintTransparent.setStyle(Paint.Style.STROKE);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
				mBitmap = Bitmap.createBitmap(mDisplayWidth, mDisplayHeight, Bitmap.Config.ALPHA_8);
        		mCanvas = new Canvas(mBitmap);
        }
        @Override
        protected void onDraw(Canvas canvas) {
        	super.onDraw(canvas);
	        if(mBitmap != null){
			    canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
				canvas.drawPath( mPath,  mPaint);
			    canvas.drawPath( circlePath,  circlePaint);
	        }
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
		        mCanvas.drawPath(mPath,  mPaint);
	            circlePath.reset();
	            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
	        }
        }
        private void touch_up() {
        	
	        circlePath.reset();
	        mCanvas.drawPath(mPath,  mPaint);

	   		mPaths.add(new Path(mPath));
	        mPath.reset();
		    startTimerForRemove();
		    //addDrawing();
		    
        }
        
        
        private void addDrawing(){

    		decoratedTextureAtlasSource = new BaseBitmapTextureAtlasSourceDecorator(mDrawingTextureSource) {
    			@Override
    			protected void onDecorateBitmap(Canvas pCanvas) {
    				pCanvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
    			}
    			
				@Override
				public BaseBitmapTextureAtlasSourceDecorator clone() {
					// TODO Auto-generated method stub
					return null;
				}  
    		};
        	decoratedTextureAtlasSource.onLoadBitmap(Bitmap.Config.ALPHA_8);
    		mDrawingTextureRegion = PixelPerfectTextureRegionFactory.createFromSource(mDrawingTexture, decoratedTextureAtlasSource, 0, 0);
        	mTargetSprites.add(addDrawingSprite(mScene, mDrawingTextureRegion));
        	
        }
        
        private void startTimerForRemove(){
        	final Handler handler = new Handler();
		    handler.postDelayed(new Runnable() { 
		    	@Override
		    	public void run() {
			    	//mCanvas.drawPath( mPaths.remove(0),  mPaintTransparent);
		        	//mTargetSprites.remove(1);
			    	invalidate();
		    		Log.i("DELAY", "-------------------------");
		    	}
		    }, 7000);
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
	    	        PathMeasure mPm = new PathMeasure(mPath, true);
	            	if(mPm.getLength() <= 150){
	            		touch_move(x, y); 
	            	}
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
	
	private PixelPerfectSprite addDrawingSprite(final Scene scene, final PixelPerfectTextureRegion region){
	    PixelPerfectSprite sprite = new PixelPerfectSprite(0,0,region);
	    scene.attachChild(sprite);
	    return sprite;
	}
	
	@Override
	public void onLoadComplete() {

	    //run game
		gameRunning();
		
	}

}
