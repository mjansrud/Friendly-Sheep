package com.friendlysheep;

import java.util.ArrayList;
import java.util.Random;


import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.PathModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.qwerjk.andengine.entity.sprite.PixelPerfectAnimatedSprite;
import com.qwerjk.andengine.entity.sprite.PixelPerfectSprite;
import com.qwerjk.andengine.opengl.texture.region.PixelPerfectTextureRegion;
import com.qwerjk.andengine.opengl.texture.region.PixelPerfectTextureRegionFactory;
import com.qwerjk.andengine.opengl.texture.region.PixelPerfectTiledTextureRegion;
import com.qwerjk.pixelperfecttest.Logger;

import android.graphics.Color;
import android.graphics.Typeface;

import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class ActivityGame extends BaseGameActivity {
	
    // ===========================================================
    // Constants
    // ===========================================================

    private static int CAMERA_WIDTH;
    private static int CAMERA_HEIGHT;

    // ===========================================================
    // Fields
    // ===========================================================

    private Random r_anim;
	private Paint mPaint;
	private Paint mPaintTransparent;
	private int displayWidth;
	private int displayHeight;

    private Font mFont;
    private Scene mScene;
    private Camera mCamera;
    private BitmapTextureAtlas bulletTexture;
    private BitmapTextureAtlas sheepTexture;
    private BitmapTextureAtlas drawingTexture;
    private PixelPerfectTextureRegion sheepRegion;
    private PixelPerfectTextureRegion drawingRegion;
    private PixelPerfectTiledTextureRegion bulletRegion;
    
    private BitmapTextureAtlas mFontTexture;
	private ArrayList<Shape> mBulletSprites;
	private ArrayList<Shape> mTargetSprites;
	
    private PixelPerfectSprite sheep;
    private ViewDrawPath viewDrawPath;
	
	
	@Override
	public Engine onLoadEngine() {
		
	    //log game started
		Log("Engine loaded");
		r_anim = new Random();
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
	    return engine;
	}
	
	@Override
	public void onLoadResources() {
	    PixelPerfectTextureRegionFactory.setAssetBasePath("gfx/");
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
	
	    
	    this.mFontTexture = new BitmapTextureAtlas(256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	
	    this.mFont = new Font(this.mFontTexture, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48, true, Color.BLACK);
	    
	    this.mEngine.getTextureManager().loadTexture(this.mFontTexture);
	    this.mEngine.getFontManager().loadFont(this.mFont);
	
	    //create textures with minimum sizes
	    this.bulletTexture = new BitmapTextureAtlas(2048, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    this.sheepTexture = new BitmapTextureAtlas(2048, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
	    
	    //create regions and fetch bitmaps - add to texture
	    this.bulletRegion = PixelPerfectTextureRegionFactory.createTiledFromAsset(bulletTexture, this, "spinning-triangle.png", 0, 0, 20, 1);
	    this.sheepRegion = PixelPerfectTextureRegionFactory.createFromAsset(sheepTexture, this, "sheep.png", 0, 0);
	    
	    this.mEngine.getTextureManager().loadTextures(this.bulletTexture, this.sheepTexture);
	}
	
	@Override
	public Scene onLoadScene() {
	    this.mEngine.registerUpdateHandler(new FPSLogger());
		    
	    mScene = new Scene(1);
	    mScene.setBackground(new ColorBackground(0.09804f, 0.6274f, 0.8784f));
	
	    viewDrawPath = new ViewDrawPath(this);
	    addContentView(viewDrawPath, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	    
	    sheep  = addSprite(mScene, 250, 1000, sheepRegion);  
	    
	    mBulletSprites = new ArrayList<Shape>();
	    mTargetSprites = new ArrayList<Shape>();
	    mTargetSprites.add(sheep);
	    
	    final ChangeableText collisionText = new ChangeableText(0, 0, this.mFont, "no collisions");
	    mScene.attachChild(collisionText);
	    
	    /* The actual collision-checking. */
	    mScene.registerUpdateHandler(new IUpdateHandler() {
	        @Override
	        public void reset() { } 
	
	        @Override
	        public void onUpdate(final float pSecondsElapsed) {
	            for(Shape bullet : mBulletSprites){
	    	            for(Shape target : mTargetSprites){
		                    if(bullet.collidesWith(target)){
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
	
	public void getDisplayInfo(){
		
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		
		CAMERA_WIDTH = size.x;
		CAMERA_HEIGHT = size.y;
		
		displayWidth = size.x;
		displayHeight = size.y;

	}

	
	public void gameRunning(){
		
		newAnimation();
		
	}
	
	public void newAnimation(){
		
		final Handler h = new Handler();
		final int delay = r_anim.nextInt(5000 - 2000) + 2000; //milliseconds 

		h.postDelayed(new Runnable(){
		    public void run(){
		    	newSprite();
		        h.postDelayed(this, delay);
		    }
		}, delay);		
		
	}
	
	public void newSprite(){

		 mBulletSprites.add(addAnimatedSprite(mScene, -10, -10, 10, bulletRegion));
		 
	}
	
	public Integer[] RandomizeAnimation(){
		
		int randomY = 0;
		int randomX = 0;
		
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
		    mPaint.setColor(Color.WHITE);
		    mPaint.setStyle(Paint.Style.STROKE);
		    mPaint.setStrokeJoin(Paint.Join.ROUND);
		    mPaint.setStrokeCap(Paint.Cap.ROUND);
		    mPaint.setStrokeWidth(12);  

		    mPaintTransparent = new Paint();
		    mPaintTransparent.setAntiAlias(true);
		    mPaintTransparent.setDither(true); 
		    mPaintTransparent.setColor(Color.BLACK);
		    mPaintTransparent.setStyle(Paint.Style.STROKE);
		    mPaintTransparent.setStrokeJoin(Paint.Join.ROUND);
		    mPaintTransparent.setStrokeCap(Paint.Cap.ROUND);
		    mPaintTransparent.setStrokeWidth(14);  
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	        
        		mBitmap = Bitmap.createBitmap(displayWidth, displayHeight, Bitmap.Config.ARGB_8888);
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
	        
	        final Handler handler = new Handler();
		    handler.postDelayed(new Runnable() {
		    	@Override
		    	public void run() {
			    	mCanvas.drawPath( mPaths.remove(0),  mPaintTransparent);
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
		objCenterPos = sheep.getSceneCenterCoordinates();
		org.anddev.andengine.entity.modifier.PathModifier.Path path = new org.anddev.andengine.entity.modifier.PathModifier.Path(2).to(coordinates[0], coordinates[2]).to(objCenterPos[0], objCenterPos[1]);
		sprite.registerEntityModifier(new LoopEntityModifier(new PathModifier(3, path)));
		
	    return sprite;
	} 
	
	private PixelPerfectSprite addSprite(final Scene scene, final int x, final int y, final PixelPerfectTextureRegion region){
	    PixelPerfectSprite sprite = new PixelPerfectSprite(x,y,region){
	        boolean mGrabbed = false;
	        
	        @Override
	        public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
	            switch(pSceneTouchEvent.getAction()) {
	                case TouchEvent.ACTION_DOWN:
	                    this.mGrabbed = true;
	                    break;
	                case TouchEvent.ACTION_MOVE:
	                    if(this.mGrabbed) {
	                        this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
	                    }
	                    break;
	                case TouchEvent.ACTION_UP:
	                    if(this.mGrabbed) {
	                        this.mGrabbed = false;
	                    }
	                    break;
	            }
	            return true;
	        }
	    };
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
