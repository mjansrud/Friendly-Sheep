package com.friendlysheep;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
 

public class ActivityGame extends Activity{

	//Defining variables
	private boolean alive;
	private Button b_left; 
	private Button b_right;
	private Button b_top;
	private Button b_bottom;
	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
	
		activity = this;
		
		setLayout();
	}
	
	private void setLayout(){
		
		GridView grid = (GridView) findViewById(R.id.myGrid);
	    grid.setAdapter(new customAdapter());

	    grid.setOnItemClickListener(new OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

	        	new AlertDialog.Builder(activity)
	            .setTitle("YO")
	            .setMessage("You are gay" + position)
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
	
	
	private void playGame(){
		
		while(alive){
			
			
			
			
		}
		
	}
	

	public class customAdapter extends BaseAdapter {
	
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	//create a basic imageview here or inflate a complex layout with
	    	//getLayoutInflator().inflate(R.layout...)
		    ImageView i = new ImageView(activity);
	
	        i.setImageResource(R.drawable.water);
	        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
	        final int w = (int) (36 * getResources().getDisplayMetrics().density + 0.5f);
	        i.setLayoutParams(new GridView.LayoutParams(w * 2, w * 2));
	        return i;
	    }
	
	    public final int getCount() {
	        return 9;
	    }
	
	
	    public final long getItemId(int position) {
	        return position;
	    }

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	
}
