package com.friendlysheep;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;

public class ViewDrawPath extends View implements OnDragListener{
	
	private ObjectPoint objectPoint;
	private ArrayList<ObjectPoint> objectPoints;
	private Canvas  canvas;
	private Path path;
	private Paint paint;   
	
	public ViewDrawPath(Context context) {
		super(context);

		Log.i("ViewDrawPath", "Created");
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(6);

		objectPoints = new ArrayList<ObjectPoint>();
   	 	path = new Path();
		canvas = new Canvas();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		
		int x = (int)event.getX();
		int y = (int)event.getY();

		objectPoint = new ObjectPoint(x,y);
		
		// TODO Auto-generated method stub
	    switch (event.getAction()) {
	    case DragEvent.ACTION_DRAG_STARTED:
	    	 break;
	    case DragEvent.ACTION_DRAG_ENTERED:
    		 path.moveTo(x, y);
	 	     canvas.drawPath(path, paint);
	    	 break;
	    case DragEvent.ACTION_DRAG_EXITED:        
	    	break;
	    case DragEvent.ACTION_DROP:
	    	break;
	    case DragEvent.ACTION_DRAG_ENDED:
	    	break;
	    default:
	    	break;
	    }
	    return true;
	}
	
	@Override
	public void onDraw(Canvas canvas) {
	    boolean first = true;
	    for(ObjectPoint point : objectPoints){
	        if(first){
	            first = false;
	            path.moveTo(point.x, point.y);
	        }
	        else{
	            path.lineTo(point.x, point.y);
	        }
	    }
	    canvas.drawPath(path, paint);
	}

}
