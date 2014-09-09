package com.friendlysheep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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

			        	new AlertDialog.Builder(activity)
			            .setTitle("YO")
			            .setMessage("You are gay " + position)
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

	        i.setImageResource(R.drawable.water);
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
