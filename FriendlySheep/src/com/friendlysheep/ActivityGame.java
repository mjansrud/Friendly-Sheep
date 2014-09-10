package com.friendlysheep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
	private AlertDialog dialog;
	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	
		activity = this;

		dialog = new AlertDialog.Builder(activity)
        .setTitle("Get ready")
        .setMessage("3")
        .setIcon(android.R.drawable.ic_dialog_info)
        .show();
		
		new CountDownTimer(3000, 1000) {

			 public void onTick(long millisUntilFinished) {
				 dialog.setMessage("" + (millisUntilFinished / 1000));
			  //here you can have your logic to set text to edittext 
			 }
 
			 public void onFinish() {
				 dialog.setMessage("0"); 
				 dialog.hide();
			 }
		}
	    .start();		
		
		setLayout();
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
		    		i.setImageResource(R.buttons.transparent_view);
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
	
}
