package com.dangle1107.overlayimage;

import android.support.v7.app.ActionBarActivity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
	TextView textSource;
	ImageView imageFront, imageDrawingPane, imageOverlaid;
	projectPt startPt, endPt;
	Bitmap bitmapMaster;
	Bitmap bitmapDrawingPane;
	Canvas canvasDrawingPane;
	Canvas canvasMaster;
	final int RQS_IMAGE1 = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textSource = (TextView)findViewById(R.id.sourceuri);
        imageFront = (ImageView)findViewById(R.id.imageFront);
        imageOverlaid = (ImageView)findViewById(R.id.imageOverlaid);
        imageDrawingPane = (ImageView)findViewById(R.id.drawingpane);
        
        textSource.setText("Start");
        
        BitmapInitialize();
        
        imageFront.setOnTouchListener(new OnTouchListener(){

     	   @Override
     	   public boolean onTouch(View v, MotionEvent event) {
     	    
     	    int action = event.getAction();
     	    int x = (int) event.getX();
     	    int y = (int) event.getY();
     	    switch(action){
     	    case MotionEvent.ACTION_DOWN:
     	     textSource.setText("ACTION_DOWN- " + x + " : " + y);
     	     startPt = projectXY((ImageView)v, bitmapMaster, x, y);
     	     break;
     	    case MotionEvent.ACTION_MOVE:
     	     textSource.setText("ACTION_MOVE- " + x + " : " + y);
     	     drawOnRectProjectedBitMap((ImageView)v, bitmapMaster, x, y);
     	     break;
     	    case MotionEvent.ACTION_UP:
     	     textSource.setText("ACTION_UP- " + x + " : " + y);
     	     endPt = projectXY((ImageView)v, bitmapMaster, x, y);
     	     drawOnRectProjectedBitMap((ImageView)v, bitmapMaster, x, y);
     	     finalizeDrawing();
     	     break;
     	    }
     	    /*
     	     * Return 'true' to indicate that the event have been consumed.
     	     * If auto-generated 'false', your code can detect ACTION_DOWN only,
     	     * cannot detect ACTION_MOVE and ACTION_UP.
     	     */
     	    return true;
     	   }});    	   	
    }


    
    public void selectOverlaidArea(View v){
    	
    }
    
    
	class projectPt{
		  int x;
		  int y;
		  
		  projectPt(int tx, int ty){
		   x = tx;
		   y = ty;
		  }
	}
	
	 private projectPt projectXY(ImageView iv, Bitmap bm, int x, int y){
		  if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
		   //outside ImageView
		   return null;
		  }else{
		   int projectedX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
		   int projectedY = (int)((double)y * ((double)bm.getHeight()/(double)iv.getHeight()));

		   return new projectPt(projectedX, projectedY);
		  }
		 }
	 
	 private void drawOnRectProjectedBitMap(ImageView iv, Bitmap bm, int x, int y){
		  if(x<0 || y<0 || x > iv.getWidth() || y > iv.getHeight()){
		   //outside ImageView
		   return;
		  }else{
		   int projectedX = (int)((double)x * ((double)bm.getWidth()/(double)iv.getWidth()));
		   int projectedY = (int)((double)y * ((double)bm.getHeight()/(double)iv.getHeight()));

		   //clear canvasDrawingPane
		   canvasDrawingPane.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		   
		   Paint paint = new Paint();
		            paint.setStyle(Paint.Style.STROKE);
		            paint.setColor(Color.WHITE);
		            paint.setStrokeWidth(3);
		   canvasDrawingPane.drawRect(startPt.x, startPt.y, projectedX, projectedY, paint);
		   imageDrawingPane.invalidate();
		   
		   
		   textSource.setText(x + ":" + y + "/" + iv.getWidth() + " : " + iv.getHeight() + "\n" +
		     projectedX + " : " + projectedY + "/" + bm.getWidth() + " : " + bm.getHeight()
		     );
		  }
		 }
	 
	 private void finalizeDrawing(){
		 BitmapInitialize();
		  canvasMaster.drawBitmap(bitmapDrawingPane, 0, 0, null);
		  int w = (int)Math.abs(endPt.x - startPt.x);
		  int h = (int)Math.abs(endPt.y - startPt.y);
		   Bitmap tempBitmap;		   		    	
		     Bitmap bm = BitmapFactory.decodeResource(
		    		 getResources(), R.drawable.front);
		     tempBitmap = Bitmap.createBitmap(bm, startPt.x, startPt.y, w, h);		     		     		   
		     imageOverlaid.setImageBitmap(tempBitmap); 
		 }	 
	 
	 public void BitmapInitialize(){
		 Bitmap tempBitmap;
		  
		     //tempBitmap is Immutable bitmap,
		     //cannot be passed to Canvas constructor		    	
		     tempBitmap = BitmapFactory.decodeResource(
		    		 getResources(), R.drawable.front);
		     
		     Config config;
		     if(tempBitmap.getConfig() != null){
		      config = tempBitmap.getConfig();
		     }else{
		      config = Config.ARGB_8888;
		     }
		     
		     //bitmapMaster is Mutable bitmap
		     bitmapMaster = Bitmap.createBitmap(
		       tempBitmap.getWidth(),
		       tempBitmap.getHeight(),
		       config);
		     
		     canvasMaster = new Canvas(bitmapMaster);
		     canvasMaster.drawBitmap(tempBitmap, 0, 0, null);
		     
		     //imageFront.setImageBitmap(bitmapMaster);
		     
		     //Create bitmap of same size for drawing
		     bitmapDrawingPane = Bitmap.createBitmap(
		       tempBitmap.getWidth(),
		       tempBitmap.getHeight(),
		       config);
		     canvasDrawingPane = new Canvas(bitmapDrawingPane);
		     imageDrawingPane.setImageBitmap(bitmapDrawingPane);		     		     		  		   
	 }
	 
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }

	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        int id = item.getItemId();
	        if (id == R.id.action_settings) {
	            return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }
	    
}


