package com.friendlysheep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
 

public class ActivityGame extends Activity{

	//Defining variables
	private boolean alive;
	private int rows = 9;
	private int columns = 9;
	private int parentWidth;
	private int parentHeight;
	private GridView grid;
	
	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	
		activity = this;

		setLayout();
		
		setContentView(new CustomView(activity));
	}
	
	private void setLayout(){
		
		grid = (GridView) findViewById(R.id.myGrid);
		grid.post(new Runnable() { 
		public void run(){
			
		        Rect rect = new Rect();
		        Window win = getWindow();
		        win.getDecorView().getWindowVisibleDisplayFrame(rect);
		        int statusHeight = rect.top;
		        int contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT).getTop();
		        int titleHeight = contentViewTop - statusHeight;
		        
		        Display display = getWindowManager().getDefaultDisplay();
		        Point size = new Point();
		        display.getSize(size);

				parentWidth = size.x;
				parentHeight = size.y - statusHeight - titleHeight;
				
				grid.setAdapter(new customAdapter());
			    grid.setOnItemClickListener(new OnItemClickListener() {
			        @Override
			        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

			        	String button;
			        	int i = 0;
			        	
			        	if(position<=26) i = 0;
			        	else if(position == 58) i = 4;
			        	else if(position >= 63) i = 3;
			        	else if(position == 27 || 35 < position && position < 38 || 44 < position && position < 48 || 53 < position && position < 58) i = 1;
			        	else if(position == 35 || 42 < position && position < 45 || 50 < position && position < 54 || 58 < position && position < 63) i = 2;
			        	else i=0;
			        	
			        	switch (i){ 
				        	case 0:
				        		button = "Top";
				        		break;
				        	case 1:
				        		button = "Left";
				        		break;
				        	case 2:
				        		button = "Right"; 
				        		break;
				        	case 3:
				        		button = "Bottom";
				        		break;
				        	case 4:
				        		button = "BAAEE";
				        		break;
			        		default:
				        		button = "Error";
			        			break;
			        	}
			        	
			        	
			        	new AlertDialog.Builder(activity)
			            .setTitle("YO")
			            .setMessage("You are gay " + position + ", button: " + button)
			            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int which) { 
			                    // continue with delete
			                }
			             })
			            .setIcon(android.R.drawable.ic_dialog_alert)
			             .show();
			        		
			        }
			    });
		}
		});
		

	}
	
	private void playGame(){
		while(alive){
			
			
		}
	}

	public class customAdapter extends BaseAdapter{
		
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	//create a basic imageview here or inflate a complex layout with
	    	//getLayoutInflator().inflate(R.layout...)
		    ImageView i = new ImageView(activity);

		    Log.i("YO", "" + position);
		    switch (position){
		    	case 58:
		    		i.setImageResource(R.drawable.sheep);
        			break;
		    	default:
		    		i.setImageResource(R.drawable.water);
        			break;
		    }
		    
	        i.setScaleType(ImageView.ScaleType.FIT_XY);

	        int width = parentWidth/columns ;
	        int height= parentHeight/rows ;
	        
	        i.setLayoutParams(new GridView.LayoutParams(width, height));
	        return i;
	    }
	
	    public final int getCount() {
	        return rows * columns;
	    }
	
	    public final long getItemId(int position) {
	        return position;
	    }

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	
	
	public class CustomView extends View {
	    private static final float DP_PER_SECONDS = 10;

	    private final float mBallCirfumference;
	    private final float mBallRadius;
	    private final Bitmap mBallBitmap;
	    private final Paint mBallBitmapPaint;
	    private final Matrix mBallTransformMatrix = new Matrix();
	    private final float mPxPerSecond;

	    private long mStartTime = -1;

	    public CustomView(Context context) {
	        super(context);
	        final Resources res = getResources();

	        // Load the ball bitmap. You probably want to use a better bitmap ;)
	        mBallBitmap = BitmapFactory.decodeResource(res, R.drawable.sheep);

	        // We need the radius and circumference of the ball for our calculations
	        // later
	        mBallRadius = mBallBitmap.getHeight() / 2;
	        mBallCirfumference = mBallRadius * 2 * (float)Math.PI;

	        // Create ourself a paint object so we can adjust the quality of the
	        // bitmap drawing
	        mBallBitmapPaint = new Paint();

	        // Significantly improves quality when drawing transformed bitmaps. Compare
	        // with when you disable this, which is the default
	        mBallBitmapPaint.setFilterBitmap(true);

	        // Calculate speed of ball in pixels
	        mPxPerSecond = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP_PER_SECONDS,
	                res.getDisplayMetrics());
	    }

	    @Override
	    protected void onDraw(Canvas canvas) {
	        super.onDraw(canvas);

	        // Calculate how far into the animation we are
	        if (mStartTime == -1) {
	            mStartTime = getDrawingTime();
	        }
	        long currentTime = getDrawingTime();
	        float secondsPassed = (currentTime - mStartTime) / 1000.0f;

	        // Calculate how far the ball has moved and how many degrees it has been
	        // rotated as a consequence of the movement
	        float movedDistance = secondsPassed * mPxPerSecond;
	        float fullRotationsMade = movedDistance / mBallCirfumference;
	        float rotationInDegrees = fullRotationsMade * 360;

	        // Setup the transformation matrix to simulate a rolling ball
	        mBallTransformMatrix.reset();
	        mBallTransformMatrix.postRotate(rotationInDegrees, mBallRadius, mBallRadius);
	        mBallTransformMatrix.postTranslate(movedDistance, 0);
	        canvas.drawBitmap(mBallBitmap, mBallTransformMatrix, mBallBitmapPaint);

	        // Force redraw so we get an animation
	        invalidate();
	    }
	}
}
